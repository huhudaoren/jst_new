ALTER TABLE jst_contest
    ADD COLUMN schedule_json TEXT COMMENT '赛程JSON [{stageName,startTime,endTime,description}]';

ALTER TABLE jst_contest
    ADD COLUMN awards_json TEXT COMMENT '奖项JSON [{awardName,awardLevel,description,count}]';

ALTER TABLE jst_contest
    ADD COLUMN faq_json TEXT COMMENT '常见问题JSON [{question,answer}]';

ALTER TABLE jst_contest
    ADD COLUMN recommend_tags VARCHAR(500) DEFAULT NULL COMMENT '标签（逗号分隔）如"艺术,全学段,国际赛事"';
