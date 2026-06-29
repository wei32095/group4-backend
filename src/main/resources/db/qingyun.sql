-- =============================================
-- 1. 用户表（user）
-- =============================================
CREATE TABLE `user` (
                        `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
                        `name` VARCHAR(50) NOT NULL COMMENT '姓名',
                        `password` VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
                        `phone` VARCHAR(20) UNIQUE NOT NULL COMMENT '手机号',
                        `bio` VARCHAR(200) DEFAULT NULL COMMENT '个人简介',
                        `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像',
                        `role` TINYINT DEFAULT 1 COMMENT '角色：1-学生，2-教师，3-管理员',
                        `openid` VARCHAR(100) DEFAULT NULL COMMENT '微信标识',
                        `status` TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
                        `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        INDEX idx_phone (`phone`),
                        INDEX idx_role (`role`),
                        INDEX idx_openid (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';


-- =============================================
-- 2. 课程表（course）
-- =============================================
CREATE TABLE `course` (
                          `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
                          `user_id` BIGINT NOT NULL COMMENT '教师ID',
                          `course_title` VARCHAR(100) NOT NULL COMMENT '课程名称',
                          `description` TEXT COMMENT '课程描述',
                          `cover` VARCHAR(500) DEFAULT NULL COMMENT '封面图',
                          `student_count` INT DEFAULT 0 COMMENT '学生人数',
                          `course_code` VARCHAR(50) UNIQUE NOT NULL COMMENT '课程码',
                          `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-进行中，archived-已归档',
                          `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          INDEX idx_user_id (`user_id`),
                          INDEX idx_course_code (`course_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';


-- =============================================
-- 3. 课程评价表（course_review）
-- =============================================
CREATE TABLE `course_review` (
                                 `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
                                 `course_id` BIGINT NOT NULL COMMENT '课程ID',
                                 `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                 `star` TINYINT NOT NULL COMMENT '星级评价（1-5）',
                                 `review_content` VARCHAR(500) DEFAULT NULL COMMENT '文字评价内容',
                                 `likecount` INT DEFAULT 0 COMMENT '点赞数',
                                 `review_create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
                                 UNIQUE KEY uk_course_user (`course_id`, `user_id`),
                                 INDEX idx_course_id (`course_id`),
                                 INDEX idx_user_id (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程评价表';


-- =============================================
-- 4. 课堂表（class）
-- =============================================
CREATE TABLE `class` (
                         `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课堂ID',
                         `course_id` BIGINT NOT NULL COMMENT '课程ID',
                         `user_id` BIGINT NOT NULL COMMENT '老师ID',
                         `class_title` VARCHAR(100) NOT NULL COMMENT '课堂名称',
                         `file_url` VARCHAR(500) DEFAULT NULL COMMENT '课件附件地址',
                         `start_time` DATETIME NOT NULL COMMENT '开始时间',
                         `end_time` DATETIME NOT NULL COMMENT '结束时间',
                         `status` VARCHAR(20) DEFAULT 'not_started' COMMENT '状态：not_started-未开始，active-进行中，ended-已结束',
                         `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                         INDEX idx_course_id (`course_id`),
                         INDEX idx_user_id (`user_id`),
                         INDEX idx_status_time (`status`, `start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课堂表';


-- =============================================
-- 5. 作业表（assignment）
-- =============================================
CREATE TABLE `assignment` (
                              `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '作业ID',
                              `course_id` BIGINT NOT NULL COMMENT '课程ID',
                              `assignment_title` VARCHAR(100) NOT NULL COMMENT '作业标题',
                              `deadline` DATETIME NOT NULL COMMENT '截止时间',
                              `max_score` INT NOT NULL COMMENT '满分分值',
                              `student_status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '当前学生状态：PENDING-待完成，SUBMITTED-已提交，GRADED-已批改，OVERDUE-逾期',
                              `assignment_create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
                              `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              INDEX idx_course_id (`course_id`),
                              INDEX idx_deadline (`deadline`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业表';


-- =============================================
-- 6. 题目表（question）
-- =============================================
CREATE TABLE `question` (
                            `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '题目ID',
                            `assignment_id` BIGINT NOT NULL COMMENT '所属作业ID',
                            `type` TINYINT NOT NULL COMMENT '题型：1-单选题，2-多选题，3-判断题，4-填空题，5-主观题',
                            `stem` TEXT NOT NULL COMMENT '题干',
                            `answer` TEXT DEFAULT NULL COMMENT '正确答案',
                            `explanation` TEXT DEFAULT NULL COMMENT '解析',
                            `perscore` INT NOT NULL COMMENT '每题分值',
                            `sort_order` INT DEFAULT 0 COMMENT '排序序号',
                            INDEX idx_assignment_id (`assignment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';


-- =============================================
-- 7. 主观题提交表（subject_submit）
-- =============================================
CREATE TABLE `subject_submit` (
                                  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '提交ID',
                                  `assignment_id` BIGINT NOT NULL COMMENT '作业ID',
                                  `user_id` BIGINT NOT NULL COMMENT '学生ID',
                                  `question_id` BIGINT NOT NULL COMMENT '题目ID',
                                  `answer_picture` VARCHAR(500) DEFAULT NULL COMMENT '答题图片',
                                  `subject_score` INT DEFAULT NULL COMMENT '主观题得分',
                                  `teacher_comment` VARCHAR(500) DEFAULT NULL COMMENT '老师评语',
                                  `finish_status` TINYINT DEFAULT 1 COMMENT '学生完成状态：1-待完成，2-已完成',
                                  `grading_status` TINYINT DEFAULT 1 COMMENT '老师批改状态：1-待批改，2-已批改',
                                  `finish_time` DATETIME DEFAULT NULL COMMENT '学生提交时间',
                                  `grading_time` DATETIME DEFAULT NULL COMMENT '老师批改时间',
                                  INDEX idx_assignment_id (`assignment_id`),
                                  INDEX idx_user_id (`user_id`),
                                  INDEX idx_question_id (`question_id`),
                                  INDEX idx_finish_status (`finish_status`),
                                  INDEX idx_grading_status (`grading_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='主观题提交表';


-- =============================================
-- 8. 客观题提交表（object_submit）
-- =============================================
CREATE TABLE `object_submit` (
                                 `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '提交ID',
                                 `assignment_id` BIGINT NOT NULL COMMENT '作业ID',
                                 `question_id` BIGINT NOT NULL COMMENT '题目ID',
                                 `user_id` BIGINT NOT NULL COMMENT '学生ID',
                                 `object_score` INT DEFAULT 0 COMMENT '客观题得分',
                                 `answer_word` VARCHAR(500) DEFAULT NULL COMMENT '学生答案',
                                 `submit_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '学生提交时间',
                                 INDEX idx_assignment_id (`assignment_id`),
                                 INDEX idx_question_id (`question_id`),
                                 INDEX idx_user_id (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客观题提交表';


-- =============================================
-- 9. 课堂签到表（class_check）
-- =============================================
CREATE TABLE `class_check` (
                               `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '签到ID',
                               `class_id` BIGINT NOT NULL COMMENT '课堂ID',
                               `user_id` BIGINT NOT NULL COMMENT '学生ID',
                               `check_status` TINYINT DEFAULT 1 COMMENT '签到状态：1-正常，2-迟到，3-缺勤',
                               `checkin_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '签到时间',
                               UNIQUE KEY uk_class_user (`class_id`, `user_id`),
                               INDEX idx_class_id (`class_id`),
                               INDEX idx_user_id (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课堂签到表';


-- =============================================
-- 10. 课堂投票表（class_vote）
-- =============================================
CREATE TABLE `class_vote` (
                              `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '投票ID',
                              `class_id` BIGINT NOT NULL COMMENT '课堂ID',
                              `heading` VARCHAR(255) NOT NULL COMMENT '选择题题目',
                              `options` JSON NOT NULL COMMENT '选项列表',
                              `correct_option` VARCHAR(10) DEFAULT NULL COMMENT '正确答案（自动判分）',
                              `duration` INT DEFAULT 120 COMMENT '投票时长（>0）',
                              `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-进行中，ended-已结束',
                              `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
                              `ended_at` DATETIME DEFAULT NULL COMMENT '结束时间',
                              INDEX idx_class_id (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课堂投票表';


-- =============================================
-- 11. 投票记录表（vote_record）
-- =============================================
CREATE TABLE `vote_record` (
                               `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
                               `vote_id` BIGINT NOT NULL COMMENT '投票ID',
                               `user_id` BIGINT NOT NULL COMMENT '学生ID',
                               `selected_option` VARCHAR(10) NOT NULL COMMENT '学生选的选项',
                               `is_correct` TINYINT DEFAULT 0 COMMENT '是否正确：0-错误，1-正确',
                               `submitted_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
                               UNIQUE KEY uk_vote_user (`vote_id`, `user_id`),
                               INDEX idx_vote_id (`vote_id`),
                               INDEX idx_user_id (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投票记录表';


-- =============================================
-- 12. 课堂聊天表（class_chat）
-- =============================================
CREATE TABLE `class_chat` (
                              `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
                              `class_id` BIGINT NOT NULL COMMENT '课堂ID',
                              `user_id` BIGINT NOT NULL COMMENT '发送人ID',
                              `message_type` TINYINT DEFAULT 1 COMMENT '消息类型：1-文字，2-图片，3-表情',
                              `content` VARCHAR(500) NOT NULL COMMENT '消息内容',
                              `sent_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
                              INDEX idx_class_id (`class_id`),
                              INDEX idx_user_id (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课堂聊天表';


-- =============================================
-- 13. 自习室记录表（study_room）
-- =============================================
CREATE TABLE `study_room` (
                              `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
                              `user_id` BIGINT NOT NULL COMMENT '学生ID',
                              `mode` TINYINT NOT NULL COMMENT '模式：1-正向计时，2-倒计时',
                              `plan_time` INT DEFAULT NULL COMMENT '计划时长（秒，倒计时用）',
                              `start_time` DATETIME NOT NULL COMMENT '开始时间',
                              `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
                              `total_time` INT DEFAULT NULL COMMENT '总时长（秒）',
                              `screen_switch_count` INT DEFAULT 0 COMMENT '切屏次数',
                              `is_valid` TINYINT DEFAULT 1 COMMENT '是否有效：1-有效，0-无效（切屏次数超3次）',
                              `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              INDEX idx_user_id (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自习室记录表';


-- =============================================
-- 14. 积分记录表（points_record）
-- =============================================
CREATE TABLE `points_record` (
                                 `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '积分记录ID',
                                 `user_id` BIGINT NOT NULL COMMENT '学生ID',
                                 `change_type` TINYINT NOT NULL COMMENT '变动类型：1-获取，2-消耗',
                                 `change_points` INT NOT NULL COMMENT '变动积分数（正数）',
                                 `left_points` INT NOT NULL COMMENT '变动后余额',
                                 `source_type` TINYINT DEFAULT NULL COMMENT '来源/用途：1-课堂签到，2-课堂投票正确，3-作业分数，4-自习时长，5-道具兑换',
                                 `change_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '变动时间',
                                 INDEX idx_user_id (`user_id`),
                                 INDEX idx_change_time (`change_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分记录表';


-- =============================================
-- 15. 花卉表（flower）
-- =============================================
CREATE TABLE `flower` (
                          `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '花卉ID',
                          `user_id` BIGINT NOT NULL COMMENT '学生ID',
                          `variety` VARCHAR(50) NOT NULL COMMENT '当前培育的花卉种类',
                          `growth_growth_value` INT DEFAULT 0 COMMENT '当前生长值（0~100）',
                          `stage` TINYINT DEFAULT 0 COMMENT '生长阶段：0-种子，1-发芽，2-幼苗，3-开花，4-成熟',
                          `is_unlocked` TINYINT DEFAULT 0 COMMENT '是否解锁图鉴：0-未解锁，1-已解锁',
                          `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          INDEX idx_user_id (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='花卉表';


-- =============================================
-- 16. 商店道具表（shop_item）
-- =============================================
CREATE TABLE `shop_item` (
                             `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '道具ID',
                             `item_name` VARCHAR(50) NOT NULL COMMENT '道具名称',
                             `icon` VARCHAR(500) DEFAULT NULL COMMENT '道具图标url',
                             `price` INT NOT NULL COMMENT '所需积分',
                             `growth_value` INT DEFAULT 10 COMMENT '使用可提升生长值',
                             `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             INDEX idx_price (`price`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商店道具表';


-- =============================================
-- 17. 系统通知表（notice）
-- =============================================
CREATE TABLE `notice` (
                          `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
                          `user_id` BIGINT NOT NULL COMMENT '用户ID',
                          `notice_title` VARCHAR(100) NOT NULL COMMENT '通知标题',
                          `notice_content` TEXT NOT NULL COMMENT '通知内容',
                          `notice_status` TINYINT DEFAULT 0 COMMENT '推送状态：0-未读，1-已读',
                          `push_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '推送时间',
                          INDEX idx_user_status_time (`user_id`, `notice_status`, `push_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统通知表';


-- =============================================
-- 18. 学情分析表（student_analysis）
-- =============================================
CREATE TABLE `student_analysis` (
                                    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分析ID',
                                    `user_id` BIGINT NOT NULL COMMENT '学生ID',
                                    `total_study_duration` INT DEFAULT 0 COMMENT '累计学习时长（秒）',
                                    `assignment_correct_rate` DECIMAL(5,2) DEFAULT 0.00 COMMENT '作业正确率（百分比）',
                                    `week_study_duration` INT DEFAULT 0 COMMENT '周学习时长（秒）',
                                    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    UNIQUE KEY uk_user_id (`user_id`),
                                    INDEX idx_user_id (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学情分析表';


-- =============================================
-- 19. 课程-学生关联表（course_student）
-- =============================================
CREATE TABLE `course_student` (
                                  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
                                  `course_id` BIGINT NOT NULL COMMENT '课程ID',
                                  `user_id` BIGINT NOT NULL COMMENT '学生ID',
                                  `joined_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
                                  UNIQUE KEY uk_course_student (`course_id`, `user_id`),
                                  INDEX idx_course_id (`course_id`),
                                  INDEX idx_student_id (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程-学生关联表';

-- =============================================
-- 20. 用户背包表（user_item）
-- =============================================
CREATE TABLE `user_item` (
                             `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
                             `user_id` BIGINT NOT NULL COMMENT '用户ID',
                             `item_id` BIGINT NOT NULL COMMENT '道具ID',
                             `quantity` INT DEFAULT 0 COMMENT '拥有数量',
                             `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             UNIQUE KEY uk_user_item (`user_id`, `item_id`),
                             INDEX idx_user_id (`user_id`),
                             INDEX idx_item_id (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户背包表';

-- 加列
ALTER TABLE notice ADD COLUMN notice_type TINYINT DEFAULT 0 COMMENT
    '通知类型：0-通用，1-上课提醒，2-作业发布，3-作业提交，4-批改完成' AFTER notice_status;

ALTER TABLE class
    DROP COLUMN created_at,
    DROP COLUMN updated_at,
    ADD COLUMN create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';