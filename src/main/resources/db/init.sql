-- =============================================
-- 青耘学堂 数据库初始化脚本
-- 适用于 MySQL 8.x
-- =============================================

CREATE DATABASE IF NOT EXISTS `qingyun` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `qingyun`;

-- =============================================
-- 1. 用户表
-- =============================================
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `bio` VARCHAR(200) DEFAULT NULL COMMENT '个人简介',
    `avatar` VARCHAR(1000) DEFAULT NULL COMMENT '头像',
    `role` TINYINT DEFAULT '1' COMMENT '角色：1-学生，2-教师，3-管理员',
    `openid` VARCHAR(100) DEFAULT NULL COMMENT '微信标识',
    `status` TINYINT DEFAULT '1' COMMENT '状态：1-正常，0-禁用',
    `points` INT DEFAULT '0' COMMENT '当前积分余额',
    `ban_expire_time` DATETIME DEFAULT NULL COMMENT '封禁到期时间',
    `ban_reason` VARCHAR(255) DEFAULT NULL COMMENT '封禁原因',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `phone` (`phone`),
    KEY `idx_phone` (`phone`),
    KEY `idx_role` (`role`),
    KEY `idx_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';


-- =============================================
-- 2. 课程表
-- =============================================
CREATE TABLE `course` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '课程ID',
    `user_id` BIGINT NOT NULL COMMENT '教师ID',
    `course_title` VARCHAR(100) NOT NULL COMMENT '课程名称',
    `description` TEXT COMMENT '课程描述',
    `cover` VARCHAR(500) DEFAULT NULL COMMENT '封面图',
    `student_count` INT DEFAULT '0' COMMENT '学生人数',
    
    `course_code` VARCHAR(50) NOT NULL COMMENT '课程码',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待审核，active-进行中，archived-已归档',
    `audit_status` TINYINT DEFAULT '0' COMMENT '审核状态：0-待审，1-通过，2-驳回',
    `audit_remark` VARCHAR(255) DEFAULT '' COMMENT '审核备注',
    `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `course_code` (`course_code`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_course_code` (`course_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';


-- =============================================
-- 3. 课程-学生关联表
-- =============================================
CREATE TABLE `course_student` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `user_id` BIGINT NOT NULL COMMENT '学生ID',
    `joined_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_course_student` (`course_id`, `user_id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_student_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程-学生关联表';


-- =============================================
-- 4. 课程评价表
-- =============================================
CREATE TABLE `course_review` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `star` TINYINT NOT NULL COMMENT '星级评价（1-5）',
    `review_content` VARCHAR(500) DEFAULT NULL COMMENT '文字评价内容',
    `likecount` INT DEFAULT '0' COMMENT '点赞数',
    `review_create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_course_user` (`course_id`, `user_id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程评价表';


-- =============================================
-- 5. 课堂表
-- =============================================
CREATE TABLE `class` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '课堂ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `user_id` BIGINT NOT NULL COMMENT '老师ID',
    `class_title` VARCHAR(100) NOT NULL COMMENT '课堂名称',
    `file_url` VARCHAR(500) DEFAULT NULL COMMENT '课件附件地址',
    `end_time` DATETIME DEFAULT NULL COMMENT '课堂结束时间',
    `status` VARCHAR(20) DEFAULT 'not_started' COMMENT '状态：not_started-未开始，active-进行中，ended-已结束',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status_time` (`status`,`create_time`,`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课堂表';


-- =============================================
-- 6. 课程资源表
-- =============================================
CREATE TABLE `course_resource` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '资源ID',
    `course_id` BIGINT NOT NULL COMMENT '所属课程ID',
    `user_id` BIGINT NOT NULL COMMENT '上传者ID（教师）',
    `file_name` VARCHAR(200) NOT NULL COMMENT '文件名',
    `file_url` VARCHAR(500) NOT NULL COMMENT '文件访问URL（OSS地址）',
    `file_size` BIGINT DEFAULT '0' COMMENT '文件大小（字节）',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '资源描述',
    `download_count` INT DEFAULT 0 COMMENT '下载次数',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_course_id (`course_id`),
    INDEX idx_user_id (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程资源表';


-- =============================================
-- 7. 作业表
-- =============================================
CREATE TABLE `assignment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '作业ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `assignment_title` VARCHAR(100) NOT NULL COMMENT '作业标题',
    `deadline` DATETIME NOT NULL COMMENT '截止时间',
    `max_score` INT NOT NULL COMMENT '满分分值',
    `assignment_create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_deadline` (`deadline`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业表';


-- =============================================
-- 8. 题目表
-- =============================================
CREATE TABLE `question` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '题目ID',
    `assignment_id` BIGINT NOT NULL COMMENT '所属作业ID',
    `type` TINYINT NOT NULL COMMENT '题型：1-单选题，2-多选题，3-判断题，4-填空题，5-主观题',
    `stem` TEXT NOT NULL COMMENT '题干',
    `answer` TEXT COMMENT '正确答案',
    `explanation` TEXT COMMENT '解析',
    `perscore` INT NOT NULL COMMENT '每题分值',
    `sort_order` INT DEFAULT '0' COMMENT '排序序号',
    `options` JSON DEFAULT NULL COMMENT '选项（单选题/多选题专用，JSON数组格式）',
    `image_url` VARCHAR(500) DEFAULT NULL COMMENT '题目图片（OSS URL）',
    PRIMARY KEY (`id`),
    KEY `idx_assignment_id` (`assignment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';


-- =============================================
-- 9. 客观题提交表
-- =============================================
CREATE TABLE `object_submit` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '提交ID',
    `assignment_id` BIGINT NOT NULL COMMENT '作业ID',
    `question_id` BIGINT NOT NULL COMMENT '题目ID',
    `user_id` BIGINT NOT NULL COMMENT '学生ID',
    `object_score` INT DEFAULT '0' COMMENT '客观题得分',
    `answer_word` VARCHAR(500) DEFAULT NULL COMMENT '学生答案',
    `submit_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '学生提交时间',
    PRIMARY KEY (`id`),
    KEY `idx_assignment_id` (`assignment_id`),
    KEY `idx_question_id` (`question_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客观题提交表';


-- =============================================
-- 10. 主观题提交表
-- =============================================
CREATE TABLE `subject_submit` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '提交ID',
    `assignment_id` BIGINT NOT NULL COMMENT '作业ID',
    `user_id` BIGINT NOT NULL COMMENT '学生ID',
    `question_id` BIGINT NOT NULL COMMENT '题目ID',
    `answer_picture` VARCHAR(500) DEFAULT NULL COMMENT '答题图片',
    `subject_score` INT DEFAULT NULL COMMENT '主观题得分',
    `teacher_comment` VARCHAR(500) DEFAULT NULL COMMENT '老师评语',
    `finish_status` TINYINT DEFAULT '1' COMMENT '学生完成状态：1-待完成，2-已完成',
    `grading_status` TINYINT DEFAULT '1' COMMENT '老师批改状态：1-待批改，2-已批改',
    `finish_time` DATETIME DEFAULT NULL COMMENT '学生提交时间',
    `grading_time` DATETIME DEFAULT NULL COMMENT '老师批改时间',
    PRIMARY KEY (`id`),
    KEY `idx_assignment_id` (`assignment_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_question_id` (`question_id`),
    KEY `idx_finish_status` (`finish_status`),
    KEY `idx_grading_status` (`grading_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='主观题提交表';


-- =============================================
-- 11. 课堂签到表
-- =============================================
CREATE TABLE `class_check` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '签到ID',
    `class_id` BIGINT NOT NULL COMMENT '课堂ID',
    `user_id` BIGINT NOT NULL COMMENT '学生ID',
    `check_status` TINYINT DEFAULT '1' COMMENT '签到状态：1-正常，2-迟到，3-缺勤',
    `checkin_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '签到时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_class_user` (`class_id`, `user_id`),
    KEY `idx_class_id` (`class_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课堂签到表';


-- =============================================
-- 12. 课堂投票表
-- =============================================
CREATE TABLE `class_vote` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '投票ID',
    `class_id` BIGINT NOT NULL COMMENT '课堂ID',
    `heading` VARCHAR(255) NOT NULL COMMENT '选择题题目',
    `options` JSON NOT NULL COMMENT '选项列表',
    `correct_option` VARCHAR(10) DEFAULT NULL COMMENT '正确答案（自动判分）',
    `duration` INT DEFAULT '120' COMMENT '投票时长（>0）',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-进行中，ended-已结束',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    `ended_at` DATETIME DEFAULT NULL COMMENT '结束时间',
    PRIMARY KEY (`id`),
    KEY `idx_class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课堂投票表';


-- =============================================
-- 13. 投票记录表
-- =============================================
CREATE TABLE `vote_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `vote_id` BIGINT NOT NULL COMMENT '投票ID',
    `user_id` BIGINT NOT NULL COMMENT '学生ID',
    `selected_option` VARCHAR(10) NOT NULL COMMENT '学生选的选项',
    `is_correct` TINYINT DEFAULT '0' COMMENT '是否正确：0-错误，1-正确',
    `submitted_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_vote_user` (`vote_id`, `user_id`),
    KEY `idx_vote_id` (`vote_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投票记录表';


-- =============================================
-- 14. 课堂聊天表
-- =============================================
CREATE TABLE `class_chat` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    `class_id` BIGINT NOT NULL COMMENT '课堂ID',
    `user_id` BIGINT NOT NULL COMMENT '发送人ID',
    `message_type` TINYINT DEFAULT '1' COMMENT '消息类型：1-文字，2-图片，3-表情',
    `content` VARCHAR(500) NOT NULL COMMENT '消息内容',
    `sent_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    PRIMARY KEY (`id`),
    KEY `idx_class_id` (`class_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课堂聊天表';


-- =============================================
-- 15. 自习室记录表
-- =============================================
CREATE TABLE `study_room` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '学生ID',
    `goal` VARCHAR(255) DEFAULT NULL COMMENT '自习目标',
    `mode` TINYINT NOT NULL COMMENT '模式：1-正向计时，2-倒计时',
    `focus_mode` TINYINT DEFAULT '0' COMMENT '专注模式：0-普通，1-番茄钟',
    `plan_time` INT DEFAULT NULL COMMENT '计划时长（秒，倒计时用）',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
    `total_time` INT DEFAULT NULL COMMENT '总时长（秒）',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_goal` (`goal`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自习室记录表';


-- =============================================
-- 16. 积分记录表
-- =============================================
CREATE TABLE `points_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '积分记录ID',
    `user_id` BIGINT NOT NULL COMMENT '学生ID',
    `change_type` TINYINT NOT NULL COMMENT '变动类型：1-获取，2-消耗',
    `change_points` INT NOT NULL COMMENT '变动积分数（正数）',
    `left_points` INT NOT NULL COMMENT '变动后余额',
    `source_type` TINYINT DEFAULT NULL COMMENT '来源/用途：1-课堂签到，2-课堂投票正确，3-作业分数，4-自习时长，5-道具兑换，6-问答回复',
    `change_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '变动时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_change_time` (`change_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分记录表';


-- =============================================
-- 17. 花卉品种配置表
-- =============================================
CREATE TABLE `seed` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '品种ID',
    `variety` VARCHAR(50) NOT NULL COMMENT '品种名称',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '简介',
    `image` VARCHAR(500) DEFAULT NULL COMMENT '图片URL',
    `stage0_image` VARCHAR(500) DEFAULT NULL COMMENT '种子阶段图片',
    `stage1_image` VARCHAR(500) DEFAULT NULL COMMENT '发芽阶段图片',
    `stage2_image` VARCHAR(500) DEFAULT NULL COMMENT '长叶阶段图片',
    `stage3_image` VARCHAR(500) DEFAULT NULL COMMENT '开花阶段图片',
    `price` INT NOT NULL COMMENT '购买价格（积分）',
    `sunlight_max` INT DEFAULT '100' COMMENT '阳光满值',
    `water_max` INT DEFAULT '100' COMMENT '水分满值',
    `nutrient_max` INT DEFAULT '100' COMMENT '养份满值',
    `is_deleted` TINYINT DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='花卉品种配置表';


-- =============================================
-- 18. 花卉实例表
-- =============================================
CREATE TABLE `flower` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '花卉ID',
    `user_id` BIGINT NOT NULL COMMENT '学生ID',
    `seed_id` BIGINT NOT NULL COMMENT '品种ID',
    `sunlight` INT DEFAULT '0' COMMENT '阳光值',
    `water` INT DEFAULT '0' COMMENT '水分值',
    `nutrient` INT DEFAULT '0' COMMENT '养份值',
    `growth_value` INT DEFAULT '0' COMMENT '当前生长值（冗余，由三维计算）',
    `stage` TINYINT DEFAULT '0' COMMENT '生长阶段：0-种子，1-发芽，2-长叶，3-开花',
    `is_unlocked` TINYINT DEFAULT '0' COMMENT '是否解锁图鉴：0-未解锁，1-已解锁',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_seed_id` (`seed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='花卉实例表';


-- =============================================
-- 19. 商店道具表
-- =============================================
CREATE TABLE `shop_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '道具ID',
    `item_name` VARCHAR(50) NOT NULL COMMENT '道具名称',
    `icon` VARCHAR(500) DEFAULT NULL COMMENT '道具图标url',
    `price` INT NOT NULL COMMENT '所需积分',
    `attribute_type` TINYINT DEFAULT '1' COMMENT '属性类型：1-阳光 2-水分 3-养份',
    `boost_value` INT DEFAULT '10' COMMENT '提升值',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_price` (`price`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商店道具表';


-- =============================================
-- 20. 系统通知表
-- =============================================
CREATE TABLE `notice` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '通知ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `notice_title` VARCHAR(100) NOT NULL COMMENT '通知标题',
    `notice_content` TEXT NOT NULL COMMENT '通知内容',
    `notice_status` TINYINT DEFAULT '0' COMMENT '推送状态：0-未读，1-已读',
    `notice_type` TINYINT DEFAULT '0' COMMENT '通知类型：0-通用，1-上课提醒，2-作业发布，3-作业提交，4-批改完成',
    `push_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '推送时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_status_time` (`user_id`,`notice_status`,`push_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统通知表';


-- =============================================
-- 21. 通知发布记录表（管理端查看已发布通知用）
-- =============================================
CREATE TABLE `notice_publish` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '发布记录ID',
    `notice_title` VARCHAR(100) NOT NULL COMMENT '通知标题',
    `notice_content` TEXT NOT NULL COMMENT '通知内容',
    `target_role` TINYINT DEFAULT NULL COMMENT '推送目标角色：1-学生，2-教师，NULL-全部',
    `recipient_count` INT NOT NULL DEFAULT '0' COMMENT '接收人数',
    `push_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '推送时间',
    PRIMARY KEY (`id`),
    KEY `idx_push_time` (`push_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知发布记录表';


-- =============================================
-- 22. 学情分析表
-- =============================================
CREATE TABLE `student_analysis` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分析ID',
    `user_id` BIGINT NOT NULL COMMENT '学生ID',
    `total_study_duration` INT DEFAULT '0' COMMENT '累计学习时长（秒）',
    `assignment_correct_rate` DECIMAL(5,2) DEFAULT '0.00' COMMENT '作业正确率（百分比）',
    `week_study_duration` INT DEFAULT '0' COMMENT '周学习时长（秒）',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学情分析表';


-- =============================================
-- 23. 敏感词表
-- =============================================
CREATE TABLE `sensitive_word` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '敏感词ID',
    `word` VARCHAR(50) NOT NULL COMMENT '敏感词',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `word` (`word`),
    KEY `idx_word` (`word`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敏感词表';


-- =============================================
-- 24. 课程问题表
-- =============================================
CREATE TABLE `course_problem` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '问题ID',
    `course_id` BIGINT NOT NULL COMMENT '所属课程ID',
    `user_id` BIGINT NOT NULL COMMENT '提问者ID',
    `title` VARCHAR(100) NOT NULL COMMENT '问题标题',
    `content` TEXT NOT NULL COMMENT '问题内容',
    `reply_count` INT DEFAULT '0' COMMENT '回复数',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程问题表';


-- =============================================
-- 25. 课程问题回复表
-- =============================================
CREATE TABLE `course_problem_reply` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '回复ID',
    `problem_id` BIGINT NOT NULL COMMENT '所属问题ID',
    `user_id` BIGINT NOT NULL COMMENT '回复者ID',
    `content` TEXT NOT NULL COMMENT '回复内容',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_ai` TINYINT DEFAULT '0' COMMENT '是否AI回复：0-人工，1-AI',
    PRIMARY KEY (`id`),
    KEY `idx_problem_id` (`problem_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程问题回复表';


-- =============================================
-- 26. 用户反馈表
-- =============================================
CREATE TABLE `feedback` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '反馈ID',
    `user_id` BIGINT NOT NULL COMMENT '提交人',
    `content` TEXT NOT NULL COMMENT '反馈内容',
    `reply_content` TEXT DEFAULT NULL COMMENT '管理员回复',
    `reply_time` DATETIME DEFAULT NULL COMMENT '回复时间',
    `status` TINYINT DEFAULT '0' COMMENT '状态：0-待处理，1-已回复',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户反馈表';

-- =============================================
-- 27. 习题推荐记录表
-- =============================================
CREATE TABLE `recommendation` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '推荐记录ID',
    `user_id` BIGINT NOT NULL COMMENT '学生ID',
    `assignment_id` BIGINT NOT NULL COMMENT '来源作业ID',
    `questions` JSON NOT NULL COMMENT '推荐题目列表（JSON数组）',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-待练习，1-已完成',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父推荐ID（关联上一次推荐）',
    `is_completed` TINYINT DEFAULT 0 COMMENT '是否全部正确完成：0-进行中，1-已完成',
    INDEX idx_user_id (`user_id`),
    INDEX idx_assignment_id (`assignment_id`),
    INDEX idx_parent_id (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='习题推荐记录表';

-- =============================================
-- 28. 学生作业薄弱知识点表
-- =============================================
CREATE TABLE `assignment_weak_points` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    `assignment_id` BIGINT NOT NULL COMMENT '作业ID',
    `user_id` BIGINT NOT NULL COMMENT '学生ID',
    `weak_points` JSON NOT NULL COMMENT '薄弱知识点（JSON数组）',
    UNIQUE KEY uk_assignment_user (assignment_id, user_id),
    INDEX idx_assignment_id (assignment_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生作业薄弱知识点表';