package com.ruoyi.jst.event.service;

import com.ruoyi.jst.event.vo.ContestListVO;
import com.ruoyi.jst.event.vo.CourseListVO;
import com.ruoyi.jst.event.vo.HomeHotTagVO;
import com.ruoyi.jst.event.vo.HomeTopicVO;

import java.util.List;

/**
 * 小程序首页聚合服务。
 *
 * @author jst
 * @since 1.0.0
 */
public interface HomeService {

    /**
     * 查询首页推荐赛事。
     *
     * @return 推荐赛事列表
     * @关联表 jst_contest / jst_enroll_record
     * @关联权限 @Anonymous
     */
    List<ContestListVO> selectRecommendContests();

    /**
     * 查询首页推荐课程。
     *
     * @return 推荐课程列表
     * @关联表 jst_course
     * @关联权限 @Anonymous
     */
    List<CourseListVO> selectRecommendCourses();

    /**
     * 查询首页热门标签。
     *
     * @return 热门标签列表
     * @关联表 jst_contest
     * @关联权限 @Anonymous
     */
    List<HomeHotTagVO> selectHotTags();

    /**
     * 查询首页专题活动。
     *
     * @return 专题活动列表
     * @关联表 jst_notice
     * @关联权限 @Anonymous
     */
    List<HomeTopicVO> selectTopics();
}
