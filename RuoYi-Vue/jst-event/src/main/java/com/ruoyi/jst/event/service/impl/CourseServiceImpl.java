package com.ruoyi.jst.event.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.audit.OperateLog;
import com.ruoyi.jst.common.cache.JstCacheService;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.event.domain.JstCourse;
import com.ruoyi.jst.event.dto.CourseQueryReqDTO;
import com.ruoyi.jst.event.dto.CourseSaveReqDTO;
import com.ruoyi.jst.event.dto.WxCourseQueryDTO;
import com.ruoyi.jst.event.enums.CourseAuditStatus;
import com.ruoyi.jst.event.mapper.CourseMapperExt;
import com.ruoyi.jst.event.service.CourseService;
import com.ruoyi.jst.event.vo.CourseDetailVO;
import com.ruoyi.jst.event.vo.CourseListVO;
import com.ruoyi.jst.event.vo.MyCourseVO;
import com.ruoyi.jst.event.vo.WxCourseCardVO;
import com.ruoyi.jst.event.vo.WxCourseDetailVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 课程领域服务实现。
 * <p>
 * 负责课程 CRUD 审核流转、上架/下架以及小程序侧列表/详情/我的课程查询。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class CourseServiceImpl implements CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);

    private static final String COURSE_STATUS_ON = "on";
    private static final String COURSE_STATUS_OFF = "off";

    @Autowired
    private CourseMapperExt courseMapperExt;

    @Autowired
    private JstCacheService jstCacheService;

    /**
     * 新增课程。
     *
     * @param req 新增入参
     * @return 新增课程ID
     * @关联表 jst_course
     * @关联状态机 SM-21
     * @关联权限 jst:event:course:add
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "课程", action = "ADD", target = "#{req.courseName}")
    public Long addCourse(CourseSaveReqDTO req) {
        // TX: CourseService.addCourse
        Date now = DateUtils.getNowDate();
        String operatorName = currentOperatorName();

        JstCourse course = buildCourse(req);
        course.setAuditStatus(CourseAuditStatus.DRAFT.dbValue());
        course.setStatus(COURSE_STATUS_OFF);
        course.setCreateBy(operatorName);
        course.setCreateTime(now);
        course.setUpdateBy(operatorName);
        course.setUpdateTime(now);
        course.setDelFlag("0");

        int inserted = courseMapperExt.insertCourse(course);
        if (inserted <= 0 || course.getCourseId() == null) {
            throw new ServiceException("新增课程失败", BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        log.info("[CourseAdd] 新增课程成功 courseId={} courseName={}", course.getCourseId(), course.getCourseName());
        evictCourseCache();
        return course.getCourseId();
    }

    /**
     * 编辑课程。
     *
     * @param req 编辑入参
     * @关联表 jst_course
     * @关联状态机 SM-21
     * @关联权限 jst:event:course:edit
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "课程", action = "EDIT", target = "#{req.courseId}")
    public void editCourse(CourseSaveReqDTO req) {
        // TX: CourseService.editCourse
        if (req.getCourseId() == null) {
            throw new ServiceException("课程ID不能为空", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }

        JstCourse existing = requireCourse(req.getCourseId());
        CourseAuditStatus auditStatus = CourseAuditStatus.fromDb(existing.getAuditStatus());
        if (auditStatus == null || !auditStatus.canEdit()) {
            throw new ServiceException("当前审核状态不允许编辑课程", BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }

        JstCourse update = buildCourse(req);
        update.setCourseId(existing.getCourseId());
        update.setUpdateBy(currentOperatorName());
        update.setUpdateTime(DateUtils.getNowDate());
        int updated = courseMapperExt.updateCourseBase(update);
        if (updated == 0) {
            throw new ServiceException("编辑课程失败，课程可能已删除", BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        log.info("[CourseEdit] 编辑课程成功 courseId={}", existing.getCourseId());
        evictCourseCache();
    }

    /**
     * 提审课程。
     *
     * @param courseId 课程ID
     * @关联表 jst_course
     * @关联状态机 SM-21
     * @关联权限 jst:event:course:audit
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "课程", action = "SUBMIT", target = "#{courseId}")
    public void submitCourse(Long courseId) {
        // TX: CourseService.submitCourse
        JstCourse course = requireCourse(courseId);
        // SM-21
        CourseAuditStatus.fromDb(course.getAuditStatus()).assertCanTransitTo(CourseAuditStatus.PENDING);
        updateAuditStatus(courseId, course.getAuditStatus(), CourseAuditStatus.PENDING.dbValue(), "课程提审失败，状态已变化");
        log.info("[CourseSubmit] 课程提审成功 courseId={}", courseId);
        evictCourseCache();
    }

    /**
     * 审核通过课程。
     *
     * @param courseId 课程ID
     * @关联表 jst_course
     * @关联状态机 SM-21
     * @关联权限 jst:event:course:audit
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "课程", action = "AUDIT_APPROVE", target = "#{courseId}")
    public void approveCourse(Long courseId) {
        // TX: CourseService.approveCourse
        JstCourse course = requireCourse(courseId);
        // SM-21
        CourseAuditStatus.fromDb(course.getAuditStatus()).assertCanTransitTo(CourseAuditStatus.APPROVED);
        updateAuditStatus(courseId, course.getAuditStatus(), CourseAuditStatus.APPROVED.dbValue(), "课程审核通过失败，状态已变化");
        log.info("[CourseApprove] 课程审核通过 courseId={}", courseId);
        evictCourseCache();
    }

    /**
     * 审核驳回课程。
     *
     * @param courseId 课程ID
     * @关联表 jst_course
     * @关联状态机 SM-21
     * @关联权限 jst:event:course:audit
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "课程", action = "AUDIT_REJECT", target = "#{courseId}")
    public void rejectCourse(Long courseId) {
        // TX: CourseService.rejectCourse
        JstCourse course = requireCourse(courseId);
        // SM-21
        CourseAuditStatus.fromDb(course.getAuditStatus()).assertCanTransitTo(CourseAuditStatus.REJECTED);
        updateAuditStatus(courseId, course.getAuditStatus(), CourseAuditStatus.REJECTED.dbValue(), "课程审核驳回失败，状态已变化");
        log.info("[CourseReject] 课程审核驳回 courseId={}", courseId);
        evictCourseCache();
    }

    /**
     * 上架课程。
     *
     * @param courseId 课程ID
     * @关联表 jst_course
     * @关联权限 jst:event:course:on
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "课程", action = "ON", target = "#{courseId}")
    public void onCourse(Long courseId) {
        // TX: CourseService.onCourse
        JstCourse course = requireCourse(courseId);
        if (!Objects.equals(CourseAuditStatus.APPROVED.dbValue(), course.getAuditStatus())) {
            throw new ServiceException("课程未审核通过，不能上架", BizErrorCode.JST_EVENT_COURSE_NOT_ON.code());
        }
        if (Objects.equals(COURSE_STATUS_ON, course.getStatus())) {
            return;
        }
        if (!Objects.equals(COURSE_STATUS_OFF, course.getStatus())) {
            throw new ServiceException("课程上下架状态非法", BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        int updated = courseMapperExt.updateShelfStatus(courseId, COURSE_STATUS_OFF, COURSE_STATUS_ON, currentOperatorName());
        if (updated == 0) {
            throw new ServiceException("课程上架失败，状态已变化", BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        log.info("[CourseOn] 课程上架成功 courseId={}", courseId);
        evictCourseCache();
    }

    /**
     * 下架课程。
     *
     * @param courseId 课程ID
     * @关联表 jst_course
     * @关联权限 jst:event:course:off
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "课程", action = "OFF", target = "#{courseId}")
    public void offCourse(Long courseId) {
        // TX: CourseService.offCourse
        JstCourse course = requireCourse(courseId);
        if (Objects.equals(COURSE_STATUS_OFF, course.getStatus())) {
            return;
        }
        if (!Objects.equals(COURSE_STATUS_ON, course.getStatus())) {
            throw new ServiceException("课程上下架状态非法", BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        int updated = courseMapperExt.updateShelfStatus(courseId, COURSE_STATUS_ON, COURSE_STATUS_OFF, currentOperatorName());
        if (updated == 0) {
            throw new ServiceException("课程下架失败，状态已变化", BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        log.info("[CourseOff] 课程下架成功 courseId={}", courseId);
        evictCourseCache();
    }

    /**
     * 管理端分页查询课程列表。
     *
     * @param query 查询条件
     * @return 课程列表
     * @关联表 jst_course
     * @关联权限 jst:event:course:list
     */
    @Override
    public List<CourseListVO> selectAdminList(CourseQueryReqDTO query) {
        return courseMapperExt.selectAdminList(query);
    }

    /**
     * 管理端查询课程详情。
     *
     * @param courseId 课程ID
     * @return 课程详情
     * @关联表 jst_course
     * @关联权限 jst:event:course:query
     */
    @Override
    public CourseDetailVO getAdminDetail(Long courseId) {
        CourseDetailVO detail = courseMapperExt.selectAdminDetail(courseId);
        if (detail == null) {
            throw new ServiceException(BizErrorCode.JST_EVENT_COURSE_NOT_FOUND.message(),
                    BizErrorCode.JST_EVENT_COURSE_NOT_FOUND.code());
        }
        return detail;
    }

    /**
     * 小程序端公开课程列表（缓存 5 分钟）。
     *
     * @param query 查询条件
     * @return 课程卡片列表
     * @关联表 jst_course
     * @关联权限 @Anonymous
     */
    @Override
    public List<WxCourseCardVO> selectWxList(WxCourseQueryDTO query) {
        int pn = query.getPageNum() != null ? query.getPageNum() : 1;
        String key = "cache:course:list:" + safeKeyPart(query.getCourseType()) + ":" + pn;
        return jstCacheService.getOrLoadPage(key, 300, () -> courseMapperExt.selectWxList(query));
    }

    /**
     * 小程序端公开课程详情（缓存 10 分钟）。
     *
     * @param courseId 课程ID
     * @return 课程详情
     * @关联表 jst_course
     * @关联权限 @Anonymous
     */
    @Override
    public WxCourseDetailVO getWxDetail(Long courseId) {
        String key = "cache:course:detail:" + courseId;
        return jstCacheService.getOrLoad(key, 600, () -> {
            JstCourse course = requireCourse(courseId);
            if (!Objects.equals(CourseAuditStatus.APPROVED.dbValue(), course.getAuditStatus())
                    || !Objects.equals(COURSE_STATUS_ON, course.getStatus())) {
                throw new ServiceException(BizErrorCode.JST_EVENT_COURSE_NOT_ON.message(),
                        BizErrorCode.JST_EVENT_COURSE_NOT_ON.code());
            }
            WxCourseDetailVO detail = courseMapperExt.selectWxDetail(courseId);
            if (detail == null) {
                throw new ServiceException(BizErrorCode.JST_EVENT_COURSE_NOT_FOUND.message(),
                        BizErrorCode.JST_EVENT_COURSE_NOT_FOUND.code());
            }
            return detail;
        });
    }

    /**
     * 查询我的课程列表。
     *
     * @param userId 当前用户ID
     * @return 我的课程列表
     * @关联表 jst_course / jst_course_learn_record
     * @关联权限 hasRole('jst_student')
     */
    @Override
    public List<MyCourseVO> selectMyCourses(Long userId) {
        if (userId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        return courseMapperExt.selectMyCourseList(userId);
    }

    private JstCourse buildCourse(CourseSaveReqDTO req) {
        JstCourse course = new JstCourse();
        course.setCourseName(req.getCourseName());
        course.setCourseType(req.getCourseType());
        course.setCoverImage(req.getCoverImage());
        course.setDescription(req.getDescription());
        course.setPrice(req.getPrice());
        course.setPointsPrice(req.getPointsPrice());
        course.setLessonCount(req.getLessonCount());
        course.setLearnerCount(req.getLearnerCount());
        course.setTotalDuration(req.getTotalDuration());
        course.setChaptersJson(req.getChaptersJson());
        course.setTeacherName(req.getTeacherName());
        course.setTeacherAvatar(req.getTeacherAvatar());
        course.setTeacherDesc(req.getTeacherDesc());
        course.setCreatorType(req.getCreatorType());
        course.setCreatorId(req.getCreatorId());
        course.setMaicSourceId(req.getMaicSourceId());
        course.setVisibleScope(req.getVisibleScope());
        course.setRemark(req.getRemark());
        return course;
    }

    private JstCourse requireCourse(Long courseId) {
        JstCourse course = courseMapperExt.selectCourseById(courseId);
        if (course == null) {
            throw new ServiceException(BizErrorCode.JST_EVENT_COURSE_NOT_FOUND.message(),
                    BizErrorCode.JST_EVENT_COURSE_NOT_FOUND.code());
        }
        return course;
    }

    private void updateAuditStatus(Long courseId, String expectedStatus, String targetStatus, String failureMsg) {
        int updated = courseMapperExt.updateAuditStatus(courseId, expectedStatus, targetStatus, currentOperatorName());
        if (updated == 0) {
            throw new ServiceException(failureMsg, BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
    }

    private String currentOperatorName() {
        String username = SecurityUtils.getUsername();
        return StringUtils.isBlank(username) ? "system" : username;
    }

    /**
     * 课程写操作后清除缓存（列表 + 详情 + 首页推荐课程）。
     */
    private void evictCourseCache() {
        jstCacheService.evictByPrefix("cache:course:");
        jstCacheService.evictByPrefix("cache:home:");
    }

    private String safeKeyPart(String value) {
        return StringUtils.isBlank(value) ? "_all" : value;
    }
}
