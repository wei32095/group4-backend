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
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像',
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
-- 测试数据
-- =============================================

-- 用户 (16个)
INSERT IGNORE INTO `user` (`id`, `name`, `password`, `phone`, `bio`, `avatar`, `role`, `openid`, `status`, `created_at`, `updated_at`) VALUES
(1,'张老师','654321','13800000001','资深英语讲师','https://example.com/avatar/13.jpg',2,'wx_openid_001',1,'2026-06-28 13:10:15','2026-06-28 16:29:22'),
(2,'李小明','123456','13800000002','初二学生','https://example.com/avatar2.jpg',1,'wx_openid_002',1,'2026-06-28 13:10:15','2026-06-30 11:32:31'),
(3,'王小红','123456','13800000003','初一学生','https://example.com/avatar3.jpg',1,'wx_openid_003',1,'2026-06-28 13:10:15','2026-06-30 13:20:48'),
(4,'admin','123456','13800000004','系统管理员','https://example.com/avatar4.jpg',3,NULL,1,'2026-06-28 13:10:15','2026-06-28 13:10:15'),
(5,'李斯','123456','13800001234',NULL,NULL,1,NULL,1,'2026-06-28 15:13:29','2026-06-28 15:13:29'),
(6,'我是微信用户','123456','13800001111',NULL,NULL,1,'mock_openid_001',1,'2026-06-28 15:56:42','2026-06-30 12:43:48'),
(9,'张三','123456','13812345678',NULL,NULL,1,'mock_openid_002',1,'2026-06-30 11:35:30','2026-06-30 17:06:02'),
(22,'何彦星','123456',NULL,NULL,NULL,1,'mock_openid_003',1,'2026-06-30 14:31:56','2026-06-30 15:24:09'),
(24,'韦敏睿','123456','18260933155',NULL,NULL,1,NULL,1,'2026-06-30 16:33:00','2026-06-30 16:33:00'),
(30,'陈老师','123456','13800000100','英语口语名师','https://example.com/avatar30.jpg',2,NULL,1,'2026-07-01 09:00:00','2026-07-01 09:00:00'),
(31,'刘老师','123456','13800000101','阅读理解教师','https://example.com/avatar31.jpg',2,NULL,1,'2026-07-01 09:00:00','2026-07-01 09:00:00'),
(32,'系统管理员','guanliyuan','13800000999',NULL,NULL,3,NULL,1,'2026-07-01 09:00:00','2026-07-01 09:00:00'),
(33,'赵小刚','123456','13800000102',NULL,NULL,1,NULL,1,'2026-07-01 10:00:00','2026-07-01 10:00:00'),
(34,'陈小美','123456','13800000103',NULL,NULL,1,NULL,1,'2026-07-01 10:00:00','2026-07-01 10:00:00'),
(35,'周小明','123456','13800000104',NULL,NULL,1,NULL,1,'2026-07-01 10:00:00','2026-07-01 10:00:00'),
(36,'林小婷','123456','13800000105',NULL,NULL,1,NULL,1,'2026-07-01 10:00:00','2026-07-01 10:00:00');

-- 课程
INSERT IGNORE INTO `course` (`id`, `user_id`, `course_title`, `description`, `cover`, `student_count`, `course_code`, `status`, `created_at`, `updated_at`, `audit_status`) VALUES
(1,1,'英语语法入门','从词性到时态，轻松掌握英语语法基础','https://example.com/course1.jpg',4,'ENG2026','active','2026-06-28 13:10:15','2026-06-28 13:10:15',1),
(2,1,'英语阅读与写作','通过趣味短文学习地道表达','https://example.com/course2.jpg',3,'WRITE2026','active','2026-06-28 13:10:15','2026-06-30 16:53:47',1),
(3,30,'趣味英语口语','日常对话场景练习','https://example.com/course3.jpg',0,'TALK2026','pending','2026-07-01 09:00:00','2026-07-01 09:00:00',0);

-- 课程-学生
INSERT IGNORE INTO `course_student` (`id`, `course_id`, `user_id`, `joined_at`) VALUES
(1,1,2,'2026-06-28 13:10:15'),(2,1,3,'2026-06-28 13:10:15'),
(3,2,2,'2026-06-28 13:10:15'),(4,2,24,'2026-06-30 16:54:00'),
(5,1,33,'2026-07-01 10:00:00'),(6,1,34,'2026-07-01 10:00:00'),
(7,2,35,'2026-07-01 10:00:00');

