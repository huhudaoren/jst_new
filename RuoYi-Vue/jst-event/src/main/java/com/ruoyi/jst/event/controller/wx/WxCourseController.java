package com.ruoyi.jst.event.controller.wx;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.event.dto.WxCourseQueryDTO;
import com.ruoyi.jst.event.service.CourseService;
import com.ruoyi.jst.event.vo.MyCourseVO;
import com.ruoyi.jst.event.vo.WxCourseCardVO;
import com.ruoyi.jst.event.vo.WxCourseDetailVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 小程序端课程 Controller。
 * <p>
 * 路径前缀：/jst/wx/course
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/wx/course")
public class WxCourseController extends BaseController {

    @Autowired
    private CourseService courseService;

    /**
     * 公开分页查询课程列表。
     *
     * @param query 查询条件
     * @return 分页结果
     * @关联表 jst_course
     * @关联权限 @Anonymous
     */
    @Anonymous
    @GetMapping("/list")
    public TableDataInfo list(@Valid WxCourseQueryDTO query) {
        startPage();
        List<WxCourseCardVO> list = courseService.selectWxList(query);
        return getDataTable(list);
    }

    /**
     * 公开查询课程详情。
     *
     * @param courseId 课程ID
     * @return 课程详情
     * @关联表 jst_course
     * @关联权限 @Anonymous
     */
    @Anonymous
    @GetMapping("/{courseId}")
    public AjaxResult detail(@PathVariable("courseId") Long courseId) {
        WxCourseDetailVO detail = courseService.getWxDetail(courseId);
        return AjaxResult.success(detail);
    }

    /**
     * 查询我学习过的课程列表。
     *
     * @return 我的课程列表
     * @关联表 jst_course / jst_course_learn_record
     * @关联权限 hasRole('jst_student')
     */
    @PreAuthorize("@ss.hasRole('jst_student')")
    @GetMapping("/my")
    public AjaxResult myCourses() {
        List<MyCourseVO> list = courseService.selectMyCourses(currentUserId());
        return AjaxResult.success(list);
    }

    private Long currentUserId() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        return userId;
    }
}
