package com.ruoyi.jst.event.mapper;

import com.ruoyi.jst.event.domain.JstCourse;
import com.ruoyi.jst.event.dto.CourseQueryReqDTO;
import com.ruoyi.jst.event.dto.WxCourseQueryDTO;
import com.ruoyi.jst.event.vo.CourseDetailVO;
import com.ruoyi.jst.event.vo.CourseListVO;
import com.ruoyi.jst.event.vo.MyCourseVO;
import com.ruoyi.jst.event.vo.WxCourseCardVO;
import com.ruoyi.jst.event.vo.WxCourseDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程领域扩展 Mapper。
 * <p>
 * 负责课程列表/详情查询与状态推进 SQL。
 *
 * @author jst
 * @since 1.0.0
 */
@Mapper
public interface CourseMapperExt {

    /**
     * 新增课程。
     *
     * @param course 课程实体
     * @return 影响行数
     */
    int insertCourse(JstCourse course);

    /**
     * 按主键查询课程实体。
     *
     * @param courseId 课程ID
     * @return 课程实体
     */
    JstCourse selectCourseById(@Param("courseId") Long courseId);

    /**
     * 更新课程基础信息。
     *
     * @param course 课程实体
     * @return 影响行数
     */
    int updateCourseBase(JstCourse course);

    /**
     * 按预期审核状态推进课程审核状态。
     *
     * @param courseId            课程ID
     * @param expectedAuditStatus 预期旧审核状态
     * @param targetAuditStatus   新审核状态
     * @param updateBy            更新人
     * @return 影响行数
     */
    int updateAuditStatus(@Param("courseId") Long courseId,
                          @Param("expectedAuditStatus") String expectedAuditStatus,
                          @Param("targetAuditStatus") String targetAuditStatus,
                          @Param("updateBy") String updateBy);

    /**
     * 按预期上下架状态推进课程上下架状态。
     *
     * @param courseId       课程ID
     * @param expectedStatus 预期旧状态
     * @param targetStatus   新状态
     * @param updateBy       更新人
     * @return 影响行数
     */
    int updateShelfStatus(@Param("courseId") Long courseId,
                          @Param("expectedStatus") String expectedStatus,
                          @Param("targetStatus") String targetStatus,
                          @Param("updateBy") String updateBy);

    /**
     * 管理端分页查询课程列表。
     *
     * @param query 查询条件
     * @return 课程列表
     */
    List<CourseListVO> selectAdminList(CourseQueryReqDTO query);

    /**
     * 管理端查询课程详情。
     *
     * @param courseId 课程ID
     * @return 课程详情
     */
    CourseDetailVO selectAdminDetail(@Param("courseId") Long courseId);

    /**
     * 小程序端分页查询公开课程列表。
     *
     * @param query 查询条件
     * @return 课程卡片列表
     */
    List<WxCourseCardVO> selectWxList(WxCourseQueryDTO query);

    /**
     * 小程序端查询公开课程详情。
     *
     * @param courseId 课程ID
     * @return 课程详情
     */
    WxCourseDetailVO selectWxDetail(@Param("courseId") Long courseId);

    /**
     * 查询当前用户学习过的课程列表。
     *
     * @param userId 当前用户ID
     * @return 我的课程列表
     */
    List<MyCourseVO> selectMyCourseList(@Param("userId") Long userId);
}
