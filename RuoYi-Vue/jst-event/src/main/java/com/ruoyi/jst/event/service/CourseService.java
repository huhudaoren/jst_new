package com.ruoyi.jst.event.service;

import com.ruoyi.jst.event.dto.CourseQueryReqDTO;
import com.ruoyi.jst.event.dto.CourseSaveReqDTO;
import com.ruoyi.jst.event.dto.WxCourseQueryDTO;
import com.ruoyi.jst.event.vo.CourseDetailVO;
import com.ruoyi.jst.event.vo.CourseListVO;
import com.ruoyi.jst.event.vo.MyCourseVO;
import com.ruoyi.jst.event.vo.WxCourseCardVO;
import com.ruoyi.jst.event.vo.WxCourseDetailVO;

import java.util.List;

/**
 * 课程领域服务。
 *
 * @author jst
 * @since 1.0.0
 */
public interface CourseService {

    /**
     * 新增课程，默认保存为 draft + off。
     *
     * @param req 新增入参
     * @return 新建课程ID
     * @关联表 jst_course
     * @关联状态机 SM-21
     * @关联权限 jst:event:course:add
     */
    Long addCourse(CourseSaveReqDTO req);

    /**
     * 编辑课程基础信息，仅 draft/rejected 可改。
     *
     * @param req 编辑入参
     * @关联表 jst_course
     * @关联状态机 SM-21
     * @关联权限 jst:event:course:edit
     */
    void editCourse(CourseSaveReqDTO req);

    /**
     * 提审课程。
     *
     * @param courseId 课程ID
     * @关联表 jst_course
     * @关联状态机 SM-21
     * @关联权限 jst:event:course:audit
     */
    void submitCourse(Long courseId);

    /**
     * 审核通过课程。
     *
     * @param courseId 课程ID
     * @关联表 jst_course
     * @关联状态机 SM-21
     * @关联权限 jst:event:course:audit
     */
    void approveCourse(Long courseId);

    /**
     * 审核驳回课程。
     *
     * @param courseId 课程ID
     * @关联表 jst_course
     * @关联状态机 SM-21
     * @关联权限 jst:event:course:audit
     */
    void rejectCourse(Long courseId);

    /**
     * 上架课程。
     *
     * @param courseId 课程ID
     * @关联表 jst_course
     * @关联权限 jst:event:course:on
     */
    void onCourse(Long courseId);

    /**
     * 下架课程。
     *
     * @param courseId 课程ID
     * @关联表 jst_course
     * @关联权限 jst:event:course:off
     */
    void offCourse(Long courseId);

    /**
     * 管理端课程列表。
     *
     * @param query 查询条件
     * @return 课程列表
     * @关联表 jst_course
     * @关联权限 jst:event:course:list
     */
    List<CourseListVO> selectAdminList(CourseQueryReqDTO query);

    /**
     * 管理端课程详情。
     *
     * @param courseId 课程ID
     * @return 课程详情
     * @关联表 jst_course
     * @关联权限 jst:event:course:query
     */
    CourseDetailVO getAdminDetail(Long courseId);

    /**
     * 小程序端公开课程列表。
     *
     * @param query 查询条件
     * @return 课程卡片列表
     * @关联表 jst_course
     * @关联权限 @Anonymous
     */
    List<WxCourseCardVO> selectWxList(WxCourseQueryDTO query);

    /**
     * 小程序端公开课程详情。
     *
     * @param courseId 课程ID
     * @return 课程详情
     * @关联表 jst_course
     * @关联权限 @Anonymous
     */
    WxCourseDetailVO getWxDetail(Long courseId);

    /**
     * 查询我的课程列表。
     *
     * @param userId 当前用户ID
     * @return 我的课程列表
     * @关联表 jst_course / jst_course_learn_record
     * @关联权限 hasRole('jst_student')
     */
    List<MyCourseVO> selectMyCourses(Long userId);
}
