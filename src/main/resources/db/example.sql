-- =============================================
-- 插入测试数据（所有表）
-- =============================================

-- ----------------------------
-- 1. 用户表 (user)
-- ----------------------------
INSERT INTO `user` (`id`, `name`, `password`, `phone`, `bio`, `avatar`, `role`, `openid`, `status`, `created_at`, `updated_at`) VALUES
                                                                                                                                    (1, '张老师', 'encrypted_pwd_1', '13800000001', '资深Java讲师', 'https://example.com/avatar1.jpg', 2, 'wx_openid_001', 1, NOW(), NOW()),
                                                                                                                                    (2, '李小明', 'encrypted_pwd_2', '13800000002', '大三学生，热爱编程', 'https://example.com/avatar2.jpg', 1, 'wx_openid_002', 1, NOW(), NOW()),
                                                                                                                                    (3, '王小红', 'encrypted_pwd_3', '13800000003', '大二学生，喜欢设计', 'https://example.com/avatar3.jpg', 1, 'wx_openid_003', 1, NOW(), NOW()),
                                                                                                                                    (4, 'admin', 'encrypted_pwd_4', '13800000004', '系统管理员', 'https://example.com/avatar4.jpg', 3, NULL, 1, NOW(), NOW());

-- ----------------------------
-- 2. 课程表 (course)
-- ----------------------------
INSERT INTO `course` (`id`, `user_id`, `course_title`, `description`, `cover`, `student_count`, `course_code`, `status`, `created_at`, `updated_at`) VALUES
                                                                                                                                                         (1, 1, 'Java高级编程', '深入理解Java虚拟机、并发编程', 'https://example.com/course1.jpg', 2, 'JAVA2026', 'active', NOW(), NOW()),
                                                                                                                                                         (2, 1, '数据库系统原理', 'MySQL、事务、索引优化', 'https://example.com/course2.jpg', 1, 'DB2026', 'archived', NOW(), NOW());

-- ----------------------------
-- 3. 课程-学生关联表 (course_student)
-- ----------------------------
INSERT INTO `course_student` (`id`, `course_id`, `user_id`, `joined_at`) VALUES
                                                                             (1, 1, 2, NOW()),
                                                                             (2, 1, 3, NOW()),
                                                                             (3, 2, 2, NOW());

-- ----------------------------
-- 4. 课堂表 (class)
-- ----------------------------
INSERT INTO `class` (`id`, `course_id`, `user_id`, `class_title`, `file_url`, `start_time`, `end_time`, `status`, `created_at`, `updated_at`) VALUES
                                                                                                                                                  (1, 1, 1, '第一讲：JVM内存模型', 'https://example.com/ppt1.pptx', '2026-07-01 09:00:00', '2026-07-01 11:00:00', 'ended', NOW(), NOW()),
                                                                                                                                                  (2, 1, 1, '第二讲：垃圾回收机制', 'https://example.com/ppt2.pptx', '2026-07-08 09:00:00', '2026-07-08 11:00:00', 'active', NOW(), NOW()),
                                                                                                                                                  (3, 2, 1, '第一讲：事务隔离级别', NULL, '2026-06-20 14:00:00', '2026-06-20 16:00:00', 'ended', NOW(), NOW());

-- ----------------------------
-- 5. 课程评价表 (course_review)
-- ----------------------------
INSERT INTO `course_review` (`id`, `course_id`, `user_id`, `star`, `review_content`, `likecount`, `review_create_time`) VALUES
                                                                                                                            (1, 1, 2, 5, '讲得很清晰，受益匪浅！', 3, NOW()),
                                                                                                                            (2, 1, 3, 4, '内容充实，但语速稍快', 1, NOW());

-- ----------------------------
-- 6. 作业表 (assignment)
-- ----------------------------
INSERT INTO `assignment` (`id`, `course_id`, `assignment_title`, `deadline`, `max_score`, `student_status`, `assignment_create_time`, `updated_at`) VALUES
                                                                                                                                                        (1, 1, '第一次作业：JVM内存划分', '2026-07-05 23:59:59', 100, 'SUBMITTED', NOW(), NOW()),
                                                                                                                                                        (2, 1, '第二次作业：垃圾回收算法', '2026-07-15 23:59:59', 100, 'PENDING', NOW(), NOW()),
                                                                                                                                                        (3, 2, '数据库事务练习', '2026-06-25 23:59:59', 50, 'OVERDUE', NOW(), NOW());

