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
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-进行中，archived-已归档',
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
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `status` VARCHAR(20) DEFAULT 'not_started' COMMENT '状态：not_started-未开始，active-进行中，ended-已结束',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status_time` (`status`,`start_time`,`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课堂表';


-- =============================================
-- 6. 作业表
-- =============================================
CREATE TABLE `assignment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '作业ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `assignment_title` VARCHAR(100) NOT NULL COMMENT '作业标题',
    `deadline` DATETIME NOT NULL COMMENT '截止时间',
    `max_score` INT NOT NULL COMMENT '满分分值',
    `student_status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '当前学生状态：PENDING-待完成，SUBMITTED-已提交，GRADED-已批改，OVERDUE-逾期',
    `assignment_create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_deadline` (`deadline`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业表';


-- =============================================
-- 7. 题目表
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
    PRIMARY KEY (`id`),
    KEY `idx_assignment_id` (`assignment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';


-- =============================================
-- 8. 客观题提交表
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
-- 9. 主观题提交表
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
-- 10. 课堂签到表
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
-- 11. 课堂投票表
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
-- 12. 投票记录表
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
-- 13. 课堂聊天表
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
-- 14. 自习室记录表
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
    `screen_switch_count` INT DEFAULT '0' COMMENT '切屏次数',
    `is_valid` TINYINT DEFAULT '1' COMMENT '是否有效：1-有效，0-无效（切屏次数超3次）',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_goal` (`goal`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自习室记录表';


-- =============================================
-- 15. 积分记录表
-- =============================================
CREATE TABLE `points_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '积分记录ID',
    `user_id` BIGINT NOT NULL COMMENT '学生ID',
    `change_type` TINYINT NOT NULL COMMENT '变动类型：1-获取，2-消耗',
    `change_points` INT NOT NULL COMMENT '变动积分数（正数）',
    `left_points` INT NOT NULL COMMENT '变动后余额',
    `source_type` TINYINT DEFAULT NULL COMMENT '来源/用途：1-课堂签到，2-课堂投票正确，3-作业分数，4-自习时长，5-道具兑换',
    `change_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '变动时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_change_time` (`change_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分记录表';


-- =============================================
-- 16. 花卉品种配置表
-- =============================================
CREATE TABLE `seed` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '品种ID',
    `variety` VARCHAR(50) NOT NULL COMMENT '品种名称',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '简介',
    `max_growth` INT NOT NULL COMMENT '最大生长值',
    `image` VARCHAR(500) DEFAULT NULL COMMENT '图片URL',
    `stage0_image` VARCHAR(500) DEFAULT NULL COMMENT '种子阶段图片',
    `stage1_image` VARCHAR(500) DEFAULT NULL COMMENT '发芽阶段图片',
    `stage2_image` VARCHAR(500) DEFAULT NULL COMMENT '幼苗阶段图片',
    `stage3_image` VARCHAR(500) DEFAULT NULL COMMENT '开花阶段图片',
    `price` INT NOT NULL COMMENT '购买价格（积分）',
    `is_deleted` TINYINT DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='花卉品种配置表';


-- =============================================
-- 17. 花卉实例表
-- =============================================
CREATE TABLE `flower` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '花卉ID',
    `user_id` BIGINT NOT NULL COMMENT '学生ID',
    `seed_id` BIGINT NOT NULL COMMENT '品种ID',
    `growth_value` INT DEFAULT '0' COMMENT '当前生长值',
    `stage` TINYINT DEFAULT '0' COMMENT '生长阶段：0-种子，1-发芽，2-幼苗，3-开花',
    `is_unlocked` TINYINT DEFAULT '0' COMMENT '是否解锁图鉴：0-未解锁，1-已解锁',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_seed_id` (`seed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='花卉实例表';


-- =============================================
-- 18. 商店道具表
-- =============================================
CREATE TABLE `shop_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '道具ID',
    `item_name` VARCHAR(50) NOT NULL COMMENT '道具名称',
    `icon` VARCHAR(500) DEFAULT NULL COMMENT '道具图标url',
    `price` INT NOT NULL COMMENT '所需积分',
    `growth_value` INT DEFAULT '10' COMMENT '使用可提升生长值',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_price` (`price`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商店道具表';


-- =============================================
-- 19. 系统通知表
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
-- 20. 学情分析表
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
-- 21. 敏感词表
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
-- 22. 课程问题表
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
-- 23. 课程问题回复表
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
-- 测试数据
-- =============================================

-- 1. 用户
INSERT INTO `user` (`id`, `name`, `password`, `phone`, `bio`, `avatar`, `role`, `openid`, `status`, `created_at`, `updated_at`) VALUES
(1, '张老师', 'encrypted_pwd_1', '13800000001', '资深Java讲师', 'https://example.com/avatar1.jpg', 2, 'wx_openid_001', 1, NOW(), NOW()),
(2, '李小明', 'encrypted_pwd_2', '13800000002', '大三学生，热爱编程', 'https://example.com/avatar2.jpg', 1, 'wx_openid_002', 1, NOW(), NOW()),
(3, '王小红', 'encrypted_pwd_3', '13800000003', '大二学生，喜欢设计', 'https://example.com/avatar3.jpg', 1, 'wx_openid_003', 1, NOW(), NOW()),
(4, 'admin', 'encrypted_pwd_4', '13800000004', '系统管理员', 'https://example.com/avatar4.jpg', 3, NULL, 1, NOW(), NOW());

-- 2. 课程
INSERT INTO `course` (`id`, `user_id`, `course_title`, `description`, `cover`, `student_count`, `course_code`, `status`, `audit_status`, `audit_remark`, `audit_time`, `created_at`, `updated_at`) VALUES
(1, 1, 'Java高级编程', '深入理解Java虚拟机、并发编程', 'https://example.com/course1.jpg', 2, 'JAVA2026', 'active', 1, '', NOW(), NOW(), NOW()),
(2, 1, '数据库系统原理', 'MySQL、事务、索引优化', 'https://example.com/course2.jpg', 1, 'DB2026', 'archived', 0, '待审核', NULL, NOW(), NOW());

-- 3. 课程-学生关联
INSERT INTO `course_student` (`id`, `course_id`, `user_id`, `joined_at`) VALUES
(1, 1, 2, NOW()), (2, 1, 3, NOW()), (3, 2, 2, NOW());

-- 4. 课程评价
INSERT INTO `course_review` (`id`, `course_id`, `user_id`, `star`, `review_content`, `likecount`, `review_create_time`) VALUES
(1, 1, 2, 5, '讲得很清晰，受益匪浅！', 3, NOW()),
(2, 1, 3, 4, '内容充实，但语速稍快', 1, NOW());

-- 5. 课堂
INSERT INTO `class` (`id`, `course_id`, `user_id`, `class_title`, `file_url`, `start_time`, `end_time`, `status`, `create_time`) VALUES
(1, 1, 1, '第一讲：JVM内存模型', 'https://example.com/ppt1.pptx', '2026-07-01 09:00:00', '2026-07-01 11:00:00', 'ended', NOW()),
(2, 1, 1, '第二讲：垃圾回收机制', 'https://example.com/ppt2.pptx', '2026-07-08 09:00:00', '2026-07-08 11:00:00', 'active', NOW()),
(3, 2, 1, '第一讲：事务隔离级别', NULL, '2026-06-20 14:00:00', '2026-06-20 16:00:00', 'ended', NOW());

-- 6. 作业
INSERT INTO `assignment` (`id`, `course_id`, `assignment_title`, `deadline`, `max_score`, `student_status`, `assignment_create_time`, `updated_at`) VALUES
(1, 1, '第一次作业：JVM内存划分', '2026-07-05 23:59:59', 100, 'SUBMITTED', NOW(), NOW()),
(2, 1, '第二次作业：垃圾回收算法', '2026-07-15 23:59:59', 100, 'PENDING', NOW(), NOW()),
(3, 2, '数据库事务练习', '2026-06-25 23:59:59', 50, 'OVERDUE', NOW(), NOW());

-- 7. 题目
INSERT INTO `question` (`id`, `assignment_id`, `type`, `stem`, `answer`, `explanation`, `perscore`, `sort_order`) VALUES
(1, 1, 1, 'Java中，以下哪个区域是线程私有的？', 'A', '程序计数器是线程私有的', 5, 1),
(2, 1, 2, '以下哪些属于JVM运行时数据区？', 'A,B,C', '方法区、堆、栈都是', 10, 2),
(3, 1, 3, '垃圾回收可以完全避免内存泄漏。', '错误', '不能完全避免', 5, 3),
(4, 1, 5, '请简述Java内存模型。', '略（主观题）', NULL, 20, 4),
(5, 2, 1, 'CMS垃圾回收器采用的算法是？', 'B', 'CMS是标记-清除', 10, 1),
(6, 2, 5, '请比较G1和ZGC的区别。', NULL, NULL, 30, 2),
(7, 3, 1, '事务的ACID特性中，C代表什么？', 'C', 'Consistency 一致性', 5, 1),
(8, 3, 4, 'SQL中用于提交事务的命令是______。', 'COMMIT', '', 5, 2),
(9, 3, 5, '请解释乐观锁与悲观锁。', NULL, NULL, 20, 3);

-- 8. 客观题提交
INSERT INTO `object_submit` (`id`, `assignment_id`, `question_id`, `user_id`, `object_score`, `answer_word`, `submit_time`) VALUES
(1, 1, 1, 2, 5, 'A', NOW()),
(2, 1, 2, 2, 10, 'A,B,C', NOW()),
(3, 1, 3, 2, 0, '正确', NOW()),
(4, 3, 7, 2, 5, 'C', NOW()),
(5, 3, 8, 2, 5, 'COMMIT', NOW());

-- 9. 主观题提交
INSERT INTO `subject_submit` (`id`, `assignment_id`, `user_id`, `question_id`, `answer_picture`, `subject_score`, `teacher_comment`, `finish_status`, `grading_status`, `finish_time`, `grading_time`) VALUES
(1, 1, 2, 4, 'https://example.com/answer1.jpg', 18, '回答较完整，但缺少内存模型具体细节', 2, 2, NOW(), NOW()),
(2, 2, 3, 6, 'https://example.com/answer2.jpg', NULL, NULL, 2, 1, NOW(), NULL),
(3, 3, 2, 9, 'https://example.com/answer3.jpg', 15, '举例清晰，但可再补充适用场景', 2, 2, NOW(), NOW());

-- 10. 课堂签到
INSERT INTO `class_check` (`id`, `class_id`, `user_id`, `check_status`, `checkin_time`) VALUES
(1, 1, 2, 1, '2026-07-01 08:55:00'),
(2, 1, 3, 2, '2026-07-01 09:15:00'),
(3, 2, 2, 1, '2026-07-08 08:50:00'),
(4, 2, 3, 3, NULL);

-- 11. 课堂投票
INSERT INTO `class_vote` (`id`, `class_id`, `heading`, `options`, `correct_option`, `duration`, `status`, `created_at`, `ended_at`) VALUES
(1, 1, 'Java中垃圾回收的触发时机？', '["A. 内存不足时", "B. 程序主动调用System.gc()", "C. 对象引用为null时", "D. 以上都是"]', 'D', 120, 'ended', NOW(), NOW()),
(2, 2, '以下哪些是并发编程的常见问题？', '["A. 死锁", "B. 活锁", "C. 线程饥饿", "D. 以上都是"]', 'D', 90, 'active', NOW(), NULL);

-- 12. 投票记录
INSERT INTO `vote_record` (`id`, `vote_id`, `user_id`, `selected_option`, `is_correct`, `submitted_at`) VALUES
(1, 1, 2, 'D', 1, NOW()),
(2, 1, 3, 'A', 0, NOW()),
(3, 2, 2, 'D', 1, NOW());

-- 13. 课堂聊天
INSERT INTO `class_chat` (`id`, `class_id`, `user_id`, `message_type`, `content`, `sent_time`) VALUES
(1, 1, 2, 1, '老师，JVM调优有什么常用工具？', NOW()),
(2, 1, 1, 1, '可以使用JVisualVM、Arthas等', NOW()),
(3, 1, 3, 2, 'https://example.com/chat_image.png', NOW()),
(4, 2, 2, 1, '今天讲GC吗？', NOW());

-- 14. 自习室记录
INSERT INTO `study_room` (`id`, `user_id`, `goal`, `mode`, `focus_mode`, `plan_time`, `start_time`, `end_time`, `total_time`, `screen_switch_count`, `is_valid`, `created_at`) VALUES
(1, 2, '复习JVM', 1, 0, NULL, '2026-07-01 08:00:00', '2026-07-01 10:30:00', 9000, 2, 1, NOW()),
(2, 2, '刷题', 2, 0, 7200, '2026-07-02 19:00:00', '2026-07-02 20:30:00', 5400, 4, 0, NOW()),
(3, 3, '学习数据库', 1, 0, NULL, '2026-07-03 14:00:00', '2026-07-03 16:00:00', 7200, 1, 1, NOW());

-- 15. 积分记录
INSERT INTO `points_record` (`id`, `user_id`, `change_type`, `change_points`, `left_points`, `source_type`, `change_time`) VALUES
(1, 2, 1, 10, 50, 1, NOW()),
(2, 2, 1, 5, 55, 2, NOW()),
(3, 2, 2, 20, 35, 5, NOW()),
(4, 3, 1, 10, 30, 1, NOW()),
(5, 3, 1, 15, 45, 3, NOW());

-- 16. 花卉品种
INSERT INTO `seed` (`id`, `variety`, `description`, `max_growth`, `image`, `stage0_image`, `stage1_image`, `stage2_image`, `stage3_image`, `price`) VALUES
(1, '向日葵', '向阳而生，充满活力的花朵', 100, 'https://example.com/seeds/sunflower.png', 'https://example.com/plants/sunflower_0.png', 'https://example.com/plants/sunflower_1.png', 'https://example.com/plants/sunflower_2.png', 'https://example.com/plants/sunflower_3.png', 0),
(2, '玫瑰', '热情似火，经典浪漫之选', 120, 'https://example.com/seeds/rose.png', 'https://example.com/plants/rose_0.png', 'https://example.com/plants/rose_1.png', 'https://example.com/plants/rose_2.png', 'https://example.com/plants/rose_3.png', 50),
(3, '仙人掌', '坚韧不拔，耐旱易养', 80, 'https://example.com/seeds/cactus.png', 'https://example.com/plants/cactus_0.png', 'https://example.com/plants/cactus_1.png', 'https://example.com/plants/cactus_2.png', 'https://example.com/plants/cactus_3.png', 30),
(4, '樱花', '刹那芳华，绚烂而短暂', 150, 'https://example.com/seeds/cherry.png', 'https://example.com/plants/cherry_0.png', 'https://example.com/plants/cherry_1.png', 'https://example.com/plants/cherry_2.png', 'https://example.com/plants/cherry_3.png', 80);

-- 17. 花卉实例
INSERT INTO `flower` (`id`, `user_id`, `seed_id`, `growth_value`, `stage`, `is_unlocked`, `created_at`, `updated_at`) VALUES
(1, 2, 2, 65, 3, 1, NOW(), NOW()),
(2, 2, 1, 20, 1, 0, NOW(), NOW()),
(3, 3, 2, 40, 2, 0, NOW(), NOW());

-- 18. 商店道具
INSERT INTO `shop_item` (`id`, `item_name`, `icon`, `price`, `growth_value`, `created_at`) VALUES
(1, '魔法肥料', 'https://example.com/fertilizer.png', 20, 15, NOW()),
(2, '加速药水', 'https://example.com/potion.png', 15, 10, NOW()),
(3, '稀有种子', 'https://example.com/seed.png', 50, 30, NOW());

-- 19. 系统通知
INSERT INTO `notice` (`id`, `user_id`, `notice_title`, `notice_content`, `notice_status`, `notice_type`, `push_time`) VALUES
(1, 2, '作业批改通知', '您的第一次作业已批改，得分85', 1, 4, '2026-07-01 10:00:00'),
(2, 2, '课堂提醒', '第二堂课将于7月8日开始，请准时参加', 0, 1, '2026-07-01 09:00:00'),
(3, 3, '作业截止提醒', '您的第二次作业即将截止，请尽快提交', 0, 2, '2026-06-30 10:00:00'),
(4, 2, '系统升级通知', '平台将于7月1日凌晨2:00-5:00进行系统升级', 0, 0, '2026-06-29 10:00:00'),
(5, 2, '第三节上课提醒', '「Java高级编程」第三讲将于7月15日14:00开始', 0, 1, '2026-06-29 09:00:00'),
(6, 3, '新作业发布', '第三次作业：并发编程练习已发布，截止日期7月20日', 0, 2, '2026-06-28 15:00:00'),
(7, 2, '作业提交提醒', '李小明提交了第2章作业，等待批改', 0, 3, '2026-06-28 14:00:00'),
(8, 3, '作业批改通知', '您的第二次作业已批改，得分92', 1, 4, '2026-06-28 11:00:00'),
(9, 2, '课程通知', '暑期课程安排已更新，请查看最新课表', 0, 0, '2026-06-27 16:00:00'),
(10, 3, '上课提醒', '「数据库系统原理」本周五课程取消', 0, 1, '2026-06-27 10:00:00');

-- 20. 学情分析
INSERT INTO `student_analysis` (`id`, `user_id`, `total_study_duration`, `assignment_correct_rate`, `week_study_duration`, `updated_at`) VALUES
(1, 2, 14400, 85.50, 3600, NOW()),
(2, 3, 7200, 70.00, 1800, NOW());

-- 21. 敏感词
INSERT INTO `sensitive_word` (`word`) VALUES
('色情'), ('暴力'), ('赌博'), ('毒品'), ('政治敏感');

-- 22. 课程问题
INSERT INTO `course_problem` (`id`, `course_id`, `user_id`, `title`, `content`, `reply_count`, `created_at`) VALUES
(1, 1, 2, 'JVM堆内存调优参数', '请问-Xms和-Xmx设置多大比较合适？', 2, NOW()),
(2, 1, 3, 'G1垃圾回收器的工作机制', 'G1相比CMS有什么优势？', 1, NOW()),
(3, 2, 2, 'MySQL的隔离级别怎么选', 'Repeatable Read和Read Committed在实际项目中怎么选？', 0, NOW());

-- 23. 课程问题回复
INSERT INTO `course_problem_reply` (`id`, `problem_id`, `user_id`, `content`, `created_at`) VALUES
(1, 1, 1, '一般建议初始堆(-Xms)和最大堆(-Xmx)设为相同值，避免运行时动态调整。具体大小根据应用内存需求，通常设为系统内存的1/4到1/2。', NOW()),
(2, 1, 3, '补充一下，如果机器内存较大（32G+），可以适当增大，但不要超过物理内存的70%。', NOW()),
(3, 2, 1, 'G1的主要优势是可控的停顿时间(-XX:MaxGCPauseMillis)，适合大堆内存场景（6G+），并且会自动进行内存压缩，减少碎片。', NOW());
