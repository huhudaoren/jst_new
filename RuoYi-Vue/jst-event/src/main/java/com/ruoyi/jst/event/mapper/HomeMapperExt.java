package com.ruoyi.jst.event.mapper;

import com.ruoyi.jst.event.vo.ContestListVO;
import com.ruoyi.jst.event.vo.CourseListVO;
import com.ruoyi.jst.event.vo.HomeTagSourceVO;
import com.ruoyi.jst.event.vo.HomeTopicVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 小程序首页聚合 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
@Mapper
public interface HomeMapperExt {

    /**
     * 查询推荐赛事列表。
     *
     * @param limit 限制条数
     * @return 推荐赛事
     */
    List<ContestListVO> selectRecommendContests(@Param("limit") Integer limit);

    /**
     * 查询推荐课程列表。
     *
     * @param limit 限制条数
     * @return 推荐课程
     */
    List<CourseListVO> selectRecommendCourses(@Param("limit") Integer limit);

    /**
     * 查询热门标签聚合源数据。
     *
     * @param limit 读取条数
     * @return 标签聚合源
     */
    List<HomeTagSourceVO> selectHotTagSources(@Param("limit") Integer limit);

    /**
     * 查询首页专题活动。
     *
     * @param limit 限制条数
     * @return 专题列表
     */
    List<HomeTopicVO> selectTopics(@Param("limit") Integer limit);
}
