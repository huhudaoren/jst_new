package com.ruoyi.jst.event.controller.wx;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.jst.event.service.HomeService;
import com.ruoyi.jst.event.vo.ContestListVO;
import com.ruoyi.jst.event.vo.CourseListVO;
import com.ruoyi.jst.event.vo.HomeHotTagVO;
import com.ruoyi.jst.event.vo.HomeTopicVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 小程序首页聚合 Controller。
 *
 * @author jst
 * @since 1.0.0
 */
@Anonymous
@RestController
@RequestMapping("/jst/wx/home")
public class WxHomeController extends BaseController {

    @Autowired
    private HomeService homeService;

    /**
     * 首页推荐赛事。
     *
     * @return 推荐赛事列表（最多 8 条）
     * @关联表 jst_contest / jst_enroll_record
     * @关联权限 @Anonymous
     */
    @Anonymous
    @GetMapping("/recommend-contests")
    public AjaxResult recommendContests() {
        List<ContestListVO> list = homeService.selectRecommendContests();
        return AjaxResult.success(list);
    }

    /**
     * 首页推荐课程。
     *
     * @return 推荐课程列表（最多 6 条）
     * @关联表 jst_course
     * @关联权限 @Anonymous
     */
    @Anonymous
    @GetMapping("/recommend-courses")
    public AjaxResult recommendCourses() {
        List<CourseListVO> list = homeService.selectRecommendCourses();
        return AjaxResult.success(list);
    }

    /**
     * 首页热门标签。
     *
     * @return 热门标签列表（最多 12 条）
     * @关联表 jst_contest
     * @关联权限 @Anonymous
     */
    @Anonymous
    @GetMapping("/hot-tags")
    public AjaxResult hotTags() {
        List<HomeHotTagVO> list = homeService.selectHotTags();
        return AjaxResult.success(list);
    }

    /**
     * 首页专题活动。
     *
     * @return 专题活动列表（最多 3 条）
     * @关联表 jst_notice
     * @关联权限 @Anonymous
     */
    @Anonymous
    @GetMapping("/topics")
    public AjaxResult topics() {
        List<HomeTopicVO> list = homeService.selectTopics();
        return AjaxResult.success(list);
    }
}
