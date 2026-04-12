ALTER TABLE jst_course
    ADD COLUMN lesson_count INT DEFAULT 0 COMMENT '课时数';

ALTER TABLE jst_course
    ADD COLUMN learner_count INT DEFAULT 0 COMMENT '学习人数';

ALTER TABLE jst_course
    ADD COLUMN total_duration VARCHAR(50) DEFAULT NULL COMMENT '总时长如12小时30分';

ALTER TABLE jst_course
    ADD COLUMN chapters_json TEXT COMMENT '课程目录 [{chapterName,lessons:[{lessonName,duration,isFree}]}]';

ALTER TABLE jst_course
    ADD COLUMN teacher_name VARCHAR(100) DEFAULT NULL COMMENT '讲师姓名';

ALTER TABLE jst_course
    ADD COLUMN teacher_avatar VARCHAR(255) DEFAULT NULL COMMENT '讲师头像';

ALTER TABLE jst_course
    ADD COLUMN teacher_desc VARCHAR(500) DEFAULT NULL COMMENT '讲师简介';