-- ----------------------------
-- 7. 题目表 (question)
-- ----------------------------
INSERT INTO `question` (`id`, `assignment_id`, `type`, `stem`, `answer`, `explanation`, `perscore`, `sort_order`) VALUES
-- 作业1的题目
(1, 1, 1, 'Java中，以下哪个区域是线程私有的？', 'A', '程序计数器是线程私有的', 5, 1),
(2, 1, 2, '以下哪些属于JVM运行时数据区？', 'A,B,C', '方法区、堆、栈都是', 10, 2),
(3, 1, 3, '垃圾回收可以完全避免内存泄漏。', '错误', '不能完全避免', 5, 3),
(4, 1, 5, '请简述Java内存模型。', '略（主观题）', NULL, 20, 4),
-- 作业2的题目
(5, 2, 1, 'CMS垃圾回收器采用的算法是？', 'B', 'CMS是标记-清除', 10, 1),
(6, 2, 5, '请比较G1和ZGC的区别。', NULL, NULL, 30, 2),
-- 作业3的题目
(7, 3, 1, '事务的ACID特性中，C代表什么？', 'C', 'Consistency 一致性', 5, 1),
(8, 3, 4, 'SQL中用于提交事务的命令是______。', 'COMMIT', '', 5, 2),
(9, 3, 5, '请解释乐观锁与悲观锁。', NULL, NULL, 20, 3);

-- ----------------------------
-- 8. 客观题提交表 (object_submit)
-- ----------------------------
INSERT INTO `object_submit` (`id`, `assignment_id`, `question_id`, `user_id`, `object_score`, `answer_word`, `submit_time`) VALUES
-- 李小明提交作业1的客观题
(1, 1, 1, 2, 5, 'A', NOW()),
(2, 1, 2, 2, 10, 'A,B,C', NOW()),
(3, 1, 3, 2, 0, '正确', NOW()),
-- 李小明提交作业3的客观题
(4, 3, 7, 2, 5, 'C', NOW()),
(5, 3, 8, 2, 5, 'COMMIT', NOW());

-- ----------------------------
-- 9. 主观题提交表 (subject_submit)
-- ----------------------------
INSERT INTO `subject_submit` (`id`, `assignment_id`, `user_id`, `question_id`, `answer_picture`, `subject_score`, `teacher_comment`, `finish_status`, `grading_status`, `finish_time`, `grading_time`) VALUES
-- 李小明提交作业1的主观题，已批改
(1, 1, 2, 4, 'https://example.com/answer1.jpg', 18, '回答较完整，但缺少内存模型具体细节', 2, 2, NOW(), NOW()),
-- 王小红提交作业2的主观题，待批改
(2, 2, 3, 6, 'https://example.com/answer2.jpg', NULL, NULL, 2, 1, NOW(), NULL),
-- 李小明提交作业3的主观题，已批改
(3, 3, 2, 9, 'https://example.com/answer3.jpg', 15, '举例清晰，但可再补充适用场景', 2, 2, NOW(), NOW());

-- ----------------------------
-- 10. 课堂签到表 (class_check)
-- ----------------------------
INSERT INTO `class_check` (`id`, `class_id`, `user_id`, `check_status`, `checkin_time`) VALUES
                                                                                            (1, 1, 2, 1, '2026-07-01 08:55:00'), -- 正常
                                                                                            (2, 1, 3, 2, '2026-07-01 09:15:00'), -- 迟到
                                                                                            (3, 2, 2, 1, '2026-07-08 08:50:00'),
                                                                                            (4, 2, 3, 3, NULL); -- 缺勤（签到时间可为空）

-- ----------------------------
-- 11. 课堂投票表 (class_vote)
-- ----------------------------
INSERT INTO `class_vote` (`id`, `class_id`, `heading`, `options`, `correct_option`, `duration`, `status`, `created_at`, `ended_at`) VALUES
                                                                                                                                        (1, 1, 'Java中垃圾回收的触发时机？', '["A. 内存不足时", "B. 程序主动调用System.gc()", "C. 对象引用为null时", "D. 以上都是"]', 'D', 120, 'ended', NOW(), NOW()),
                                                                                                                                        (2, 2, '以下哪些是并发编程的常见问题？', '["A. 死锁", "B. 活锁", "C. 线程饥饿", "D. 以上都是"]', 'D', 90, 'active', NOW(), NULL);

-- ----------------------------
-- 12. 投票记录表 (vote_record)
-- ----------------------------
INSERT INTO `vote_record` (`id`, `vote_id`, `user_id`, `selected_option`, `is_correct`, `submitted_at`) VALUES
                                                                                                            (1, 1, 2, 'D', 1, NOW()),
                                                                                                            (2, 1, 3, 'A', 0, NOW()),
                                                                                                            (3, 2, 2, 'D', 1, NOW());

