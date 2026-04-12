package com.ruoyi.jst.event.vo;

import java.util.List;

/**
 * 赛事详情推荐出参 VO。
 *
 * @author jst
 * @since 1.0.0
 */
public class ContestRecommendVO {

    private List<ContestListVO> relatedContests;
    private List<ContestRecommendCourseVO> relatedCourses;

    public List<ContestListVO> getRelatedContests() {
        return relatedContests;
    }

    public void setRelatedContests(List<ContestListVO> relatedContests) {
        this.relatedContests = relatedContests;
    }

    public List<ContestRecommendCourseVO> getRelatedCourses() {
        return relatedCourses;
    }

    public void setRelatedCourses(List<ContestRecommendCourseVO> relatedCourses) {
        this.relatedCourses = relatedCourses;
    }
}