-- 课堂
INSERT IGNORE INTO `class` (`id`, `course_id`, `user_id`, `class_title`, `file_url`, `end_time`, `status`, `create_time`) VALUES
(1,1,1,'第一讲：名词与冠词','https://example.com/ppt1.pptx','2026-07-01 11:00:00','ended','2026-06-29 15:07:22'),
(2,1,1,'第二讲：动词与时态','https://example.com/ppt2.pptx','2026-07-08 11:00:00','active','2026-06-29 15:07:22'),
(3,2,1,'第一讲：如何写好段落',NULL,'2026-06-20 16:00:00','ended','2026-06-29 15:07:22');

-- 作业（已去掉 student_status 字段）
INSERT IGNORE INTO `assignment` (`id`, `course_id`, `assignment_title`, `deadline`, `max_score`, `assignment_create_time`, `updated_at`) VALUES
(1,1,'第一次作业：名词分类练习','2026-07-05 23:59:59',100,'2026-06-28 13:10:16','2026-06-28 13:10:16'),
(2,1,'第二次作业：一般现在时','2026-07-15 23:59:59',100,'2026-06-28 13:10:16','2026-06-28 13:10:16'),
(3,2,'段落写作：我的周末','2026-07-25 23:59:59',50,'2026-06-28 13:10:16','2026-07-01 16:35:41');

-- 题目
INSERT IGNORE INTO `question` (`id`, `assignment_id`, `type`, `stem`, `answer`, `explanation`, `perscore`, `sort_order`) VALUES
(1,1,1,'下列哪个单词是可数名词？','C','apple是可数名词',5,1),
(2,1,2,'哪些是不可数名词？','A,B,D','water/rice/milk都是不可数',10,2),
(3,1,3,'an用于元音音素开头的单词前','正确','an apple, an hour',5,3),
(4,1,5,'写出5个不可数名词并造句','略','',20,4),
(5,2,1,'He _____ to school every day.','C','第三人称单数用goes',10,1),
(6,2,5,'用一般现在时介绍你的日常生活',NULL,NULL,30,2),
(7,3,1,'主题句通常放在段落什么位置？','B','段落开头',5,1),
(8,3,4,'连接思路的词叫做______词','过渡','however, therefore等',5,2),
(9,3,5,'以My Favorite Season写80词短文',NULL,NULL,20,3);

-- 客观题提交
INSERT IGNORE INTO `object_submit` (`id`, `assignment_id`, `question_id`, `user_id`, `object_score`, `answer_word`, `submit_time`) VALUES
(1,1,1,2,5,'C','2026-06-28 13:10:16'),
(2,1,2,2,10,'A,B,D','2026-06-28 13:10:16'),
(3,1,3,2,0,'正确','2026-06-28 13:10:16'),
(4,3,7,2,5,'B','2026-06-28 13:10:16'),
(5,3,8,2,5,'过渡','2026-06-28 13:10:16');

-- 主观题提交
INSERT IGNORE INTO `subject_submit` (`id`, `assignment_id`, `user_id`, `question_id`, `answer_picture`, `subject_score`, `teacher_comment`, `finish_status`, `grading_status`, `finish_time`, `grading_time`) VALUES
(1,1,2,4,'https://example.com/answer1.jpg',18,'句子写得很好！',2,2,'2026-06-28 13:10:16','2026-06-28 13:10:16'),
(2,2,3,6,'https://example.com/answer2.jpg',NULL,NULL,2,1,'2026-06-28 13:10:16',NULL),
(3,3,2,9,'https://example.com/answer3.jpg',15,'注意过渡词使用',2,2,'2026-06-28 13:10:16','2026-06-28 13:10:16');

-- 签到
INSERT IGNORE INTO `class_check` (`id`, `class_id`, `user_id`, `check_status`, `checkin_time`) VALUES
(1,1,2,1,'2026-07-01 08:55:00'),(2,1,3,2,'2026-07-01 09:15:00'),
(3,2,2,1,'2026-07-08 08:50:00'),(4,2,3,3,NULL);

