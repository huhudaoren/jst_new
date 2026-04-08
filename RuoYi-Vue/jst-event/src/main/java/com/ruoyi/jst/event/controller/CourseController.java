package com.ruoyi.jst.event.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.event.dto.CourseQueryReqDTO;
import com.ruoyi.jst.event.dto.CourseSaveReqDTO;
import com.ruoyi.jst.event.service.CourseService;
import com.ruoyi.jst.event.vo.CourseDetailVO;
import com.ruoyi.jst.event.vo.CourseListVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端课程 Controller。
 * <p>
 * 路径前缀：/jst/event/course
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/event/course")
public class CourseController extends BaseController {

    @Autowired
    private CourseService courseService;

    /**
     * 管理端分页查询课程列表。
     *
     * @param query 查询条件
     * @return 分页结果
     * @关联表 jst_course
     * @关联权限 jst:event:course:list
     */
    @PreAuthorize("@ss.hasPermi('jst:event:course:list')")
    @GetMapping("/list")
    public TableDataInfo list(@Validated CourseQueryReqDTO query) {
        startPage();
        List<CourseListVO> list = courseService.selectAdminList(query);
        return getDataTable(list);
    }

    /**
     * 管理端查询课程详情。
     *
     * @param courseId 课程ID
     * @return 课程详情
     * @关联表 jst_course
     * @关联权限 jst:event:course:query
     */
    @PreAuthorize("@ss.hasPermi('jst:event:course:query')")
    @GetMapping("/{courseId}")
    public AjaxResult detail(@PathVariable("courseId") Long courseId) {
        CourseDetailVO detail = courseService.getAdminDetail(courseId);
        return AjaxResult.success(detail);
    }

    /**
     * 新增课程。
     *
     * @param req 新增入参
     * @return 新增课程ID
     * @关联表 jst_course
     * @关联状态机 SM-21
     * @关联权限 jst:event:course:add
     */
    @PreAuthorize("@ss.hasPermi('jst:event:course:add')")
    @PostMapping("/add")
    public AjaxResult add(@Valid @RequestBody CourseSaveReqDTO req) {
        return AjaxResult.success(courseService.addCourse(req));
    }

    /**
     * 编辑课程。
     *
     * @param req 编辑入参
     * @return 处理结果
     * @关联表 jst_course
     * @关联状态机 SM-21
     * @关联权限 jst:event:course:edit
     */
    @PreAuthorize("@ss.hasPermi('jst:event:course:edit')")
    @PutMapping("/edit")
    public AjaxResult edit(@Valid @RequestBody CourseSaveReqDTO req) {
        courseService.editCourse(req);
        return AjaxResult.success();
    }

    /**
     * 提审课程。
     *
     * @param courseId 课程ID
     * @return 处理结果
     * @关联表 jst_course
     * @关联状态机 SM-21
     * @关联权限 jst:event:course:audit
     */
    @PreAuthorize("@ss.hasPermi('jst:event:course:audit')")
    @PostMapping("/{courseId}/submit")
    public AjaxResult submit(@PathVariable("courseId") Long courseId) {
        courseService.submitCourse(courseId);
        return AjaxResult.success();
    }

    /**
     * 审核通过课程。
     *
     * @param courseId 课程ID
     * @return 处理结果
     * @关联表 jst_course
     * @关联状态机 SM-21
     * @关联权限 jst:event:course:audit
     */
    @PreAuthorize("@ss.hasPermi('jst:event:course:audit')")
    @PostMapping("/{courseId}/audit/approve")
    public AjaxResult approve(@PathVariable("courseId") Long courseId) {
        courseService.approveCourse(courseId);
        return AjaxResult.success();
    }

    /**
     * 审核驳回课程。
     *
     * @param courseId 课程ID
     * @return 处理结果
     * @关联表 jst_course
     * @关联状态机 SM-21
     * @关联权限 jst:event:course:audit
     */
    @PreAuthorize("@ss.hasPermi('jst:event:course:audit')")
    @PostMapping("/{courseId}/audit/reject")
    public AjaxResult reject(@PathVariable("courseId") Long courseId) {
        courseService.rejectCourse(courseId);
        return AjaxResult.success();
    }

    /**
     * 上架课程。
     *
     * @param courseId 课程ID
     * @return 处理结果
     * @关联表 jst_course
     * @关联权限 jst:event:course:on
     */
    @PreAuthorize("@ss.hasPermi('jst:event:course:on')")
    @PostMapping("/{courseId}/on")
    public AjaxResult on(@PathVariable("courseId") Long courseId) {
        courseService.onCourse(courseId);
        return AjaxResult.success();
    }

    /**
     * 下架课程。
     *
     * @param courseId 课程ID
     * @return 处理结果
     * @关联表 jst_course
     * @关联权限 jst:event:course:off
     */
    @PreAuthorize("@ss.hasPermi('jst:event:course:off')")
    @PostMapping("/{courseId}/off")
    public AjaxResult off(@PathVariable("courseId") Long courseId) {
        courseService.offCourse(courseId);
        return AjaxResult.success();
    }
}