-- ----------------------------
-- 13. 课堂聊天表 (class_chat)
-- ----------------------------
INSERT INTO `class_chat` (`id`, `class_id`, `user_id`, `message_type`, `content`, `sent_time`) VALUES
                                                                                                   (1, 1, 2, 1, '老师，JVM调优有什么常用工具？', NOW()),
                                                                                                   (2, 1, 1, 1, '可以使用JVisualVM、Arthas等', NOW()),
                                                                                                   (3, 1, 3, 2, 'https://example.com/chat_image.png', NOW()),
                                                                                                   (4, 2, 2, 1, '今天讲GC吗？', NOW());

-- ----------------------------
-- 14. 自习室记录表 (study_room)
-- ----------------------------
INSERT INTO `study_room` (`id`, `user_id`, `mode`, `plan_time`, `start_time`, `end_time`, `total_time`, `screen_switch_count`, `is_valid`, `created_at`) VALUES
                                                                                                                                                             (1, 2, 1, NULL, '2026-07-01 08:00:00', '2026-07-01 10:30:00', 9000, 2, 1, NOW()),
                                                                                                                                                             (2, 2, 2, 7200, '2026-07-02 19:00:00', '2026-07-02 20:30:00', 5400, 4, 0, NOW()), -- 无效（切屏超3次）
                                                                                                                                                             (3, 3, 1, NULL, '2026-07-03 14:00:00', '2026-07-03 16:00:00', 7200, 1, 1, NOW());

-- ----------------------------
-- 15. 积分记录表 (points_record)
-- ----------------------------
INSERT INTO `points_record` (`id`, `user_id`, `change_type`, `change_points`, `left_points`, `source_type`, `change_time`) VALUES
                                                                                                                               (1, 2, 1, 10, 50, 1, NOW()), -- 签到
                                                                                                                               (2, 2, 1, 5, 55, 2, NOW()), -- 投票正确
                                                                                                                               (3, 2, 2, 20, 35, 5, NOW()), -- 兑换道具
                                                                                                                               (4, 3, 1, 10, 30, 1, NOW()),
                                                                                                                               (5, 3, 1, 15, 45, 3, NOW()); -- 作业得分

-- ----------------------------
-- 16. 花卉表 (flower)
-- ----------------------------
INSERT INTO `flower` (`id`, `user_id`, `variety`, `growth_growth_value`, `stage`, `is_unlocked`, `created_at`, `updated_at`) VALUES
                                                                                                                                 (1, 2, '玫瑰', 65, 3, 1, NOW(), NOW()),
                                                                                                                                 (2, 2, '向日葵', 20, 1, 0, NOW(), NOW()),
                                                                                                                                 (3, 3, '百合', 40, 2, 0, NOW(), NOW());

-- ----------------------------
-- 17. 商店道具表 (shop_item)
-- ----------------------------
INSERT INTO `shop_item` (`id`, `item_name`, `icon`, `price`, `growth_value`, `created_at`) VALUES
                                                                                               (1, '魔法肥料', 'https://example.com/fertilizer.png', 20, 15, NOW()),
                                                                                               (2, '加速药水', 'https://example.com/potion.png', 15, 10, NOW()),
                                                                                               (3, '稀有种子', 'https://example.com/seed.png', 50, 30, NOW());

-- ----------------------------
-- 18. 系统通知表 (notice)
-- ----------------------------
INSERT INTO `notice` (`id`, `user_id`, `notice_title`, `notice_content`, `notice_status`, `push_time`) VALUES
                                                                                                           (1, 2, '作业批改通知', '您的第一次作业已批改，得分85', 1, NOW()),
                                                                                                           (2, 2, '课堂提醒', '第二堂课将于7月8日开始，请准时参加', 0, NOW()),
                                                                                                           (3, 3, '作业截止提醒', '您的第二次作业即将截止，请尽快提交', 0, NOW());

-- ----------------------------
-- 19. 学情分析表 (student_analysis)
-- ----------------------------
INSERT INTO `student_analysis` (`id`, `user_id`, `total_study_duration`, `assignment_correct_rate`, `week_study_duration`, `updated_at`) VALUES
                                                                                                                                             (1, 2, 14400, 85.50, 3600, NOW()),
                                                                                                                                             (2, 3, 7200, 70.00, 1800, NOW());