-- 投票
INSERT IGNORE INTO `class_vote` (`id`, `class_id`, `heading`, `options`, `correct_option`, `duration`, `status`) VALUES
(1,1,'_____ apple a day...','[\"A. A\",\"B. An\",\"C. The\",\"D. 不填\"]','B',120,'ended'),
(2,2,'She _____ to school yesterday.','[\"A. go\",\"B. goes\",\"C. went\",\"D. going\"]','C',90,'active');
INSERT IGNORE INTO `vote_record` (`id`, `vote_id`, `user_id`, `selected_option`, `is_correct`, `submitted_at`) VALUES
(1,1,2,'B',1,'2026-06-28 13:10:16'),(2,1,3,'A',0,'2026-06-28 13:10:16'),(3,2,2,'C',1,'2026-06-28 13:10:16');

-- 聊天 自习室 积分
INSERT IGNORE INTO `class_chat` (`id`, `class_id`, `user_id`, `message_type`, `content`, `sent_time`) VALUES
(1,1,2,1,'可数名词和不可数名词怎么区分？','2026-06-28 13:10:16'),
(2,1,1,1,'能数出个数的就是可数名词','2026-06-28 13:10:16');
INSERT IGNORE INTO `study_room` (`id`, `user_id`, `goal`, `mode`, `start_time`, `end_time`, `total_time`, `created_at`) VALUES
(1,2,'复习',1,'2026-07-01 08:00:00','2026-07-01 10:30:00',9000,'2026-06-28 13:10:16'),
(2,2,'背单词',2,'2026-07-02 19:00:00','2026-07-02 20:30:00',5400,'2026-06-28 13:10:16'),
(3,3,'预习',1,'2026-07-03 14:00:00','2026-07-03 16:00:00',7200,'2026-06-28 13:10:16');
INSERT IGNORE INTO `points_record` (`id`, `user_id`, `change_type`, `change_points`, `left_points`, `source_type`, `change_time`) VALUES
(1,2,1,10,50,1,'2026-06-28 13:10:16'),
(2,2,1,5,55,2,'2026-06-28 13:10:16'),
(3,2,2,20,35,5,'2026-06-28 13:10:16'),
(4,3,1,10,30,1,'2026-06-28 13:10:16'),
(5,3,1,15,45,3,'2026-06-28 13:10:16'),
(6,2,2,15,20,5,'2026-06-29 11:17:07');

-- 花卉
INSERT IGNORE INTO `seed` (`id`, `variety`, `description`, `image`, `price`, `sunlight_max`, `water_max`, `nutrient_max`, `created_at`, `is_deleted`) VALUES
(1,'向日葵','向阳而生','https://example.com/seeds/sunflower.png',0,100,80,60,'2026-06-29 18:29:18',0),
(2,'玫瑰','热情似火','https://example.com/seeds/rose.png',50,90,100,70,'2026-06-29 18:29:18',0),
(3,'仙人掌','耐旱易养','https://example.com/seeds/cactus.png',30,60,40,50,'2026-06-29 18:29:18',0),
(4,'樱花','绚烂短暂','https://example.com/seeds/cherry.png',80,80,90,100,'2026-06-29 18:29:18',0);
INSERT IGNORE INTO `flower` (`id`, `user_id`, `seed_id`, `sunlight`, `water`, `nutrient`, `growth_value`, `stage`, `is_unlocked`, `created_at`, `updated_at`) VALUES
(1,2,2,90,80,60,230,3,1,'2026-06-28 13:10:16','2026-07-01 15:29:33'),
(2,2,1,50,60,40,150,2,0,'2026-06-28 13:10:16','2026-07-01 15:29:33'),
(3,3,2,90,100,70,260,3,0,'2026-06-28 13:10:16','2026-07-01 15:31:47'),
(4,6,1,0,0,0,0,0,0,'2026-06-29 22:56:34','2026-06-29 22:56:34'),
(5,3,1,30,40,20,90,1,0,'2026-07-01 15:32:37','2026-07-01 15:32:37'),
(6,24,1,20,10,20,50,0,0,'2026-07-01 16:22:39','2026-07-01 16:41:58');
INSERT IGNORE INTO `shop_item` (`id`, `item_name`, `icon`, `price`, `attribute_type`, `boost_value`, `created_at`) VALUES
(1,'有机肥料','https://example.com/fertilizer.png',5,2,10,'2026-07-01 15:18:34'),
(2,'纯净水','https://example.com/potion.png',10,3,10,'2026-07-01 15:18:34'),
(3,'阳光精华','https://example.com/potion.png',8,1,10,'2026-07-01 15:18:34');

