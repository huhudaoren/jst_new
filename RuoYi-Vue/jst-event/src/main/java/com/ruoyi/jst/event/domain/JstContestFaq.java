package com.ruoyi.jst.event.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 赛事常见问题实体。
 *
 * @author jst
 * @since 1.0.0
 */
public class JstContestFaq extends BaseEntity {

    private Long faqId;
    private Long contestId;
    private String question;
    private String answer;
    private Integer sortOrder;
    private String delFlag;

    public Long getFaqId() {
        return faqId;
    }

    public void setFaqId(Long faqId) {
        this.faqId = faqId;
    }

    public Long getContestId() {
        return contestId;
    }

    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }
}