-- 学情分析
INSERT IGNORE INTO `student_analysis` (`id`, `user_id`, `total_study_duration`, `assignment_correct_rate`, `week_study_duration`, `updated_at`) VALUES
(1,2,14455,85.50,55,'2026-06-30 11:16:59'),
(2,3,7200,70.00,1800,'2026-06-28 13:10:16');

-- 通知
INSERT IGNORE INTO `notice` (`id`, `user_id`, `notice_title`, `notice_content`, `notice_status`, `notice_type`, `push_time`) VALUES
(1,2,'作业批改通知','第一次作业已批改，得分85',1,4,'2026-06-28 13:10:16'),
(2,2,'课堂提醒','第二讲7月8日开始',1,1,'2026-06-28 13:10:16'),
(3,3,'作业截止提醒','第二次作业即将截止',0,2,'2026-06-28 13:10:16'),
(4,2,'系统升级通知','7月1日2:00-5:00系统升级',1,0,'2026-06-29 10:00:00'),
(5,2,'上课提醒','第三讲7月15日14:00开始',1,1,'2026-06-29 09:00:00'),
(6,3,'新作业发布','过去时态练习已发布',0,2,'2026-06-28 15:00:00'),
(7,2,'作业提交提醒','王小红提交了第二次作业',1,3,'2026-06-28 14:00:00'),
(8,3,'作业批改通知','第一次作业已批改，得分92',1,4,'2026-06-28 11:00:00'),
(9,2,'课程通知','暑期课程安排已更新',1,0,'2026-06-27 16:00:00'),
(10,3,'上课提醒','本周五课程取消',0,1,'2026-06-27 10:00:00');

-- 通知发布记录（管理端查看用）
INSERT IGNORE INTO `notice_publish` (`id`, `notice_title`, `notice_content`, `target_role`, `recipient_count`, `push_time`) VALUES
(1,'系统升级通知','7月1日2:00-5:00系统升级维护',NULL,10,'2026-06-29 10:00:00'),
(2,'新作业发布','过去时态练习已发布，请按时完成',1,8,'2026-06-28 15:00:00'),
(3,'课程通知','暑期课程安排已更新，请查看最新课表',NULL,10,'2026-06-27 16:00:00');

-- 同步积分
UPDATE `user` u
JOIN (SELECT user_id, left_points FROM points_record WHERE (user_id, id) IN (SELECT user_id, MAX(id) FROM points_record GROUP BY user_id)) p ON u.id = p.user_id
SET u.points = p.left_points;

-- 课程资源 敏感词 问答
INSERT IGNORE INTO `course_resource` (`id`, `course_id`, `user_id`, `file_name`, `file_url`, `file_size`, `description`, `download_count`, `created_at`) VALUES
(1,1,1,'第一讲课件.pptx','https://example.com/resource/ppt1.pptx',2048000,'课堂PPT',3,'2026-07-01 09:00:00'),
(2,1,1,'名词练习题.pdf','https://example.com/resource/exercise1.pdf',512000,'课后练习',5,'2026-07-01 10:00:00'),
(3,2,1,'范文集.pdf','https://example.com/resource/essay.pdf',102400,'范文合集',2,'2026-06-20 15:00:00');
INSERT IGNORE INTO `sensitive_word` (`id`, `word`, `created_at`) VALUES (1,'垃圾',NOW()),(2,'笨蛋',NOW()),(3,'傻瓜',NOW()),(4,'滚',NOW());
INSERT IGNORE INTO `course_problem` (`id`, `course_id`, `user_id`, `title`, `content`, `reply_count`, `created_at`) VALUES
(1,1,2,'可数名词变复数的规则？','能总结一下吗？',1,'2026-07-02 10:00:00'),
(2,1,3,'a和an的区别？','看第一个字母吗？',1,'2026-07-03 14:00:00'),
(3,2,2,'主题句必须放第一句？','有时在最后，可以吗？',0,'2026-07-04 09:00:00');
INSERT IGNORE INTO `course_problem_reply` (`id`, `problem_id`, `user_id`, `content`, `created_at`) VALUES
(1,1,1,'s/x/sh/ch加es，辅音+y改y为i加es','2026-07-02 11:00:00'),
(2,2,1,'看发音，如university发/juː/用a','2026-07-03 15:00:00');
