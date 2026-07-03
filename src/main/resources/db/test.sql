--=============================================
--测试数据
-- =============================================
-- =============================================
-- 继续插入积分记录数据（学生38-50）
-- =============================================
-- =============================================
-- 插入用户数据（10位老师 + 40位学生 + 1位管理员）
-- =============================================

-- 老师（10位，id: 1-10）
INSERT INTO `user` (`id`, `name`, `password`, `phone`, `bio`, `avatar`, `role`, `openid`, `status`, `points`, `ban_expire_time`, `ban_reason`, `created_at`) VALUES
                                                                                                                                                                 (1, '张明', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13800000001', '资深数学教师，教龄15年，擅长函数与几何', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/teacher1.jpg', 2, 'wx_teacher_001', 1, 0, NULL, NULL, '2024-01-01 08:00:00'),
                                                                                                                                                                 (2, '李芳', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13800000002', '英语教育专家，擅长语法与写作教学', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/teacher2.jpg', 2, 'wx_teacher_002', 1, 0, NULL, NULL, '2024-01-02 09:00:00'),
                                                                                                                                                                 (3, '王强', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13800000003', '物理竞赛教练，清北名师', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/teacher3.jpg', 2, 'wx_teacher_003', 1, 0, NULL, NULL, '2024-01-03 10:00:00'),
                                                                                                                                                                 (4, '陈丽', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13800000004', '化学博士，趣味化学教学', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/teacher4.jpg', 2, 'wx_teacher_004', 1, 0, NULL, NULL, '2024-01-04 11:00:00'),
                                                                                                                                                                 (5, '赵刚', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13800000005', '语文名师，高考作文专家', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/teacher5.jpg', 2, 'wx_teacher_005', 1, 0, NULL, NULL, '2024-01-05 12:00:00'),
                                                                                                                                                                 (6, '孙悦', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13800000006', '生物教师，擅长遗传学与生态学', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/teacher6.jpg', 2, 'wx_teacher_006', 1, 0, NULL, NULL, '2024-01-06 13:00:00'),
                                                                                                                                                                 (7, '周伟', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13800000007', '历史教师，擅长中国古代史', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/teacher7.jpg', 2, 'wx_teacher_007', 1, 0, NULL, NULL, '2024-01-07 14:00:00'),
                                                                                                                                                                 (8, '吴芳', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13800000008', '地理教师，擅长自然地理与气候', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/teacher8.jpg', 2, 'wx_teacher_008', 1, 0, NULL, NULL, '2024-01-08 15:00:00'),
                                                                                                                                                                 (9, '郑强', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13800000009', '政治教师，擅长马克思主义哲学', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/teacher9.jpg', 2, 'wx_teacher_009', 0, 0, '2026-08-01 00:00:00', '违规发布不当言论', '2024-01-09 16:00:00'),
                                                                                                                                                                 (10, '钱琳', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13800000010', '信息技术教师，擅长编程与AI', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/teacher10.jpg', 2, 'wx_teacher_010', 1, 0, NULL, NULL, '2024-01-10 17:00:00');

-- 学生（40位，id: 11-50）
INSERT INTO `user` (`id`, `name`, `password`, `phone`, `bio`, `avatar`, `role`, `openid`, `status`, `points`, `ban_expire_time`, `ban_reason`, `created_at`) VALUES
                                                                                                                                                                 (11, '刘洋', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000001', '热爱数学，目标满分', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student1.jpg', 1, 'wx_student_001', 1, 580, NULL, NULL, '2024-02-01 08:00:00'),
                                                                                                                                                                 (12, '周婷', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000002', '英语课代表，喜欢语法', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student2.jpg', 1, 'wx_student_002', 1, 320, NULL, NULL, '2024-02-02 09:00:00'),
                                                                                                                                                                 (13, '吴昊', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000003', '物理爱好者，竞赛选手', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student3.jpg', 1, 'wx_student_003', 1, 450, NULL, NULL, '2024-02-03 10:00:00'),
                                                                                                                                                                 (14, '郑雪', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000004', '化学课代表，喜欢实验', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student4.jpg', 1, 'wx_student_004', 1, 210, NULL, NULL, '2024-02-04 11:00:00'),
                                                                                                                                                                 (15, '孙鹏', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000005', '语文小才子，作文高手', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student5.jpg', 1, 'wx_student_005', 1, 670, NULL, NULL, '2024-02-05 12:00:00'),
                                                                                                                                                                 (16, '黄雨桐', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000006', '', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student6.jpg', 1, 'wx_student_006', 1, 180, NULL, NULL, '2024-02-06 13:00:00'),
                                                                                                                                                                 (17, '林浩', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000007', '目标是清华', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student7.jpg', 1, 'wx_student_007', 1, 890, NULL, NULL, '2024-02-07 14:00:00'),
                                                                                                                                                                 (18, '何欣', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000008', '', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student8.jpg', 1, 'wx_student_008', 1, 55, NULL, NULL, '2024-02-08 15:00:00'),
                                                                                                                                                                 (19, '罗凯', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000009', '喜欢物理和化学', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student9.jpg', 1, 'wx_student_009', 1, 420, NULL, NULL, '2024-02-09 16:00:00'),
                                                                                                                                                                 (20, '唐雅', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000010', '英语成绩年级前10', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student10.jpg', 1, 'wx_student_010', 1, 730, NULL, NULL, '2024-02-10 17:00:00'),
                                                                                                                                                                 (21, '沈悦', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000011', '', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student11.jpg', 1, 'wx_student_011', 1, 340, NULL, NULL, '2024-02-11 08:00:00'),
                                                                                                                                                                 (22, '韩冰', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000012', '数学课代表', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student12.jpg', 1, 'wx_student_012', 1, 920, NULL, NULL, '2024-02-12 09:00:00'),
                                                                                                                                                                 (23, '秦雨', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000013', '喜欢生物', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student13.jpg', 1, 'wx_student_013', 1, 280, NULL, NULL, '2024-02-13 10:00:00'),
                                                                                                                                                                 (24, '顾磊', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000014', '', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student14.jpg', 1, 'wx_student_014', 1, 150, NULL, NULL, '2024-02-14 11:00:00'),
                                                                                                                                                                 (25, '邵婷', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000015', '历史爱好者', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student15.jpg', 1, 'wx_student_015', 1, 510, NULL, NULL, '2024-02-15 12:00:00'),
                                                                                                                                                                 (26, '孟凯', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000016', '', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student16.jpg', 1, 'wx_student_016', 1, 60, NULL, NULL, '2024-02-16 13:00:00'),
                                                                                                                                                                 (27, '褚悦', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000017', '地理课代表', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student17.jpg', 1, 'wx_student_017', 1, 380, NULL, NULL, '2024-02-17 14:00:00'),
                                                                                                                                                                 (28, '丁浩', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000018', '', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student18.jpg', 1, 'wx_student_018', 1, 200, NULL, NULL, '2024-02-18 15:00:00'),
                                                                                                                                                                 (29, '欧阳雪', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000019', '喜欢写作文', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student19.jpg', 1, 'wx_student_019', 1, 640, NULL, NULL, '2024-02-19 16:00:00'),
                                                                                                                                                                 (30, '慕容枫', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000020', '', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student20.jpg', 1, 'wx_student_020', 1, 110, NULL, NULL, '2024-02-20 17:00:00'),
                                                                                                                                                                 (31, '陆静', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000021', '化学课代表', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student21.jpg', 1, 'wx_student_021', 1, 460, NULL, NULL, '2024-02-21 08:00:00'),
                                                                                                                                                                 (32, '柳阳', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000022', '', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student22.jpg', 1, 'wx_student_022', 1, 25, NULL, NULL, '2024-02-22 09:00:00'),
                                                                                                                                                                 (33, '卫兰', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000023', '喜欢生物', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student23.jpg', 1, 'wx_student_023', 1, 330, NULL, NULL, '2024-02-23 10:00:00'),
                                                                                                                                                                 (34, '尤娜', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000024', '', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student24.jpg', 1, 'wx_student_024', 0, 50, '2026-08-15 00:00:00', '恶意刷屏攻击他人', '2024-02-24 11:00:00'),
                                                                                                                                                                 (35, '邹华', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000025', '历史课代表', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student25.jpg', 1, 'wx_student_025', 1, 540, NULL, NULL, '2024-02-25 12:00:00'),
                                                                                                                                                                 (36, '贾玲', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000026', '', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student26.jpg', 1, 'wx_student_026', 1, 90, NULL, NULL, '2024-02-26 13:00:00'),
                                                                                                                                                                 (37, '程龙', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000027', '喜欢编程', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student27.jpg', 1, 'wx_student_027', 1, 780, NULL, NULL, '2024-02-27 14:00:00'),
                                                                                                                                                                 (38, '姚远', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000028', '', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student28.jpg', 1, 'wx_student_028', 1, 40, NULL, NULL, '2024-02-28 15:00:00'),
                                                                                                                                                                 (39, '段敏', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000029', '地理爱好者', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student29.jpg', 1, 'wx_student_029', 1, 290, NULL, NULL, '2024-03-01 16:00:00'),
                                                                                                                                                                 (40, '白杨', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000030', '', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student30.jpg', 1, 'wx_student_030', 1, 140, NULL, NULL, '2024-03-02 17:00:00'),
                                                                                                                                                                 (41, '万雪', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000031', '政治课代表', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student31.jpg', 1, 'wx_student_031', 1, 410, NULL, NULL, '2024-03-03 08:00:00'),
                                                                                                                                                                 (42, '苗青', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000032', '', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student32.jpg', 1, 'wx_student_032', 1, 75, NULL, NULL, '2024-03-04 09:00:00'),
                                                                                                                                                                 (43, '盛杰', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000033', '喜欢物理', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student33.jpg', 1, 'wx_student_033', 1, 500, NULL, NULL, '2024-03-05 10:00:00'),
                                                                                                                                                                 (44, '方文', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000034', '', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student34.jpg', 1, 'wx_student_034', 1, 120, NULL, NULL, '2024-03-06 11:00:00'),
                                                                                                                                                                 (45, '戴月', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000035', '喜欢文学', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student35.jpg', 1, 'wx_student_035', 1, 620, NULL, NULL, '2024-03-07 12:00:00'),
                                                                                                                                                                 (46, '谭晶', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000036', '', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student36.jpg', 1, 'wx_student_036', 1, 30, NULL, NULL, '2024-03-08 13:00:00'),
                                                                                                                                                                 (47, '韦华', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000037', '生物课代表', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student37.jpg', 1, 'wx_student_037', 1, 360, NULL, NULL, '2024-03-09 14:00:00'),
                                                                                                                                                                 (48, '罗琳', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000038', '', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student38.jpg', 1, 'wx_student_038', 1, 170, NULL, NULL, '2024-03-10 15:00:00'),
                                                                                                                                                                 (49, '谢军', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000039', '喜欢历史', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student39.jpg', 1, 'wx_student_039', 1, 440, NULL, NULL, '2024-03-11 16:00:00'),
                                                                                                                                                                 (50, '石磊', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '13900000040', '', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/student40.jpg', 1, 'wx_student_040', 1, 85, NULL, NULL, '2024-03-12 17:00:00');

-- 管理员（1位，id: 51）
INSERT INTO `user` (`id`, `name`, `password`, `phone`, `bio`, `avatar`, `role`, `openid`, `status`, `points`, `ban_expire_time`, `ban_reason`, `created_at`) VALUES
    (51, '系统管理员', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '18800000001', '平台超级管理员', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/avatar/admin.jpg', 3, 'wx_admin_001', 1, 0, NULL, NULL, '2024-01-01 00:00:00');


-- =============================================
-- 插入课程数据（15门课程，覆盖10位老师）
-- =============================================
INSERT INTO `course` (`id`, `user_id`, `course_title`, `description`, `cover`, `student_count`, `course_code`, `status`, `audit_status`, `audit_remark`, `audit_time`, `created_at`) VALUES
                                                                                                                                                                                         (1, 1, '初中数学·函数专题', '从一次函数到二次函数，系统掌握函数思想，适合初二初三学生', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/course/math_function.jpg', 12, 'MATH2024001', 'active', 1, '优质课程，审核通过', '2024-01-10 10:00:00', '2024-01-01 08:00:00'),
                                                                                                                                                                                         (2, 1, '高中数学·导数与微积分', '高等数学入门，导数定义、求导法则、微积分基本定理', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/course/math_derivative.jpg', 6, 'MATH2024002', 'active', 1, '审核通过', '2024-02-05 14:30:00', '2024-01-10 09:00:00'),
                                                                                                                                                                                         (3, 2, '初中英语·语法精讲', '时态、语态、从句、非谓语动词，一站式扫清语法障碍', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/course/english_grammar.jpg', 10, 'ENG2024001', 'active', 1, '审核通过', '2024-01-20 09:00:00', '2024-01-05 10:00:00'),
                                                                                                                                                                                         (4, 2, '高中英语·阅读理解', '快速阅读技巧、长难句分析、题型分类突破', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/course/english_reading.jpg', 4, 'ENG2024002', 'archived', 1, '审核通过', '2024-03-01 16:00:00', '2024-01-15 11:00:00'),
                                                                                                                                                                                         (5, 3, '初中物理·力学基础', '牛顿三定律、浮力压强、功与机械能，中考必考', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/course/physics_mechanics.jpg', 10, 'PHY2024001', 'active', 1, '审核通过', '2024-01-15 11:00:00', '2024-01-03 12:00:00'),
                                                                                                                                                                                         (6, 3, '高中物理·电磁学', '电场、磁场、电磁感应，高考压轴题专题', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/course/physics_electric.jpg', 5, 'PHY2024002', 'active', 1, '审核通过', '2024-02-20 08:30:00', '2024-01-20 13:00:00'),
                                                                                                                                                                                         (7, 4, '初中化学·元素与化合物', '常见元素性质、化学方程式、酸碱盐', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/course/chemistry_element.jpg', 8, 'CHEM2024001', 'active', 1, '审核通过', '2024-03-05 13:00:00', '2024-02-01 14:00:00'),
                                                                                                                                                                                         (8, 4, '高中化学·有机化学', '烃及其衍生物、有机反应类型、同分异构体', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/course/chemistry_organic.jpg', 3, 'CHEM2024002', 'active', 0, '', NULL, '2024-02-15 15:00:00'),
                                                                                                                                                                                         (9, 5, '初中语文·文言文专题', '实词虚词、文言句式、名篇背诵与翻译', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/course/chinese_wenyan.jpg', 6, 'CHN2024001', 'active', 1, '审核通过', '2024-02-01 10:30:00', '2024-01-08 16:00:00'),
                                                                                                                                                                                         (10, 5, '高中语文·作文写作', '议论文结构、素材积累、语言表达提升', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/course/chinese_composition.jpg', 4, 'CHN2024002', 'active', 1, '审核通过', '2024-03-15 09:00:00', '2024-01-25 17:00:00'),
                                                                                                                                                                                         (11, 6, '初中生物·遗传与进化', '基因、DNA、自然选择，生物竞赛基础', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/course/bio_genetics.jpg', 5, 'BIO2024001', 'active', 1, '审核通过', '2024-02-10 10:00:00', '2024-01-12 08:00:00'),
                                                                                                                                                                                         (12, 7, '初中历史·中国古代史', '从夏商周到明清，梳理五千年文明脉络', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/course/history_ancient.jpg', 4, 'HIS2024001', 'active', 1, '审核通过', '2024-02-15 14:00:00', '2024-01-18 09:00:00'),
                                                                                                                                                                                         (13, 8, '初中地理·自然地理', '气候、地形、水文、土壤，会考必备', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/course/geo_nature.jpg', 3, 'GEO2024001', 'active', 1, '审核通过', '2024-03-01 09:00:00', '2024-01-22 10:00:00'),
                                                                                                                                                                                         (14, 9, '高中政治·哲学基础', '唯物论、辩证法、认识论，高考政治难点突破', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/course/politics_philosophy.jpg', 2, 'POL2024001', 'active', 2, '课程内容部分观点有争议，请重新审阅', '2024-03-10 16:00:00', '2024-02-01 11:00:00'),
                                                                                                                                                                                         (15, 10, 'Python编程入门', '变量、循环、函数、面向对象，零基础也能学', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/course/python_basic.jpg', 8, 'CS2024001', 'active', 1, '优质课程，审核通过', '2024-03-20 10:00:00', '2024-02-10 12:00:00');


-- =============================================
-- 插入课程-学生关联数据（覆盖40位学生）
-- =============================================
INSERT INTO `course_student` (`course_id`, `user_id`, `joined_at`) VALUES
-- 课程1：函数专题（12人）
(1, 11, '2024-01-15 08:00:00'), (1, 12, '2024-01-16 09:00:00'), (1, 13, '2024-01-16 10:00:00'),
(1, 14, '2024-01-17 08:00:00'), (1, 15, '2024-01-17 09:00:00'), (1, 16, '2024-01-18 10:00:00'),
(1, 17, '2024-01-18 11:00:00'), (1, 18, '2024-01-19 08:00:00'), (1, 19, '2024-01-19 09:00:00'),
(1, 20, '2024-01-20 10:00:00'), (1, 21, '2024-01-20 11:00:00'), (1, 22, '2024-01-21 08:00:00'),
-- 课程2：导数与微积分（6人）
(2, 13, '2024-02-06 09:00:00'), (2, 17, '2024-02-06 10:00:00'), (2, 19, '2024-02-07 08:00:00'),
(2, 22, '2024-02-07 09:00:00'), (2, 26, '2024-02-08 10:00:00'), (2, 30, '2024-02-08 11:00:00'),
-- 课程3：英语语法（10人）
(3, 11, '2024-01-22 08:00:00'), (3, 12, '2024-01-22 09:00:00'), (3, 14, '2024-01-23 10:00:00'),
(3, 15, '2024-01-23 11:00:00'), (3, 16, '2024-01-24 08:00:00'), (3, 18, '2024-01-24 09:00:00'),
(3, 20, '2024-01-25 10:00:00'), (3, 23, '2024-01-25 11:00:00'), (3, 25, '2024-01-26 08:00:00'),
(3, 28, '2024-01-26 09:00:00'),
-- 课程4：英语阅读（4人，已归档课程）
(4, 12, '2024-03-02 08:00:00'), (4, 20, '2024-03-02 09:00:00'), (4, 25, '2024-03-03 10:00:00'),
(4, 28, '2024-03-03 11:00:00'),
-- 课程5：力学基础（10人）
(5, 11, '2024-01-18 08:00:00'), (5, 13, '2024-01-18 09:00:00'), (5, 16, '2024-01-19 10:00:00'),
(5, 19, '2024-01-19 11:00:00'), (5, 21, '2024-01-20 08:00:00'), (5, 23, '2024-01-20 09:00:00'),
(5, 26, '2024-01-21 10:00:00'), (5, 29, '2024-01-21 11:00:00'), (5, 32, '2024-01-22 08:00:00'),
(5, 35, '2024-01-22 09:00:00'),
-- 课程6：电磁学（5人）
(6, 13, '2024-02-21 08:00:00'), (6, 17, '2024-02-21 09:00:00'), (6, 22, '2024-02-22 10:00:00'),
(6, 29, '2024-02-22 11:00:00'), (6, 37, '2024-02-23 08:00:00'),
-- 课程7：元素与化合物（8人）
(7, 14, '2024-03-06 08:00:00'), (7, 19, '2024-03-06 09:00:00'), (7, 21, '2024-03-07 10:00:00'),
(7, 24, '2024-03-07 11:00:00'), (7, 27, '2024-03-08 08:00:00'), (7, 31, '2024-03-08 09:00:00'),
(7, 33, '2024-03-09 10:00:00'), (7, 36, '2024-03-09 11:00:00'),
-- 课程8：有机化学（3人，待审核）
(8, 14, '2024-02-18 08:00:00'), (8, 31, '2024-02-18 09:00:00'), (8, 38, '2024-02-19 10:00:00'),
-- 课程9：文言文专题（6人）
(9, 15, '2024-02-03 08:00:00'), (9, 18, '2024-02-03 09:00:00'), (9, 22, '2024-02-04 10:00:00'),
(9, 29, '2024-02-04 11:00:00'), (9, 35, '2024-02-05 08:00:00'), (9, 40, '2024-02-05 09:00:00'),
-- 课程10：作文写作（4人）
(10, 15, '2024-03-16 08:00:00'), (10, 29, '2024-03-16 09:00:00'), (10, 38, '2024-03-17 10:00:00'),
(10, 45, '2024-03-17 11:00:00'),
-- 课程11：遗传与进化（5人）
(11, 17, '2024-02-12 08:00:00'), (11, 23, '2024-02-12 09:00:00'), (11, 30, '2024-02-13 10:00:00'),
(11, 33, '2024-02-13 11:00:00'), (11, 47, '2024-02-14 08:00:00'),
-- 课程12：中国古代史（4人）
(12, 25, '2024-02-18 08:00:00'), (12, 35, '2024-02-18 09:00:00'), (12, 40, '2024-02-19 10:00:00'),
(12, 49, '2024-02-19 11:00:00'),
-- 课程13：自然地理（3人）
(13, 27, '2024-03-02 08:00:00'), (13, 39, '2024-03-02 09:00:00'), (13, 42, '2024-03-03 10:00:00'),
-- 课程14：哲学基础（2人，被驳回）
(14, 41, '2024-03-05 08:00:00'), (14, 46, '2024-03-05 09:00:00'),
-- 课程15：Python编程（8人）
(15, 26, '2024-03-22 08:00:00'), (15, 32, '2024-03-22 09:00:00'), (15, 37, '2024-03-23 10:00:00'),
(15, 43, '2024-03-23 11:00:00'), (15, 44, '2024-03-24 08:00:00'), (15, 46, '2024-03-24 09:00:00'),
(15, 48, '2024-03-25 10:00:00'), (15, 50, '2024-03-25 11:00:00');


-- =============================================
-- 插入课堂数据（每门课5-8节课，覆盖进行中/未开始/已结束）
-- =============================================
INSERT INTO `class` (`id`, `course_id`, `user_id`, `class_title`, `file_url`, `end_time`, `status`, `create_time`) VALUES
-- 课程1：函数专题（6节，均已结束）
(1, 1, 1, '第一讲·变量与函数概念', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/math_func_1.pdf', '2024-01-15 12:00:00', 'ended', '2024-01-10 18:00:00'),
(2, 1, 1, '第二讲·正比例函数与一次函数', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/math_func_2.pdf', '2024-01-22 12:00:00', 'ended', '2024-01-17 18:00:00'),
(3, 1, 1, '第三讲·反比例函数', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/math_func_3.pdf', '2024-01-29 12:00:00', 'ended', '2024-01-24 18:00:00'),
(4, 1, 1, '第四讲·二次函数基础', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/math_func_4.pdf', '2024-02-05 12:00:00', 'ended', '2024-01-31 18:00:00'),
(5, 1, 1, '第五讲·二次函数综合', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/math_func_5.pdf', '2024-02-19 12:00:00', 'ended', '2024-02-14 18:00:00'),
(6, 1, 1, '第六讲·函数综合复习', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/math_func_6.pdf', '2024-02-26 12:00:00', 'ended', '2024-02-21 18:00:00'),
-- 课程2：导数与微积分（3节，1进行中）
(7, 2, 1, '第一讲·导数概念与几何意义', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/math_adv_1.pdf', '2024-06-30 14:00:00', 'active', '2024-06-25 18:00:00'),
(8, 2, 1, '第二讲·求导法则', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/math_adv_2.pdf', '2024-07-07 14:00:00', 'not_started', '2024-07-01 18:00:00'),
(9, 2, 1, '第三讲·微积分基本定理', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/math_adv_3.pdf', '2024-07-14 14:00:00', 'not_started', '2024-07-08 18:00:00'),
-- 课程3：英语语法（5节，均已结束）
(10, 3, 2, '第一讲·时态系统梳理', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/eng_grammar_1.pdf', '2024-01-25 11:00:00', 'ended', '2024-01-20 18:00:00'),
(11, 3, 2, '第二讲·被动语态全解', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/eng_grammar_2.pdf', '2024-02-01 11:00:00', 'ended', '2024-01-27 18:00:00'),
(12, 3, 2, '第三讲·三大从句（上）', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/eng_grammar_3.pdf', '2024-02-08 11:00:00', 'ended', '2024-02-03 18:00:00'),
(13, 3, 2, '第四讲·三大从句（下）', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/eng_grammar_4.pdf', '2024-02-15 11:00:00', 'ended', '2024-02-10 18:00:00'),
(14, 3, 2, '第五讲·非谓语动词', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/eng_grammar_5.pdf', '2024-02-22 11:00:00', 'ended', '2024-02-17 18:00:00'),
-- 课程5：力学基础（5节，均已结束）
(15, 5, 3, '第一讲·力的基本概念', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/phy_mech_1.pdf', '2024-01-20 16:00:00', 'ended', '2024-01-15 18:00:00'),
(16, 5, 3, '第二讲·牛顿第一、第二定律', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/phy_mech_2.pdf', '2024-01-27 16:00:00', 'ended', '2024-01-22 18:00:00'),
(17, 5, 3, '第三讲·牛顿第三定律与受力分析', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/phy_mech_3.pdf', '2024-02-03 16:00:00', 'ended', '2024-01-29 18:00:00'),
(18, 5, 3, '第四讲·浮力与压强', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/phy_mech_4.pdf', '2024-02-17 16:00:00', 'ended', '2024-02-12 18:00:00'),
(19, 5, 3, '第五讲·功与机械能', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/phy_mech_5.pdf', '2024-02-24 16:00:00', 'ended', '2024-02-19 18:00:00'),
-- 课程7：元素与化合物（3节，1进行中）
(20, 7, 4, '第一讲·常见元素及其性质', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/chem_elem_1.pdf', '2024-03-08 14:00:00', 'ended', '2024-03-05 18:00:00'),
(21, 7, 4, '第二讲·化学方程式与计算', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/chem_elem_2.pdf', '2024-03-15 14:00:00', 'ended', '2024-03-10 18:00:00'),
(22, 7, 4, '第三讲·酸碱盐专题', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/chem_elem_3.pdf', '2024-07-20 14:00:00', 'not_started', '2024-07-15 18:00:00'),
-- 课程9：文言文专题（4节，已结束）
(23, 9, 5, '第一讲·实词与虚词', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/chn_wenyan_1.pdf', '2024-02-10 10:00:00', 'ended', '2024-02-05 18:00:00'),
(24, 9, 5, '第二讲·文言句式', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/chn_wenyan_2.pdf', '2024-02-17 10:00:00', 'ended', '2024-02-12 18:00:00'),
(25, 9, 5, '第三讲·名篇精读', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/chn_wenyan_3.pdf', '2024-02-24 10:00:00', 'ended', '2024-02-19 18:00:00'),
(26, 9, 5, '第四讲·翻译技巧', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/chn_wenyan_4.pdf', '2024-03-02 10:00:00', 'ended', '2024-02-26 18:00:00'),
-- 课程11：遗传与进化（4节，2节进行中）
(27, 11, 6, '第一讲·基因与DNA', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/bio_gen_1.pdf', '2024-06-28 15:00:00', 'active', '2024-06-20 18:00:00'),
(28, 11, 6, '第二讲·遗传定律', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/bio_gen_2.pdf', '2024-07-05 15:00:00', 'not_started', '2024-06-30 18:00:00'),
(29, 11, 6, '第三讲·进化论', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/bio_gen_3.pdf', '2024-07-12 15:00:00', 'not_started', '2024-07-08 18:00:00'),
(30, 11, 6, '第四讲·自然选择', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/bio_gen_4.pdf', '2024-07-19 15:00:00', 'not_started', '2024-07-14 18:00:00'),
-- 课程12：中国古代史（3节，1节进行中）
(31, 12, 7, '第一讲·夏商周', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/his_ancient_1.pdf', '2024-06-30 10:00:00', 'active', '2024-06-25 18:00:00'),
(32, 12, 7, '第二讲·秦汉', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/his_ancient_2.pdf', '2024-07-07 10:00:00', 'not_started', '2024-07-01 18:00:00'),
(33, 12, 7, '第三讲·三国两晋南北朝', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/his_ancient_3.pdf', '2024-07-14 10:00:00', 'not_started', '2024-07-08 18:00:00'),
-- 课程15：Python编程（3节，全部未开始）
(34, 15, 10, '第一讲·变量与数据类型', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/cs_python_1.pdf', '2024-07-10 19:00:00', 'not_started', '2024-07-03 18:00:00'),
(35, 15, 10, '第二讲·条件与循环', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/cs_python_2.pdf', '2024-07-17 19:00:00', 'not_started', '2024-07-10 18:00:00'),
(36, 15, 10, '第三讲·函数与面向对象', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/class/cs_python_3.pdf', '2024-07-24 19:00:00', 'not_started', '2024-07-17 18:00:00');


-- =============================================
-- 插入课程资源数据
-- =============================================
INSERT INTO `course_resource` (`course_id`, `user_id`, `file_name`, `file_url`, `file_size`, `description`, `download_count`, `created_at`) VALUES
                                                                                                                                                (1, 1, '函数专题-课件全集.pdf', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/resource/math_func_all.pdf', 20485760, '函数专题全套课件，含所有章节', 230, '2024-01-10 08:00:00'),
                                                                                                                                                (1, 1, '函数习题集（含答案）.pdf', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/resource/math_func_exercises.pdf', 5242880, '精选100道函数练习题', 180, '2024-01-20 08:00:00'),
                                                                                                                                                (3, 2, '英语语法-思维导图.pdf', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/resource/eng_grammar_map.pdf', 3072000, '全语法体系思维导图', 320, '2024-01-22 08:00:00'),
                                                                                                                                                (5, 3, '力学基础-实验视频合集', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/resource/phy_mech_videos.zip', 524288000, '含10个力学实验视频', 89, '2024-01-18 08:00:00'),
                                                                                                                                                (7, 4, '元素周期表-记忆口诀.pdf', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/resource/chem_periodic_mnemonic.pdf', 1048576, '快速记忆元素周期表', 410, '2024-03-06 08:00:00'),
                                                                                                                                                (9, 5, '文言文-必备实词虚词表.pdf', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/resource/chn_wenyan_words.pdf', 2621440, '120个常见实词+20个虚词', 260, '2024-02-05 08:00:00'),
                                                                                                                                                (11, 6, '遗传学-图解宝典.pdf', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/resource/bio_genetics_guide.pdf', 15728640, '遗传规律图解全解', 95, '2024-02-12 08:00:00'),
                                                                                                                                                (12, 7, '中国古代史-时间轴.pdf', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/resource/his_timeline.pdf', 2097152, '五千年历史时间轴', 150, '2024-02-18 08:00:00'),
                                                                                                                                                (13, 8, '自然地理-气候分类表.pdf', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/resource/geo_climate.pdf', 1572864, '全球气候类型全表', 80, '2024-03-02 08:00:00'),
                                                                                                                                                (15, 10, 'Python-练习代码包.zip', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/resource/cs_python_codes.zip', 10240000, '课程所有示例代码', 340, '2024-03-22 08:00:00'),
                                                                                                                                                (15, 10, 'Python-项目实战指南.pdf', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/resource/cs_python_project.pdf', 5242880, '3个实战项目完整指导', 210, '2024-03-28 08:00:00');


-- =============================================
-- 插入作业数据（每门课2-3个作业）
-- =============================================
INSERT INTO `assignment` (`id`, `course_id`, `assignment_title`, `deadline`, `max_score`, `student_status`, `assignment_create_time`) VALUES
                                                                                                                                          (1, 1, '一次函数基础练习', '2024-01-20 23:59:59', 100, 'GRADED', '2024-01-12 08:00:00'),
                                                                                                                                          (2, 1, '二次函数综合测试', '2024-02-10 23:59:59', 120, 'GRADED', '2024-02-01 08:00:00'),
                                                                                                                                          (3, 1, '函数专题期末考试', '2024-03-01 23:59:59', 150, 'SUBMITTED', '2024-02-20 08:00:00'),
                                                                                                                                          (4, 3, '时态与语态专项练习', '2024-01-28 23:59:59', 100, 'GRADED', '2024-01-20 08:00:00'),
                                                                                                                                          (5, 3, '从句综合测试', '2024-02-18 23:59:59', 100, 'SUBMITTED', '2024-02-10 08:00:00'),
                                                                                                                                          (6, 5, '力学基础单元测试', '2024-01-30 23:59:59', 100, 'GRADED', '2024-01-20 08:00:00'),
                                                                                                                                          (7, 5, '牛顿定律专项练习', '2024-02-20 23:59:59', 80, 'OVERDUE', '2024-02-10 08:00:00'),
                                                                                                                                          (8, 7, '元素与化合物-第一单元练习', '2024-03-20 23:59:59', 100, 'SUBMITTED', '2024-03-10 08:00:00'),
                                                                                                                                          (9, 9, '文言文-实词虚词练习', '2024-02-15 23:59:59', 100, 'GRADED', '2024-02-05 08:00:00'),
                                                                                                                                          (10, 9, '文言文-翻译专项', '2024-03-05 23:59:59', 80, 'OVERDUE', '2024-02-25 08:00:00'),
                                                                                                                                          (11, 11, '遗传学基础练习', '2024-06-30 23:59:59', 100, 'PENDING', '2024-06-20 08:00:00'),
                                                                                                                                          (12, 12, '中国古代史-夏商周练习', '2024-07-05 23:59:59', 100, 'PENDING', '2024-06-25 08:00:00'),
                                                                                                                                          (13, 15, 'Python-变量与数据类型练习', '2024-07-15 23:59:59', 100, 'PENDING', '2024-07-03 08:00:00');


-- =============================================
-- 插入题目数据
-- =============================================
INSERT INTO `question` (`assignment_id`, `type`, `stem`, `answer`, `explanation`, `perscore`, `sort_order`, `options`, `image_url`) VALUES
-- 作业1：一次函数基础练习
(1, 1, '一次函数 y=2x+3 的图像在 y 轴上的截距是？', 'B', '在y轴截距即x=0时y的值，y=3', 10, 1, '["A. 2", "B. 3", "C. -3", "D. -2"]', NULL),
(1, 1, '下列哪个是正比例函数？', 'B', '正比例函数形如y=kx，必须过原点', 10, 2, '["A. y=2x+1", "B. y=3x", "C. y=x²", "D. y=4"]', NULL),
(1, 5, '请写出一次函数 y=2x-1 与 x 轴、y 轴的交点坐标。', 'x轴(0.5,0)，y轴(0,-1)', '令y=0得x=0.5，令x=0得y=-1', 20, 3, NULL, NULL),
-- 作业2：二次函数综合测试
(2, 1, '二次函数 y=x² 的对称轴是？', 'B', 'y=x²顶点在原点，对称轴为x=0', 10, 1, '["A. x=1", "B. x=0", "C. y=0", "D. x=-1"]', NULL),
(2, 1, 'y=(x-1)²+2 的顶点坐标是？', 'B', '二次函数顶点式y=a(x-h)²+k，顶点为(h,k)', 10, 2, '["A. (0,0)", "B. (1,2)", "C. (-1,2)", "D. (1,-2)"]', NULL),
(2, 5, '求二次函数 y=x²-4x+3 的顶点坐标和与 x 轴交点。', '顶点(2,-1)，x轴交点(1,0)和(3,0)', '配方法得顶点，令y=0求交点', 20, 3, NULL, NULL),
-- 作业4：时态与语态
(4, 1, 'She ___ to school every day.', 'B', 'every day表示经常性动作，用一般现在时，主语三单加s', 10, 1, '["A. go", "B. goes", "C. going", "D. went"]', NULL),
(4, 1, 'I ___ a letter now.', 'C', 'now表示正在进行的动作，用现在进行时', 10, 2, '["A. write", "B. writes", "C. am writing", "D. wrote"]', NULL),
(4, 5, '用现在完成时翻译：我已经在北京住了5年。', 'I have lived in Beijing for 5 years.', '现在完成时表示过去开始持续到现在的动作', 20, 3, NULL, NULL),
-- 作业6：力学基础
(6, 1, '物体受合力为零时，下列说法正确的是？', 'C', '牛顿第一定律：物体保持匀速直线运动或静止状态', 10, 1, '["A. 一定静止", "B. 一定匀速运动", "C. 保持匀速直线运动或静止", "D. 加速运动"]', NULL),
(6, 1, '质量 m=2kg 的物体受 F=10N 的力，加速度为？', 'B', '牛顿第二定律 a=F/m=10/2=5m/s²', 10, 2, '["A. 2m/s²", "B. 5m/s²", "C. 8m/s²", "D. 20m/s²"]', NULL),
(6, 5, '用牛顿第二定律推导物体在光滑斜面（倾角 θ）上的加速度。', 'a=g·sinθ', '沿斜面方向受力为mg·sinθ，a=F/m=g·sinθ', 20, 3, NULL, NULL),
-- 作业9：文言文
(9, 1, '"吾日三省吾身"中的"日"意思是？', 'C', '日在此处为名词作状语，每天的意思', 10, 1, '["A. 太阳", "B. 日子", "C. 每天", "D. 白天"]', NULL),
(9, 5, '翻译："学而时习之，不亦说乎？"', '学习并且经常复习它，不也是很愉快吗？', '时：时常；习：复习；说：通"悦"，愉快', 20, 2, NULL, NULL);


-- =============================================
-- 插入客观题提交数据
-- =============================================
INSERT INTO `object_submit` (`assignment_id`, `question_id`, `user_id`, `object_score`, `answer_word`, `submit_time`) VALUES
-- 作业1：一次函数（学生提交）
(1, 1, 11, 10, 'B', '2024-01-18 10:00:00'),
(1, 1, 12, 10, 'B', '2024-01-18 11:00:00'),
(1, 1, 13, 10, 'B', '2024-01-19 09:00:00'),
(1, 1, 14, 0, 'A', '2024-01-19 10:00:00'),
(1, 1, 15, 10, 'B', '2024-01-19 14:00:00'),
(1, 1, 16, 10, 'B', '2024-01-20 08:00:00'),
(1, 1, 17, 10, 'B', '2024-01-20 09:00:00'),
(1, 1, 18, 0, 'C', '2024-01-20 10:00:00'),
(1, 1, 19, 10, 'B', '2024-01-18 20:00:00'),
(1, 1, 20, 10, 'B', '2024-01-19 16:00:00'),
(1, 2, 11, 10, 'B', '2024-01-18 10:00:00'),
(1, 2, 12, 10, 'B', '2024-01-18 11:00:00'),
(1, 2, 13, 0, 'A', '2024-01-19 09:00:00'),
(1, 2, 14, 10, 'B', '2024-01-19 10:00:00'),
(1, 2, 15, 10, 'B', '2024-01-19 14:00:00'),
(1, 2, 16, 10, 'B', '2024-01-20 08:00:00'),
(1, 2, 17, 10, 'B', '2024-01-20 09:00:00'),
(1, 2, 18, 0, 'C', '2024-01-20 10:00:00'),
(1, 2, 19, 10, 'B', '2024-01-18 20:00:00'),
(1, 2, 20, 10, 'B', '2024-01-19 16:00:00'),
-- 作业4：时态与语态
(4, 7, 11, 10, 'B', '2024-01-25 10:00:00'),
(4, 7, 12, 10, 'B', '2024-01-25 11:00:00'),
(4, 7, 14, 0, 'A', '2024-01-26 09:00:00'),
(4, 7, 15, 10, 'B', '2024-01-26 10:00:00'),
(4, 7, 16, 10, 'B', '2024-01-26 14:00:00'),
(4, 8, 11, 10, 'C', '2024-01-25 10:00:00'),
(4, 8, 12, 10, 'C', '2024-01-25 11:00:00'),
(4, 8, 14, 10, 'C', '2024-01-26 09:00:00'),
(4, 8, 15, 0, 'A', '2024-01-26 10:00:00'),
(4, 8, 16, 10, 'C', '2024-01-26 14:00:00'),
-- 作业6：力学基础
(6, 10, 11, 10, 'C', '2024-01-28 09:00:00'),
(6, 10, 13, 10, 'C', '2024-01-28 10:00:00'),
(6, 10, 16, 10, 'C', '2024-01-29 08:00:00'),
(6, 10, 19, 0, 'A', '2024-01-29 09:00:00'),
(6, 10, 21, 10, 'C', '2024-01-29 14:00:00'),
(6, 11, 11, 10, 'B', '2024-01-28 09:00:00'),
(6, 11, 13, 10, 'B', '2024-01-28 10:00:00'),
(6, 11, 16, 10, 'B', '2024-01-29 08:00:00'),
(6, 11, 19, 10, 'B', '2024-01-29 09:00:00'),
(6, 11, 21, 0, 'A', '2024-01-29 14:00:00');


-- =============================================
-- 插入主观题提交数据（含批改）
-- =============================================
INSERT INTO `subject_submit` (`assignment_id`, `user_id`, `question_id`, `answer_picture`, `subject_score`, `teacher_comment`, `finish_status`, `grading_status`, `finish_time`, `grading_time`) VALUES
-- 作业1：一次函数（已批改）
(1, 11, 3, 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/submit/math_answer_11.jpg', 20, '思路清晰，步骤完整，满分！', 2, 2, '2024-01-18 10:00:00', '2024-01-19 20:00:00'),
(1, 12, 3, 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/submit/math_answer_12.jpg', 18, '坐标计算正确，但缺少推导过程', 2, 2, '2024-01-18 11:00:00', '2024-01-19 20:30:00'),
(1, 13, 3, 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/submit/math_answer_13.jpg', 16, 'y轴坐标算错，注意符号', 2, 2, '2024-01-19 09:00:00', '2024-01-20 10:00:00'),
(1, 14, 3, 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/submit/math_answer_14.jpg', 0, '未作答', 2, 2, '2024-01-19 10:00:00', '2024-01-20 10:30:00'),
(1, 15, 3, 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/submit/math_answer_15.jpg', 20, '完美解答', 2, 2, '2024-01-19 14:00:00', '2024-01-20 11:00:00'),
(1, 16, 3, 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/submit/math_answer_16.jpg', 20, '全部正确，很好', 2, 2, '2024-01-20 08:00:00', '2024-01-20 11:30:00'),
(1, 17, 3, 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/submit/math_answer_17.jpg', 15, '步骤跳跃，请写详细过程', 2, 2, '2024-01-20 09:00:00', '2024-01-20 12:00:00'),
(1, 18, 3, NULL, 0, '未提交', 1, 1, NULL, NULL),
(1, 19, 3, 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/submit/math_answer_19.jpg', 20, '满分！继续保持', 2, 2, '2024-01-18 20:00:00', '2024-01-19 21:00:00'),
-- 作业4：时态与语态（已批改）
(4, 11, 9, 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/submit/eng_answer_11.jpg', 19, '句子结构正确，注意for的用法', 2, 2, '2024-01-25 10:00:00', '2024-01-26 20:00:00'),
(4, 12, 9, 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/submit/eng_answer_12.jpg', 20, '完美！', 2, 2, '2024-01-25 11:00:00', '2024-01-26 20:30:00'),
(4, 14, 9, 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/submit/eng_answer_14.jpg', 15, '时态用错了，应用have lived', 2, 2, '2024-01-26 09:00:00', '2024-01-27 09:00:00'),
(4, 15, 9, 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/submit/eng_answer_15.jpg', 18, '很好，in可以用for代替', 2, 2, '2024-01-26 10:00:00', '2024-01-27 09:30:00'),
(4, 16, 9, 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/submit/eng_answer_16.jpg', 20, '非常标准！', 2, 2, '2024-01-26 14:00:00', '2024-01-27 10:00:00'),
-- 作业6：力学基础（部分已批改）
(6, 11, 12, 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/submit/phy_answer_11.jpg', 20, '推导过程完整，思路清晰', 2, 2, '2024-01-28 09:00:00', '2024-01-29 20:00:00'),
(6, 13, 12, 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/submit/phy_answer_13.jpg', 18, '公式正确，文字说明略少', 2, 2, '2024-01-28 10:00:00', '2024-01-29 20:30:00'),
(6, 16, 12, 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/submit/phy_answer_16.jpg', 15, '推导正确但中间步骤缺失', 2, 2, '2024-01-29 08:00:00', '2024-01-30 09:00:00'),
(6, 19, 12, NULL, NULL, NULL, 1, 1, NULL, NULL),
(6, 21, 12, 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/submit/phy_answer_21.jpg', 20, '非常优秀！', 2, 2, '2024-01-29 14:00:00', '2024-01-30 09:30:00'),
-- 作业11：遗传学（待批改）
(11, 17, NULL, 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/submit/bio_answer_17.jpg', NULL, NULL, 2, 1, '2024-06-25 20:00:00', NULL),
(11, 23, NULL, 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/submit/bio_answer_23.jpg', NULL, NULL, 2, 1, '2024-06-26 19:00:00', NULL),
(11, 30, NULL, NULL, NULL, NULL, 1, 1, NULL, NULL),
(11, 33, NULL, 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/submit/bio_answer_33.jpg', NULL, NULL, 2, 1, '2024-06-27 20:30:00', NULL),
(11, 47, NULL, NULL, NULL, NULL, 1, 1, NULL, NULL),
-- 作业13：Python（待批改）
(13, 26, NULL, 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/submit/py_answer_26.jpg', NULL, NULL, 2, 1, '2024-07-08 21:00:00', NULL),
(13, 32, NULL, 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/submit/py_answer_32.jpg', NULL, NULL, 2, 1, '2024-07-09 20:00:00', NULL),
(13, 37, NULL, 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/submit/py_answer_37.jpg', NULL, NULL, 2, 1, '2024-07-09 22:30:00', NULL),
(13, 43, NULL, NULL, NULL, NULL, 1, 1, NULL, NULL),
(13, 44, NULL, 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/submit/py_answer_44.jpg', NULL, NULL, 2, 1, '2024-07-10 19:00:00', NULL),
(13, 46, NULL, NULL, NULL, NULL, 1, 1, NULL, NULL),
(13, 48, NULL, 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/submit/py_answer_48.jpg', NULL, NULL, 2, 1, '2024-07-10 20:15:00', NULL),
(13, 50, NULL, NULL, NULL, NULL, 1, 1, NULL, NULL);


-- =============================================
-- 插入签到数据
-- =============================================
INSERT INTO `class_check` (`class_id`, `user_id`, `check_status`, `checkin_time`) VALUES
-- 课堂1：函数第一讲
(1, 11, 1, '2024-01-10 19:05:00'), (1, 12, 1, '2024-01-10 19:08:00'), (1, 13, 1, '2024-01-10 19:02:00'),
(1, 14, 1, '2024-01-10 19:10:00'), (1, 15, 2, '2024-01-10 19:20:00'), (1, 16, 1, '2024-01-10 19:01:00'),
(1, 17, 1, '2024-01-10 19:06:00'), (1, 18, 3, NULL), (1, 19, 1, '2024-01-10 19:03:00'),
(1, 20, 1, '2024-01-10 19:07:00'), (1, 21, 2, '2024-01-10 19:15:00'), (1, 22, 1, '2024-01-10 19:00:30'),
-- 课堂3：函数第三讲
(3, 11, 1, '2024-01-24 19:03:00'), (3, 12, 1, '2024-01-24 19:06:00'), (3, 13, 1, '2024-01-24 19:00:00'),
(3, 14, 1, '2024-01-24 19:12:00'), (3, 15, 1, '2024-01-24 19:08:00'), (3, 16, 3, NULL),
(3, 17, 1, '2024-01-24 19:02:00'), (3, 18, 1, '2024-01-24 19:10:00'), (3, 19, 2, '2024-01-24 19:25:00'),
(3, 20, 1, '2024-01-24 19:01:00'),
-- 课堂10：英语语法第一讲
(10, 11, 1, '2024-01-20 19:02:00'), (10, 12, 1, '2024-01-20 19:05:00'), (10, 14, 1, '2024-01-20 19:08:00'),
(10, 15, 1, '2024-01-20 19:01:00'), (10, 16, 2, '2024-01-20 19:18:00'), (10, 18, 1, '2024-01-20 19:00:30'),
(10, 20, 1, '2024-01-20 19:06:00'), (10, 23, 3, NULL), (10, 25, 1, '2024-01-20 19:04:00'),
(10, 28, 1, '2024-01-20 19:09:00'),
-- 课堂15：物理第一讲
(15, 11, 1, '2024-01-15 19:03:00'), (15, 13, 1, '2024-01-15 19:00:00'), (15, 16, 1, '2024-01-15 19:07:00'),
(15, 19, 2, '2024-01-15 19:22:00'), (15, 21, 1, '2024-01-15 19:05:00'), (15, 23, 1, '2024-01-15 19:01:00'),
(15, 26, 1, '2024-01-15 19:08:00'), (15, 29, 3, NULL), (15, 32, 1, '2024-01-15 19:04:00'),
(15, 35, 1, '2024-01-15 19:02:00'),
-- 课堂20：化学第一讲
(20, 14, 1, '2024-03-06 19:02:00'), (20, 19, 1, '2024-03-06 19:05:00'), (20, 21, 1, '2024-03-06 19:00:30'),
(20, 24, 2, '2024-03-06 19:20:00'), (20, 27, 1, '2024-03-06 19:06:00'), (20, 31, 1, '2024-03-06 19:01:00'),
(20, 33, 1, '2024-03-06 19:08:00'), (20, 36, 3, NULL),
-- 课堂27：生物第一讲（进行中课程，已签到5人）
(27, 17, 1, '2024-06-25 20:02:00'), (27, 23, 1, '2024-06-25 20:05:00'), (27, 30, 1, '2024-06-25 19:58:00'),
(27, 33, 2, '2024-06-25 20:15:00'), (27, 47, 1, '2024-06-25 20:00:30'),
-- 课堂31：历史第一讲（进行中课程）
(31, 25, 1, '2024-06-28 10:03:00'), (31, 35, 1, '2024-06-28 10:00:00'), (31, 40, 2, '2024-06-28 10:12:00'),
(31, 49, 1, '2024-06-28 10:01:30');


-- =============================================
-- 插入课堂投票数据
-- =============================================
INSERT INTO `class_vote` (`class_id`, `heading`, `options`, `correct_option`, `duration`, `status`, `created_at`, `ended_at`) VALUES
                                                                                                                                  (1, '函数的定义域是指？', '["A. 自变量的取值范围", "B. 因变量的取值范围", "C. 函数的表达式", "D. 函数的图像"]', 'A', 120, 'ended', '2024-01-10 19:15:00', '2024-01-10 19:17:00'),
                                                                                                                                  (1, '下列哪个是一次函数？', '["A. y = 2x + 1", "B. y = x² + 1", "C. y = 3/x", "D. y = 5"]', 'A', 60, 'ended', '2024-01-10 19:25:00', '2024-01-10 19:26:00'),
                                                                                                                                  (3, '反比例函数的图像在哪个象限？', '["A. 一三象限", "B. 二四象限", "C. 一二象限", "D. 三四象限"]', 'A', 90, 'ended', '2024-01-24 19:15:00', '2024-01-24 19:16:30'),
                                                                                                                                  (10, '一般现在时的标志词是？', '["A. yesterday", "B. often/usually", "C. tomorrow", "D. now"]', 'B', 120, 'ended', '2024-01-20 19:10:00', '2024-01-20 19:12:00'),
                                                                                                                                  (15, '牛顿第一定律又称为？', '["A. 加速度定律", "B. 惯性定律", "C. 作用力反作用力定律", "D. 万有引力定律"]', 'B', 90, 'ended', '2024-01-15 19:10:00', '2024-01-15 19:11:30'),
                                                                                                                                  (15, '下列哪个是力的单位？', '["A. 千克", "B. 牛顿", "C. 帕斯卡", "D. 焦耳"]', 'B', 60, 'ended', '2024-01-15 19:20:00', '2024-01-15 19:21:00'),
                                                                                                                                  (20, '元素周期表中第一个元素是？', '["A. 氧", "B. 氢", "C. 碳", "D. 氮"]', 'B', 90, 'ended', '2024-03-06 19:10:00', '2024-03-06 19:11:30'),
                                                                                                                                  (27, 'DNA的双螺旋结构是哪两位科学家发现的？', '["A. 孟德尔与摩尔根", "B. 沃森与克里克", "C. 达尔文与拉马克", "D. 巴斯德与科赫"]', 'B', 120, 'ended', '2024-06-25 20:10:00', '2024-06-25 20:12:00'),
                                                                                                                                  (27, '基因的本质是？', '["A. 蛋白质", "B. DNA片段", "C. 染色体", "D. 糖类"]', 'B', 60, 'active', '2024-06-25 20:20:00', NULL),
                                                                                                                                  (31, '中国历史上第一个王朝是？', '["A. 商朝", "B. 夏朝", "C. 周朝", "D. 秦朝"]', 'B', 90, 'ended', '2024-06-28 10:05:00', '2024-06-28 10:06:30'),
                                                                                                                                  (7, '导数的几何意义是？', '["A. 曲线下面积", "B. 切线的斜率", "C. 函数的零点", "D. 曲线的长度"]', 'B', 120, 'active', '2024-06-25 20:00:00', NULL);


-- =============================================
-- 插入投票记录数据
-- =============================================
INSERT INTO `vote_record` (`vote_id`, `user_id`, `selected_option`, `is_correct`, `submitted_at`) VALUES
-- 投票1：函数定义域（已结束）
(1, 11, 'A', 1, '2024-01-10 19:15:30'), (1, 12, 'A', 1, '2024-01-10 19:15:40'), (1, 13, 'B', 0, '2024-01-10 19:15:50'),
(1, 14, 'A', 1, '2024-01-10 19:16:00'), (1, 15, 'A', 1, '2024-01-10 19:16:10'), (1, 16, 'A', 1, '2024-01-10 19:16:20'),
(1, 17, 'C', 0, '2024-01-10 19:16:30'), (1, 19, 'A', 1, '2024-01-10 19:16:40'), (1, 20, 'A', 1, '2024-01-10 19:16:50'),
(1, 21, 'A', 1, '2024-01-10 19:17:00'), (1, 22, 'A', 1, '2024-01-10 19:17:10'),
-- 投票2：一次函数（已结束）
(2, 11, 'A', 1, '2024-01-10 19:25:10'), (2, 12, 'A', 1, '2024-01-10 19:25:20'), (2, 13, 'C', 0, '2024-01-10 19:25:30'),
(2, 14, 'A', 1, '2024-01-10 19:25:40'), (2, 15, 'A', 1, '2024-01-10 19:25:50'), (2, 16, 'A', 1, '2024-01-10 19:26:00'),
(2, 17, 'A', 1, '2024-01-10 19:26:10'), (2, 19, 'A', 1, '2024-01-10 19:26:20'), (2, 20, 'A', 1, '2024-01-10 19:26:30'),
(2, 21, 'A', 1, '2024-01-10 19:26:40'), (2, 22, 'B', 0, '2024-01-10 19:26:50'),
-- 投票4：时态标志词（已结束）
(4, 11, 'B', 1, '2024-01-20 19:10:10'), (4, 12, 'B', 1, '2024-01-20 19:10:20'), (4, 14, 'A', 0, '2024-01-20 19:10:30'),
(4, 15, 'B', 1, '2024-01-20 19:10:40'), (4, 16, 'B', 1, '2024-01-20 19:10:50'), (4, 18, 'B', 1, '2024-01-20 19:11:00'),
(4, 20, 'B', 1, '2024-01-20 19:11:10'), (4, 25, 'B', 1, '2024-01-20 19:11:20'), (4, 28, 'B', 1, '2024-01-20 19:11:30'),
-- 投票5：牛顿第一定律（已结束）
(5, 11, 'B', 1, '2024-01-15 19:10:10'), (5, 13, 'B', 1, '2024-01-15 19:10:20'), (5, 16, 'B', 1, '2024-01-15 19:10:30'),
(5, 19, 'A', 0, '2024-01-15 19:10:40'), (5, 21, 'B', 1, '2024-01-15 19:10:50'), (5, 23, 'B', 1, '2024-01-15 19:11:00'),
(5, 26, 'B', 1, '2024-01-15 19:11:10'), (5, 32, 'B', 1, '2024-01-15 19:11:20'), (5, 35, 'B', 1, '2024-01-15 19:11:30'),
-- 投票6：力的单位（已结束）
(6, 11, 'B', 1, '2024-01-15 19:20:10'), (6, 13, 'B', 1, '2024-01-15 19:20:20'), (6, 16, 'B', 1, '2024-01-15 19:20:30'),
(6, 19, 'B', 1, '2024-01-15 19:20:40'), (6, 21, 'A', 0, '2024-01-15 19:20:50'), (6, 23, 'B', 1, '2024-01-15 19:21:00'),
(6, 26, 'B', 1, '2024-01-15 19:21:10'), (6, 32, 'B', 1, '2024-01-15 19:21:20'), (6, 35, 'B', 1, '2024-01-15 19:21:30'),
-- 投票7：元素周期表（已结束）
(7, 14, 'B', 1, '2024-03-06 19:10:10'), (7, 19, 'B', 1, '2024-03-06 19:10:20'), (7, 21, 'B', 1, '2024-03-06 19:10:30'),
(7, 24, 'A', 0, '2024-03-06 19:10:40'), (7, 27, 'B', 1, '2024-03-06 19:10:50'), (7, 31, 'B', 1, '2024-03-06 19:11:00'),
(7, 33, 'B', 1, '2024-03-06 19:11:10'),
-- 投票8：DNA发现者（已结束）
(8, 17, 'B', 1, '2024-06-25 20:10:10'), (8, 23, 'B', 1, '2024-06-25 20:10:20'), (8, 30, 'A', 0, '2024-06-25 20:10:30'),
(8, 33, 'B', 1, '2024-06-25 20:10:40'), (8, 47, 'B', 1, '2024-06-25 20:10:50'),
-- 投票10：第一个王朝（已结束）
(10, 25, 'B', 1, '2024-06-28 10:05:10'), (10, 35, 'B', 1, '2024-06-28 10:05:20'), (10, 40, 'A', 0, '2024-06-28 10:05:30'),
(10, 49, 'B', 1, '2024-06-28 10:05:40');


-- =============================================
-- 插入聊天数据
-- =============================================
INSERT INTO `class_chat` (`class_id`, `user_id`, `message_type`, `content`, `sent_time`) VALUES
                                                                                             (1, 11, 1, '老师好！终于开课了', '2024-01-10 19:00:30'),
                                                                                             (1, 12, 1, '张老师好', '2024-01-10 19:00:45'),
                                                                                             (1, 1, 1, '同学们好，欢迎来到函数专题课程', '2024-01-10 19:01:00'),
                                                                                             (1, 13, 1, '老师，定义域是什么意思？', '2024-01-10 19:05:20'),
                                                                                             (1, 1, 1, '定义域就是自变量x可以取的值的范围', '2024-01-10 19:06:00'),
                                                                                             (1, 14, 1, '明白啦', '2024-01-10 19:06:30'),
                                                                                             (1, 15, 2, 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/emoji/happy.png', '2024-01-10 19:07:00'),
                                                                                             (1, 16, 1, '这节课内容很清晰', '2024-01-10 19:30:00'),
                                                                                             (1, 17, 1, '老师，一次函数和正比例函数有什么区别？', '2024-01-10 19:35:00'),
                                                                                             (1, 1, 1, '正比例函数是一次函数的特殊情况，过原点', '2024-01-10 19:36:00'),
                                                                                             (10, 11, 1, '李老师好', '2024-01-20 19:00:10'),
                                                                                             (10, 12, 1, '终于等到了语法课', '2024-01-20 19:00:25'),
                                                                                             (10, 2, 1, '大家好，今天我们讲时态', '2024-01-20 19:00:40'),
                                                                                             (10, 14, 1, '李老师，一般现在时有几个用法？', '2024-01-20 19:10:00'),
                                                                                             (10, 2, 1, '主要有三种：客观事实、经常性动作、按规定发生的事', '2024-01-20 19:11:00'),
                                                                                             (10, 15, 1, '老师讲得太清楚了！', '2024-01-20 19:25:00'),
                                                                                             (10, 16, 3, 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/emoji/awesome.png', '2024-01-20 19:26:00'),
                                                                                             (15, 11, 1, '王老师好', '2024-01-15 19:00:15'),
                                                                                             (15, 13, 1, '物理课来了', '2024-01-15 19:00:30'),
                                                                                             (15, 3, 1, '同学们好，今天学习牛顿定律', '2024-01-15 19:00:45'),
                                                                                             (15, 16, 1, '惯性定律是不是就是物体保持原来状态？', '2024-01-15 19:08:00'),
                                                                                             (15, 3, 1, '对的，这叫惯性，物体有保持原来运动状态的属性', '2024-01-15 19:09:00'),
                                                                                             (15, 19, 1, '老师，加速度和力是什么关系？', '2024-01-15 19:15:00'),
                                                                                             (15, 3, 1, 'F=ma，力是产生加速度的原因', '2024-01-15 19:16:00'),
                                                                                             (27, 17, 1, '孙老师好', '2024-06-25 20:00:10'),
                                                                                             (27, 23, 1, '遗传学我来啦', '2024-06-25 20:00:20'),
                                                                                             (27, 6, 1, '欢迎同学们，今天讲基因', '2024-06-25 20:00:30'),
                                                                                             (27, 30, 1, '基因和DNA是什么关系？', '2024-06-25 20:05:00'),
                                                                                             (27, 6, 1, '基因是DNA上有遗传效应的片段', '2024-06-25 20:06:00'),
                                                                                             (27, 33, 1, '明白了！', '2024-06-25 20:06:30');


-- =============================================
-- 插入自习室记录（40位学生都有记录，覆盖各种情况）
-- =============================================
INSERT INTO `study_room` (`user_id`, `goal`, `mode`, `focus_mode`, `plan_time`, `start_time`, `end_time`, `total_time`, `created_at`) VALUES
-- 高效学生（学习时长长，注意力集中）
(11, '完成函数专题复习', 1, 1, NULL, '2024-06-01 19:00:00', '2024-06-01 20:30:00', 5400, '2024-06-01 18:55:00'),
(11, '预习导数章节', 1, 0, NULL, '2024-06-03 18:00:00', '2024-06-03 19:30:00', 5400, '2024-06-03 17:55:00'),
(11, '英语语法练习', 2, 1, 7200, '2024-06-05 20:00:00', '2024-06-05 21:30:00', 5400, '2024-06-05 19:55:00'),
(12, '英语单词背诵', 1, 1, NULL, '2024-06-01 20:00:00', '2024-06-01 20:45:00', 2700, '2024-06-01 19:55:00'),
(12, '语法练习', 2, 0, 3600, '2024-06-02 16:00:00', '2024-06-02 17:00:00', 3600, '2024-06-02 15:55:00'),
(13, '物理刷题', 1, 1, NULL, '2024-06-01 19:00:00', '2024-06-01 21:00:00', 7200, '2024-06-01 18:55:00'),
(13, '数学练习', 1, 0, NULL, '2024-06-03 15:00:00', '2024-06-03 16:30:00', 5400, '2024-06-03 14:55:00'),
(14, '化学作业', 1, 0, NULL, '2024-06-02 14:00:00', '2024-06-02 15:00:00', 3600, '2024-06-02 13:55:00'),
(15, '作文素材积累', 1, 1, NULL, '2024-06-01 09:00:00', '2024-06-01 10:30:00', 5400, '2024-06-01 08:55:00'),
(15, '文言文背诵', 2, 1, 5400, '2024-06-04 08:00:00', '2024-06-04 09:00:00', 3600, '2024-06-04 07:55:00'),
(16, '完成作业', 1, 0, NULL, '2024-06-03 19:00:00', '2024-06-03 20:00:00', 3600, '2024-06-03 18:55:00'),
(17, '数学竞赛准备', 1, 1, NULL, '2024-06-01 08:00:00', '2024-06-01 10:00:00', 7200, '2024-06-01 07:55:00'),
(17, '物理竞赛准备', 1, 1, NULL, '2024-06-02 08:00:00', '2024-06-02 10:30:00', 9000, '2024-06-02 07:55:00'),
(18, '复习函数', 1, 0, NULL, '2024-06-01 19:30:00', '2024-06-01 20:00:00', 1800, '2024-06-01 19:25:00'),
(19, '完成化学作业', 1, 1, NULL, '2024-06-02 20:00:00', '2024-06-02 21:30:00', 5400, '2024-06-02 19:55:00'),
(20, '英语阅读理解', 1, 1, NULL, '2024-06-01 15:00:00', '2024-06-01 16:30:00', 5400, '2024-06-01 14:55:00'),
(20, '背单词', 2, 0, 3600, '2024-06-03 07:00:00', '2024-06-03 07:30:00', 1800, '2024-06-03 06:55:00'),
(21, '物理作业', 1, 0, NULL, '2024-06-02 19:00:00', '2024-06-02 20:30:00', 5400, '2024-06-02 18:55:00'),
(22, '数学压轴题训练', 1, 1, NULL, '2024-06-01 14:00:00', '2024-06-01 16:00:00', 7200, '2024-06-01 13:55:00'),
(23, '生物预习', 1, 0, NULL, '2024-06-03 20:00:00', '2024-06-03 21:00:00', 3600, '2024-06-03 19:55:00'),
(24, '完成作业', 1, 0, NULL, '2024-06-01 18:00:00', '2024-06-01 19:00:00', 3600, '2024-06-01 17:55:00'),
(25, '历史复习', 1, 1, NULL, '2024-06-02 09:00:00', '2024-06-02 10:30:00', 5400, '2024-06-02 08:55:00'),
(26, 'Python练习', 2, 0, 7200, '2024-06-01 20:00:00', '2024-06-01 20:45:00', 2700, '2024-06-01 19:55:00'),
(27, '地理复习', 1, 0, NULL, '2024-06-02 15:00:00', '2024-06-02 16:30:00', 5400, '2024-06-02 14:55:00'),
(28, '完成英语作业', 1, 1, NULL, '2024-06-01 19:00:00', '2024-06-01 20:00:00', 3600, '2024-06-01 18:55:00'),
(29, '作文训练', 2, 1, 5400, '2024-06-03 14:00:00', '2024-06-03 15:30:00', 5400, '2024-06-03 13:55:00'),
(30, '数学复习', 1, 0, NULL, '2024-06-01 10:00:00', '2024-06-01 11:00:00', 3600, '2024-06-01 09:55:00'),
(31, '化学方程式练习', 1, 1, NULL, '2024-06-02 19:00:00', '2024-06-02 20:30:00', 5400, '2024-06-02 18:55:00'),
(32, '力学复习', 1, 0, NULL, '2024-06-01 16:00:00', '2024-06-01 17:00:00', 3600, '2024-06-01 15:55:00'),
(33, '生物遗传题', 1, 1, NULL, '2024-06-03 09:00:00', '2024-06-03 10:30:00', 5400, '2024-06-03 08:55:00'),
(34, '完成作业', 1, 0, NULL, '2024-06-01 20:00:00', '2024-06-01 20:30:00', 1800, '2024-06-01 19:55:00'),
(35, '历史笔记整理', 1, 1, NULL, '2024-06-02 10:00:00', '2024-06-02 11:30:00', 5400, '2024-06-02 09:55:00'),
(36, '化学预习', 1, 0, NULL, '2024-06-01 14:00:00', '2024-06-01 14:30:00', 1800, '2024-06-01 13:55:00'),
(37, 'Python项目', 1, 1, NULL, '2024-06-01 19:00:00', '2024-06-01 21:00:00', 7200, '2024-06-01 18:55:00'),
(38, '函数复习', 1, 0, NULL, '2024-06-02 08:00:00', '2024-06-02 08:45:00', 2700, '2024-06-02 07:55:00'),
(39, '地理知识点记忆', 1, 1, NULL, '2024-06-03 18:00:00', '2024-06-03 19:00:00', 3600, '2024-06-03 17:55:00'),
(40, '文言文翻译', 2, 0, 3600, '2024-06-01 09:30:00', '2024-06-01 10:30:00', 3600, '2024-06-01 09:25:00'),
(41, '政治复习', 1, 1, NULL, '2024-06-02 14:00:00', '2024-06-02 15:30:00', 5400, '2024-06-02 13:55:00'),
(42, '完成作业', 1, 0, NULL, '2024-06-01 19:00:00', '2024-06-01 19:30:00', 1800, '2024-06-01 18:55:00'),
(43, '物理刷题', 1, 1, NULL, '2024-06-03 15:00:00', '2024-06-03 16:30:00', 5400, '2024-06-03 14:55:00'),
(44, 'Python基础练习', 2, 0, 5400, '2024-06-01 20:00:00', '2024-06-01 21:00:00', 3600, '2024-06-01 19:55:00'),
(45, '作文素材积累', 1, 1, NULL, '2024-06-02 08:00:00', '2024-06-02 09:30:00', 5400, '2024-06-02 07:55:00'),
(46, '完成作业', 1, 0, NULL, '2024-06-01 18:00:00', '2024-06-01 18:20:00', 1200, '2024-06-01 17:55:00'),
(47, '生物竞赛准备', 1, 1, NULL, '2024-06-01 08:00:00', '2024-06-01 09:30:00', 5400, '2024-06-01 07:55:00'),
(48, 'Python练习', 2, 0, 7200, '2024-06-02 19:00:00', '2024-06-02 19:40:00', 2400, '2024-06-02 18:55:00'),
(49, '历史背诵', 1, 1, NULL, '2024-06-03 07:00:00', '2024-06-03 08:00:00', 3600, '2024-06-03 06:55:00'),
(50, '编程练习', 1, 0, NULL, '2024-06-01 15:00:00', '2024-06-01 16:00:00', 3600, '2024-06-01 14:55:00');


-- =============================================
-- 插入积分记录数据
-- =============================================
INSERT INTO `points_record` (`user_id`, `change_type`, `change_points`, `left_points`, `source_type`, `change_time`) VALUES
-- 学生11（总分580）
(11, 1, 10, 10, 1, '2024-01-10 19:20:00'), -- 签到
(11, 1, 10, 20, 2, '2024-01-10 19:25:00'), -- 投票正确
(11, 1, 30, 50, 3, '2024-01-20 10:00:00'), -- 作业高分
(11, 1, 5, 55, 4, '2024-06-01 20:35:00'), -- 自习
(11, 2, -20, 35, 5, '2024-06-02 10:00:00'), -- 兑换道具
(11, 1, 20, 55, 3, '2024-01-28 09:00:00'), -- 作业
(11, 1, 10, 65, 1, '2024-01-20 19:05:00'), -- 签到
(11, 1, 10, 75, 2, '2024-01-20 19:10:00'), -- 投票正确
(11, 1, 30, 105, 3, '2024-02-10 08:00:00'), -- 考试
(11, 1, 5, 110, 4, '2024-06-03 19:35:00'), -- 自习
(11, 2, -50, 60, 5, '2024-06-05 09:00:00'), -- 兑换道具
(11, 1, 20, 80, 6, '2024-01-15 10:00:00'), -- 问答回复
(11, 1, 15, 95, 2, '2024-01-15 19:10:00'), -- 投票正确
(11, 1, 10, 105, 1, '2024-01-15 19:20:00'), -- 签到
(11, 1, 20, 125, 3, '2024-02-20 08:00:00'), -- 作业
(11, 1, 30, 155, 3, '2024-03-01 08:00:00'), -- 期末考试
(11, 1, 5, 160, 4, '2024-06-05 21:35:00'), -- 自习
(11, 2, -30, 130, 5, '2024-06-06 10:00:00'), -- 兑换道具
(11, 1, 50, 180, 3, '2024-03-10 08:00:00'), -- 作业高分
(11, 1, 10, 190, 1, '2024-01-24 19:05:00'), -- 签到
(11, 1, 10, 200, 2, '2024-01-24 19:15:00'), -- 投票正确
(11, 1, 20, 220, 6, '2024-01-20 14:00:00'), -- 问答回复
(11, 1, 30, 250, 3, '2024-02-05 08:00:00'), -- 作业
(11, 1, 10, 260, 1, '2024-03-06 19:05:00'), -- 签到
(11, 1, 10, 270, 2, '2024-03-06 19:10:00'), -- 投票正确
(11, 1, 20, 290, 3, '2024-03-15 08:00:00'), -- 考试
(11, 1, 5, 295, 4, '2024-06-02 17:05:00'), -- 自习
(11, 1, 30, 325, 3, '2024-04-01 08:00:00'), -- 作业
(11, 2, -30, 295, 5, '2024-06-10 10:00:00'), -- 兑换道具
(11, 1, 15, 310, 2, '2024-06-25 20:10:00'), -- 投票正确
(11, 1, 10, 320, 1, '2024-06-25 20:05:00'), -- 签到
(11, 1, 20, 340, 6, '2024-06-28 12:00:00'), -- 问答回复
(11, 1, 30, 370, 3, '2024-06-20 08:00:00'), -- 作业
(11, 1, 10, 380, 2, '2024-06-28 10:10:00'), -- 投票正确
(11, 1, 10, 390, 1, '2024-06-28 10:05:00'), -- 签到
(11, 1, 30, 420, 3, '2024-06-25 08:00:00'), -- 作业
(11, 2, -30, 390, 5, '2024-06-15 10:00:00'), -- 兑换道具
(11, 1, 20, 410, 6, '2024-06-20 15:00:00'), -- 问答回复
(11, 1, 10, 420, 2, '2024-07-05 10:00:00'), -- 投票正确
(11, 1, 20, 440, 3, '2024-07-01 08:00:00'), -- 作业
(11, 1, 10, 450, 1, '2024-07-05 09:00:00'), -- 签到
(11, 1, 30, 480, 3, '2024-07-10 08:00:00'), -- 考试
(11, 2, -50, 430, 5, '2024-07-01 10:00:00'), -- 兑换道具
(11, 1, 15, 445, 2, '2024-07-10 09:00:00'), -- 投票正确
(11, 1, 10, 455, 1, '2024-07-10 08:30:00'), -- 签到
(11, 1, 50, 505, 3, '2024-07-15 08:00:00'), -- 作业满分
(11, 1, 5, 510, 4, '2024-07-05 21:00:00'), -- 自习
(11, 1, 20, 530, 6, '2024-07-12 16:00:00'), -- 问答回复
(11, 1, 30, 560, 3, '2024-07-20 08:00:00'), -- 作业
(11, 2, -20, 540, 5, '2024-07-15 10:00:00'), -- 兑换道具
(11, 1, 10, 550, 2, '2024-07-20 09:00:00'), -- 投票正确
(11, 1, 10, 560, 1, '2024-07-20 08:30:00'), -- 签到
(11, 1, 20, 580, 6, '2024-07-22 15:00:00'), -- 问答回复
-- 学生12（总分320）
(12, 1, 10, 10, 1, '2024-01-10 19:20:00'), -- 签到
(12, 1, 10, 20, 2, '2024-01-10 19:25:00'), -- 投票正确
(12, 1, 30, 50, 3, '2024-01-20 11:00:00'), -- 作业
(12, 1, 5, 55, 4, '2024-06-01 20:50:00'), -- 自习
(12, 1, 15, 70, 2, '2024-01-20 19:10:00'), -- 投票正确
(12, 1, 10, 80, 1, '2024-01-20 19:05:00'), -- 签到
(12, 1, 20, 100, 3, '2024-02-10 08:00:00'), -- 考试
(12, 1, 5, 105, 4, '2024-06-02 17:05:00'), -- 自习
(12, 1, 10, 115, 2, '2024-03-02 08:00:00'), -- 投票正确
(12, 1, 15, 130, 3, '2024-03-02 10:00:00'), -- 作业
(12, 1, 10, 140, 1, '2024-03-02 09:00:00'), -- 签到
(12, 2, -50, 90, 5, '2024-03-05 10:00:00'), -- 兑换道具
(12, 1, 5, 95, 4, '2024-06-03 07:35:00'), -- 自习
(12, 1, 10, 105, 1, '2024-06-25 20:05:00'), -- 签到
(12, 1, 15, 120, 2, '2024-06-25 20:10:00'), -- 投票正确
(12, 1, 20, 140, 3, '2024-06-30 08:00:00'), -- 作业
(12, 1, 10, 150, 1, '2024-07-05 09:00:00'), -- 签到
(12, 1, 30, 180, 3, '2024-07-10 08:00:00'), -- 考试
(12, 2, -30, 150, 5, '2024-07-12 10:00:00'), -- 兑换道具
(12, 1, 20, 170, 6, '2024-07-15 16:00:00'), -- 问答回复
(12, 1, 50, 220, 3, '2024-07-20 08:00:00'), -- 作业满分
(12, 2, -40, 180, 5, '2024-07-18 10:00:00'), -- 兑换道具
(12, 1, 10, 190, 1, '2024-07-20 08:30:00'), -- 签到
(12, 1, 30, 220, 3, '2024-07-25 08:00:00'), -- 作业
(12, 1, 20, 240, 6, '2024-07-28 16:00:00'), -- 问答回复
(12, 1, 5, 245, 4, '2024-07-05 21:00:00'), -- 自习
(12, 1, 30, 275, 3, '2024-07-30 08:00:00'), -- 考试
(12, 1, 15, 290, 2, '2024-07-30 09:00:00'), -- 投票正确
(12, 1, 10, 300, 1, '2024-07-30 08:30:00'), -- 签到
(12, 2, -50, 250, 5, '2024-07-25 10:00:00'), -- 兑换道具
(12, 1, 20, 270, 6, '2024-08-01 16:00:00'), -- 问答回复
(12, 1, 50, 320, 3, '2024-08-05 08:00:00'), -- 作业满分
-- 学生13（总分450）
(13, 1, 10, 10, 1, '2024-01-10 19:20:00'), -- 签到
(13, 1, 0, 10, 2, '2024-01-10 19:25:00'), -- 投票错误
(13, 1, 20, 30, 3, '2024-01-20 09:00:00'), -- 作业
(13, 1, 5, 35, 4, '2024-06-01 21:05:00'), -- 自习
(13, 1, 15, 50, 2, '2024-01-15 19:10:00'), -- 投票正确
(13, 1, 10, 60, 1, '2024-01-15 19:20:00'), -- 签到
(13, 1, 30, 90, 3, '2024-01-28 10:00:00'), -- 作业
(13, 1, 5, 95, 4, '2024-06-03 16:35:00'), -- 自习
(13, 1, 10, 105, 1, '2024-01-24 19:05:00'), -- 签到
(13, 1, 0, 105, 2, '2024-01-24 19:15:00'), -- 投票错误
(13, 1, 20, 125, 3, '2024-02-06 09:00:00'), -- 作业
(13, 1, 10, 135, 2, '2024-02-21 08:00:00'), -- 投票正确
(13, 1, 10, 145, 1, '2024-02-21 09:00:00'), -- 签到
(13, 1, 30, 175, 3, '2024-02-10 08:00:00'), -- 考试
(13, 2, -50, 125, 5, '2024-02-15 10:00:00'), -- 兑换道具
(13, 1, 5, 130, 4, '2024-06-01 21:05:00'), -- 自习
(13, 1, 15, 145, 2, '2024-06-25 20:10:00'), -- 投票正确
(13, 1, 10, 155, 1, '2024-06-25 20:05:00'), -- 签到
(13, 1, 30, 185, 3, '2024-06-28 08:00:00'), -- 作业
(13, 2, -30, 155, 5, '2024-07-01 10:00:00'), -- 兑换道具
(13, 1, 20, 175, 6, '2024-07-05 16:00:00'), -- 问答回复
(13, 1, 10, 185, 1, '2024-07-07 09:00:00'), -- 签到
(13, 1, 50, 235, 3, '2024-07-10 08:00:00'), -- 考试满分
(13, 2, -40, 195, 5, '2024-07-12 10:00:00'), -- 兑换道具
(13, 1, 15, 210, 2, '2024-07-10 09:00:00'), -- 投票正确
(13, 1, 20, 230, 3, '2024-07-15 08:00:00'), -- 作业
(13, 1, 5, 235, 4, '2024-07-03 16:35:00'), -- 自习
(13, 1, 10, 245, 1, '2024-07-17 09:00:00'), -- 签到
(13, 1, 30, 275, 3, '2024-07-20 08:00:00'), -- 作业
(13, 2, -30, 245, 5, '2024-07-18 10:00:00'), -- 兑换道具
(13, 1, 20, 265, 6, '2024-07-22 16:00:00'), -- 问答回复
(13, 1, 50, 315, 3, '2024-07-25 08:00:00'), -- 作业满分
(13, 1, 15, 330, 2, '2024-07-25 09:00:00'), -- 投票正确
(13, 1, 10, 340, 1, '2024-07-25 08:30:00'), -- 签到
(13, 2, -40, 300, 5, '2024-07-22 10:00:00'), -- 兑换道具
(13, 1, 50, 350, 3, '2024-07-30 08:00:00'), -- 考试满分
(13, 1, 20, 370, 6, '2024-08-01 16:00:00'), -- 问答回复
(13, 2, -30, 340, 5, '2024-07-30 10:00:00'), -- 兑换道具
(13, 1, 10, 350, 1, '2024-08-05 09:00:00'), -- 签到
(13, 1, 50, 400, 3, '2024-08-05 08:00:00'), -- 作业满分
(13, 1, 50, 450, 3, '2024-08-10 08:00:00'), -- 考试满分
-- 学生14（总分210）
(14, 1, 10, 10, 1, '2024-01-10 19:20:00'), -- 签到
(14, 1, 10, 20, 2, '2024-01-10 19:25:00'), -- 投票正确
(14, 1, 10, 30, 3, '2024-01-20 09:00:00'), -- 作业
(14, 1, 5, 35, 4, '2024-06-02 15:05:00'), -- 自习
(14, 1, 0, 35, 2, '2024-01-20 19:10:00'), -- 投票错误
(14, 1, 10, 45, 1, '2024-01-20 19:05:00'), -- 签到
(14, 1, 20, 65, 3, '2024-01-25 11:00:00'), -- 作业
(14, 1, 10, 75, 2, '2024-03-06 19:10:00'), -- 投票正确
(14, 1, 10, 85, 1, '2024-03-06 19:05:00'), -- 签到
(14, 1, 30, 115, 3, '2024-03-20 08:00:00'), -- 作业
(14, 2, -50, 65, 5, '2024-03-22 10:00:00'), -- 兑换道具
(14, 1, 5, 70, 4, '2024-06-01 18:30:00'), -- 自习
(14, 1, 10, 80, 1, '2024-01-15 19:20:00'), -- 签到
(14, 1, 30, 110, 3, '2024-02-18 08:00:00'), -- 作业
(14, 2, -30, 80, 5, '2024-02-20 10:00:00'), -- 兑换道具
(14, 1, 15, 95, 2, '2024-07-05 10:00:00'), -- 投票正确
(14, 1, 10, 105, 1, '2024-07-05 09:00:00'), -- 签到
(14, 1, 20, 125, 3, '2024-07-10 08:00:00'), -- 作业
(14, 2, -20, 105, 5, '2024-07-08 10:00:00'), -- 兑换道具
(14, 1, 50, 155, 3, '2024-07-15 08:00:00'), -- 考试满分
(14, 1, 10, 165, 2, '2024-07-15 09:00:00'), -- 投票正确
(14, 2, -30, 135, 5, '2024-07-12 10:00:00'), -- 兑换道具
(14, 1, 30, 165, 3, '2024-07-20 08:00:00'), -- 作业
(14, 1, 20, 185, 6, '2024-07-22 16:00:00'), -- 问答回复
(14, 1, 10, 195, 1, '2024-07-20 08:30:00'), -- 签到
(14, 2, -40, 155, 5, '2024-07-18 10:00:00'), -- 兑换道具
(14, 1, 50, 205, 3, '2024-07-25 08:00:00'), -- 作业满分
(14, 1, 5, 210, 4, '2024-07-20 21:00:00'), -- 自习
-- 学生15（总分670）
(15, 1, 10, 10, 1, '2024-01-10 19:20:00'),
(15, 1, 10, 20, 2, '2024-01-10 19:25:00'),
(15, 1, 30, 50, 3, '2024-01-20 10:00:00'),
(15, 1, 5, 55, 4, '2024-06-01 10:35:00'),
(15, 1, 10, 65, 1, '2024-01-20 19:05:00'),
(15, 1, 10, 75, 2, '2024-01-20 19:10:00'),
(15, 1, 30, 105, 3, '2024-01-26 10:00:00'),
(15, 1, 5, 110, 4, '2024-06-04 09:05:00'),
(15, 1, 20, 130, 6, '2024-01-20 14:00:00'),
(15, 1, 20, 150, 3, '2024-01-28 10:00:00'),
(15, 1, 10, 160, 1, '2024-01-24 19:05:00'),
(15, 1, 10, 170, 2, '2024-01-24 19:15:00'),
(15, 1, 30, 200, 3, '2024-02-10 08:00:00'),
(15, 2, -50, 150, 5, '2024-02-12 10:00:00'),
(15, 1, 5, 155, 4, '2024-06-03 14:30:00'),
(15, 1, 30, 185, 3, '2024-02-20 08:00:00'),
(15, 1, 20, 205, 6, '2024-02-05 16:00:00'),
(15, 1, 10, 215, 1, '2024-03-06 19:05:00'),
(15, 1, 10, 225, 2, '2024-03-06 19:10:00'),
(15, 1, 30, 255, 3, '2024-03-15 08:00:00'),
(15, 2, -30, 225, 5, '2024-03-18 10:00:00'),
(15, 1, 30, 255, 3, '2024-03-20 08:00:00'),
(15, 2, -40, 215, 5, '2024-03-22 10:00:00'),
(15, 1, 20, 235, 6, '2024-03-25 16:00:00'),
(15, 1, 50, 285, 3, '2024-04-01 08:00:00'),
(15, 2, -50, 235, 5, '2024-04-05 10:00:00'),
(15, 1, 5, 240, 4, '2024-06-02 10:35:00'),
(15, 1, 10, 250, 1, '2024-06-25 20:05:00'),
(15, 1, 15, 265, 2, '2024-06-25 20:10:00'),
(15, 1, 30, 295, 3, '2024-06-30 08:00:00'),
(15, 2, -30, 265, 5, '2024-07-01 10:00:00'),
(15, 1, 20, 285, 6, '2024-07-03 16:00:00'),
(15, 1, 10, 295, 1, '2024-07-05 09:00:00'),
(15, 1, 50, 345, 3, '2024-07-10 08:00:00'),
(15, 2, -40, 305, 5, '2024-07-08 10:00:00'),
(15, 1, 15, 320, 2, '2024-07-10 09:00:00'),
(15, 1, 20, 340, 3, '2024-07-15 08:00:00'),
(15, 1, 5, 345, 4, '2024-07-01 10:35:00'),
(15, 2, -30, 315, 5, '2024-07-15 10:00:00'),
(15, 1, 50, 365, 3, '2024-07-20 08:00:00'),
(15, 2, -50, 315, 5, '2024-07-18 10:00:00'),
(15, 1, 20, 335, 6, '2024-07-22 16:00:00'),
(15, 1, 50, 385, 3, '2024-07-25 08:00:00'),
(15, 1, 15, 400, 2, '2024-07-25 09:00:00'),
(15, 1, 10, 410, 1, '2024-07-25 08:30:00'),
(15, 2, -40, 370, 5, '2024-07-22 10:00:00'),
(15, 1, 50, 420, 3, '2024-07-30 08:00:00'),
(15, 2, -30, 390, 5, '2024-07-28 10:00:00'),
(15, 1, 20, 410, 6, '2024-08-01 16:00:00'),
(15, 1, 50, 460, 3, '2024-08-05 08:00:00'),
(15, 2, -40, 420, 5, '2024-08-02 10:00:00'),
(15, 1, 50, 470, 3, '2024-08-10 08:00:00'),
(15, 1, 20, 490, 6, '2024-08-08 16:00:00'),
(15, 1, 30, 520, 3, '2024-08-12 08:00:00'),
(15, 1, 50, 570, 3, '2024-08-15 08:00:00'),
(15, 2, -50, 520, 5, '2024-08-12 10:00:00'),
(15, 1, 50, 570, 3, '2024-08-18 08:00:00'),
(15, 1, 20, 590, 6, '2024-08-20 16:00:00'),
(15, 2, -40, 550, 5, '2024-08-18 10:00:00'),
(15, 1, 50, 600, 3, '2024-08-22 08:00:00'),
(15, 1, 30, 630, 3, '2024-08-25 08:00:00'),
(15, 1, 40, 670, 3, '2024-08-28 08:00:00'),
-- 学生16（总分180）
(16, 1, 10, 10, 1, '2024-01-10 19:20:00'),
(16, 1, 10, 20, 2, '2024-01-10 19:25:00'),
(16, 1, 20, 40, 3, '2024-01-20 10:00:00'),
(16, 1, 5, 45, 4, '2024-06-03 20:05:00'),
(16, 1, 10, 55, 1, '2024-01-20 19:05:00'),
(16, 1, 10, 65, 2, '2024-01-20 19:10:00'),
(16, 1, 30, 95, 3, '2024-01-28 10:00:00'),
(16, 1, 5, 100, 4, '2024-06-03 20:05:00'),
(16, 1, 20, 120, 3, '2024-02-05 08:00:00'),
(16, 2, -30, 90, 5, '2024-02-08 10:00:00'),
(16, 1, 10, 100, 1, '2024-01-24 19:05:00'),
(16, 1, 30, 130, 3, '2024-02-20 08:00:00'),
(16, 1, 10, 140, 2, '2024-01-15 19:10:00'),
(16, 1, 10, 150, 1, '2024-01-15 19:20:00'),
(16, 2, -30, 120, 5, '2024-06-05 10:00:00'),
(16, 1, 30, 150, 3, '2024-03-01 08:00:00'),
(16, 1, 30, 180, 3, '2024-03-15 08:00:00'),
-- 学生17（总分890）- 更多记录
(17, 1, 10, 10, 1, '2024-01-10 19:20:00'),
(17, 1, 0, 10, 2, '2024-01-10 19:25:00'),
(17, 1, 30, 40, 3, '2024-01-20 10:00:00'),
(17, 1, 5, 45, 4, '2024-06-01 10:05:00'),
(17, 1, 10, 55, 1, '2024-01-24 19:05:00'),
(17, 1, 10, 65, 2, '2024-01-24 19:15:00'),
(17, 1, 30, 95, 3, '2024-01-28 10:00:00'),
(17, 1, 5, 100, 4, '2024-06-02 10:35:00'),
(17, 1, 20, 120, 6, '2024-01-20 14:00:00'),
(17, 1, 30, 150, 3, '2024-02-06 09:00:00'),
(17, 2, -50, 100, 5, '2024-02-08 10:00:00'),
(17, 1, 10, 110, 1, '2024-02-21 09:00:00'),
(17, 1, 10, 120, 2, '2024-02-21 08:00:00'),
(17, 1, 30, 150, 3, '2024-02-10 08:00:00'),
(17, 2, -30, 120, 5, '2024-02-12 10:00:00'),
(17, 1, 5, 125, 4, '2024-06-03 16:35:00'),
(17, 1, 30, 155, 3, '2024-02-20 08:00:00'),
(17, 1, 20, 175, 6, '2024-02-05 16:00:00'),
(17, 1, 10, 185, 1, '2024-06-25 20:05:00'),
(17, 1, 15, 200, 2, '2024-06-25 20:10:00'),
(17, 1, 30, 230, 3, '2024-06-28 08:00:00'),
(17, 2, -30, 200, 5, '2024-07-01 10:00:00'),
(17, 1, 20, 220, 6, '2024-07-03 16:00:00'),
(17, 1, 10, 230, 1, '2024-07-05 09:00:00'),
(17, 1, 50, 280, 3, '2024-07-10 08:00:00'),
(17, 2, -40, 240, 5, '2024-07-08 10:00:00'),
(17, 1, 15, 255, 2, '2024-07-10 09:00:00'),
(17, 1, 20, 275, 3, '2024-07-15 08:00:00'),
(17, 1, 5, 280, 4, '2024-07-01 10:35:00'),
(17, 2, -30, 250, 5, '2024-07-15 10:00:00'),
(17, 1, 50, 300, 3, '2024-07-20 08:00:00'),
(17, 2, -50, 250, 5, '2024-07-18 10:00:00'),
(17, 1, 20, 270, 6, '2024-07-22 16:00:00'),
(17, 1, 50, 320, 3, '2024-07-25 08:00:00'),
(17, 1, 15, 335, 2, '2024-07-25 09:00:00'),
(17, 1, 10, 345, 1, '2024-07-25 08:30:00'),
(17, 2, -40, 305, 5, '2024-07-22 10:00:00'),
(17, 1, 50, 355, 3, '2024-07-30 08:00:00'),
(17, 2, -30, 325, 5, '2024-07-28 10:00:00'),
(17, 1, 20, 345, 6, '2024-08-01 16:00:00'),
(17, 1, 50, 395, 3, '2024-08-05 08:00:00'),
(17, 2, -40, 355, 5, '2024-08-02 10:00:00'),
(17, 1, 50, 405, 3, '2024-08-10 08:00:00'),
(17, 1, 20, 425, 6, '2024-08-08 16:00:00'),
(17, 1, 30, 455, 3, '2024-08-12 08:00:00'),
(17, 1, 50, 505, 3, '2024-08-15 08:00:00'),
(17, 2, -50, 455, 5, '2024-08-12 10:00:00'),
(17, 1, 50, 505, 3, '2024-08-18 08:00:00'),
(17, 1, 20, 525, 6, '2024-08-20 16:00:00'),
(17, 2, -40, 485, 5, '2024-08-18 10:00:00'),
(17, 1, 50, 535, 3, '2024-08-22 08:00:00'),
(17, 1, 30, 565, 3, '2024-08-25 08:00:00'),
(17, 1, 40, 605, 3, '2024-08-28 08:00:00'),
(17, 1, 50, 655, 3, '2024-09-01 08:00:00'),
(17, 1, 30, 685, 3, '2024-09-05 08:00:00'),
(17, 1, 50, 735, 3, '2024-09-08 08:00:00'),
(17, 1, 20, 755, 6, '2024-09-10 16:00:00'),
(17, 2, -40, 715, 5, '2024-09-08 10:00:00'),
(17, 1, 50, 765, 3, '2024-09-12 08:00:00'),
(17, 1, 30, 795, 3, '2024-09-15 08:00:00'),
(17, 1, 50, 845, 3, '2024-09-18 08:00:00'),
(17, 1, 30, 875, 3, '2024-09-20 08:00:00'),
(17, 1, 15, 890, 2, '2024-09-20 09:00:00'),
-- 学生18-50（简略版，每个学生基础记录）
(18, 1, 10, 10, 1, '2024-01-10 19:20:00'),
(18, 1, 0, 10, 2, '2024-01-10 19:25:00'),
(18, 1, 15, 25, 3, '2024-01-20 10:00:00'),
(18, 1, 5, 30, 4, '2024-06-01 20:05:00'),
(18, 1, 10, 40, 1, '2024-01-24 19:05:00'),
(18, 1, 15, 55, 3, '2024-01-28 10:00:00'),
(19, 1, 10, 10, 1, '2024-01-10 19:20:00'),
(19, 1, 0, 10, 2, '2024-01-10 19:25:00'),
(19, 1, 20, 30, 3, '2024-01-20 10:00:00'),
(19, 1, 5, 35, 4, '2024-06-02 21:35:00'),
(19, 1, 10, 45, 1, '2024-01-15 19:20:00'),
(19, 1, 10, 55, 2, '2024-01-15 19:10:00'),
(19, 1, 30, 85, 3, '2024-01-28 10:00:00'),
(19, 2, -30, 55, 5, '2024-02-01 10:00:00'),
(19, 1, 5, 60, 4, '2024-06-03 20:05:00'),
(19, 1, 30, 90, 3, '2024-02-20 08:00:00'),
(19, 1, 10, 100, 1, '2024-03-06 19:05:00'),
(19, 1, 10, 110, 2, '2024-03-06 19:10:00'),
(19, 1, 20, 130, 3, '2024-03-20 08:00:00'),
(19, 2, -30, 100, 5, '2024-03-22 10:00:00'),
(19, 1, 20, 120, 6, '2024-03-25 16:00:00'),
(19, 1, 30, 150, 3, '2024-04-01 08:00:00'),
(20, 1, 10, 10, 1, '2024-01-10 19:20:00'),
(20, 1, 10, 20, 2, '2024-01-10 19:25:00'),
(20, 1, 30, 50, 3, '2024-01-20 10:00:00'),
(20, 1, 5, 55, 4, '2024-06-01 16:35:00'),
(20, 1, 10, 65, 1, '2024-01-20 19:05:00'),
(20, 1, 10, 75, 2, '2024-01-20 19:10:00'),
(20, 1, 30, 105, 3, '2024-01-28 10:00:00'),
(20, 2, -50, 55, 5, '2024-02-01 10:00:00'),
(20, 1, 5, 60, 4, '2024-06-03 07:35:00'),
(20, 1, 20, 80, 6, '2024-01-20 14:00:00'),
(20, 1, 30, 110, 3, '2024-02-10 08:00:00'),
(20, 1, 10, 120, 2, '2024-03-02 08:00:00'),
(20, 1, 10, 130, 1, '2024-03-02 09:00:00'),
(20, 1, 20, 150, 3, '2024-03-15 08:00:00'),
(20, 2, -30, 120, 5, '2024-03-18 10:00:00'),
(20, 1, 30, 150, 3, '2024-04-01 08:00:00'),
(20, 2, -30, 120, 5, '2024-04-05 10:00:00'),
(20, 1, 50, 170, 3, '2024-05-01 08:00:00'),
(20, 1, 20, 190, 6, '2024-05-10 16:00:00'),
(20, 1, 30, 220, 3, '2024-06-01 08:00:00'),
(20, 2, -40, 180, 5, '2024-06-05 10:00:00'),
(20, 1, 50, 230, 3, '2024-07-01 08:00:00'),
(20, 1, 20, 250, 6, '2024-07-10 16:00:00'),
(20, 1, 30, 280, 3, '2024-07-15 08:00:00'),
(20, 2, -30, 250, 5, '2024-07-18 10:00:00'),
(20, 1, 50, 300, 3, '2024-07-20 08:00:00'),
(20, 1, 30, 330, 3, '2024-07-25 08:00:00'),
(20, 2, -40, 290, 5, '2024-07-22 10:00:00'),
(20, 1, 50, 340, 3, '2024-07-30 08:00:00'),
(20, 1, 50, 390, 3, '2024-08-05 08:00:00'),
(20, 2, -50, 340, 5, '2024-08-02 10:00:00'),
(20, 1, 50, 390, 3, '2024-08-10 08:00:00'),
(20, 1, 30, 420, 3, '2024-08-15 08:00:00'),
(20, 2, -30, 390, 5, '2024-08-12 10:00:00'),
(20, 1, 50, 440, 3, '2024-08-20 08:00:00'),
(20, 1, 30, 470, 3, '2024-08-25 08:00:00'),
(20, 2, -40, 430, 5, '2024-08-22 10:00:00'),
(20, 1, 50, 480, 3, '2024-08-28 08:00:00'),
(20, 1, 50, 530, 3, '2024-09-01 08:00:00'),
(20, 2, -50, 480, 5, '2024-09-02 10:00:00'),
(20, 1, 50, 530, 3, '2024-09-05 08:00:00'),
(20, 1, 50, 580, 3, '2024-09-10 08:00:00'),
(20, 1, 50, 630, 3, '2024-09-15 08:00:00'),
(20, 1, 50, 680, 3, '2024-09-20 08:00:00'),
(20, 2, -30, 650, 5, '2024-09-18 10:00:00'),
(20, 1, 30, 680, 3, '2024-09-22 08:00:00'),
(20, 1, 50, 730, 3, '2024-09-25 08:00:00'),
-- 学生21-50 简略记录
(21, 1, 10, 10, 1, '2024-01-15 19:20:00'),
(21, 1, 10, 20, 2, '2024-01-15 19:10:00'),
(21, 1, 20, 40, 3, '2024-01-28 10:00:00'),
(21, 1, 5, 45, 4, '2024-06-02 20:35:00'),
(21, 1, 10, 55, 1, '2024-03-06 19:05:00'),
(21, 1, 10, 65, 2, '2024-03-06 19:10:00'),
(21, 1, 20, 85, 3, '2024-03-20 08:00:00'),
(21, 1, 20, 105, 6, '2024-03-25 16:00:00'),
(22, 1, 10, 10, 1, '2024-01-10 19:20:00'),
(22, 1, 10, 20, 2, '2024-01-10 19:25:00'),
(22, 1, 30, 50, 3, '2024-01-20 10:00:00'),
(22, 1, 5, 55, 4, '2024-06-01 16:05:00'),
(22, 1, 10, 65, 1, '2024-01-24 19:05:00'),
(22, 1, 30, 95, 3, '2024-02-06 09:00:00'),
(22, 2, -50, 45, 5, '2024-02-08 10:00:00'),
(22, 1, 10, 55, 2, '2024-02-21 08:00:00'),
(22, 1, 10, 65, 1, '2024-02-21 09:00:00'),
(22, 1, 30, 95, 3, '2024-02-10 08:00:00'),
(22, 1, 5, 100, 4, '2024-06-03 16:35:00'),
(22, 1, 30, 130, 3, '2024-02-20 08:00:00'),
(22, 2, -30, 100, 5, '2024-02-22 10:00:00'),
(22, 1, 20, 120, 6, '2024-02-05 16:00:00'),
(23, 1, 10, 10, 1, '2024-01-15 19:20:00'),
(23, 1, 10, 20, 2, '2024-01-15 19:10:00'),
(23, 1, 20, 40, 3, '2024-01-28 10:00:00'),
(23, 1, 5, 45, 4, '2024-06-03 21:05:00'),
(23, 1, 10, 55, 1, '2024-01-20 19:05:00'),
(23, 1, 15, 70, 2, '2024-06-25 20:10:00'),
(23, 1, 10, 80, 1, '2024-06-25 20:05:00'),
(23, 1, 20, 100, 3, '2024-06-28 08:00:00'),
(24, 1, 10, 10, 1, '2024-03-06 19:05:00'),
(24, 1, 0, 10, 2, '2024-03-06 19:10:00'),
(24, 1, 20, 30, 3, '2024-03-20 08:00:00'),
(24, 1, 5, 35, 4, '2024-06-01 19:05:00'),
(25, 1, 10, 10, 1, '2024-01-20 19:05:00'),
(25, 1, 10, 20, 2, '2024-01-20 19:10:00'),
(25, 1, 20, 40, 3, '2024-01-28 10:00:00'),
(25, 1, 5, 45, 4, '2024-06-02 10:35:00'),
(25, 1, 10, 55, 1, '2024-03-02 09:00:00'),
(25, 1, 10, 65, 2, '2024-03-02 08:00:00'),
(25, 1, 20, 85, 3, '2024-03-15 08:00:00'),
(25, 1, 20, 105, 6, '2024-03-20 16:00:00'),
(25, 1, 30, 135, 3, '2024-04-01 08:00:00'),
(25, 2, -30, 105, 5, '2024-04-05 10:00:00'),
(25, 1, 30, 135, 3, '2024-05-01 08:00:00'),
(25, 1, 30, 165, 3, '2024-06-01 08:00:00'),
(25, 2, -40, 125, 5, '2024-06-05 10:00:00'),
(25, 1, 30, 155, 3, '2024-07-01 08:00:00'),
(25, 1, 50, 205, 3, '2024-07-20 08:00:00'),
(25, 2, -40, 165, 5, '2024-07-18 10:00:00'),
(25, 1, 50, 215, 3, '2024-08-01 08:00:00'),
(25, 1, 50, 265, 3, '2024-08-15 08:00:00'),
(25, 2, -50, 215, 5, '2024-08-12 10:00:00'),
(25, 1, 50, 265, 3, '2024-08-20 08:00:00'),
(25, 1, 30, 295, 3, '2024-08-25 08:00:00'),
(25, 2, -40, 255, 5, '2024-08-22 10:00:00'),
(25, 1, 50, 305, 3, '2024-08-30 08:00:00'),
(25, 1, 50, 355, 3, '2024-09-05 08:00:00'),
(25, 2, -50, 305, 5, '2024-09-02 10:00:00'),
(25, 1, 50, 355, 3, '2024-09-10 08:00:00'),
(25, 1, 50, 405, 3, '2024-09-15 08:00:00'),
(25, 2, -30, 375, 5, '2024-09-12 10:00:00'),
(25, 1, 50, 425, 3, '2024-09-20 08:00:00'),
(26, 1, 10, 10, 1, '2024-02-06 09:00:00'),
(26, 1, 20, 30, 3, '2024-02-10 08:00:00'),
(26, 1, 5, 35, 4, '2024-06-01 20:50:00'),
(26, 1, 10, 45, 1, '2024-01-15 19:20:00'),
(26, 1, 10, 55, 2, '2024-01-15 19:10:00'),
(26, 1, 30, 85, 3, '2024-01-28 10:00:00'),
(27, 1, 10, 10, 1, '2024-03-06 19:05:00'),
(27, 1, 10, 20, 2, '2024-03-06 19:10:00'),
(27, 1, 20, 40, 3, '2024-03-20 08:00:00'),
(27, 1, 5, 45, 4, '2024-06-02 16:35:00'),
(28, 1, 10, 10, 1, '2024-01-20 19:05:00'),
(28, 1, 10, 20, 2, '2024-01-20 19:10:00'),
(28, 1, 20, 40, 3, '2024-01-28 10:00:00'),
(28, 1, 5, 45, 4, '2024-06-01 20:05:00'),
(28, 1, 10, 55, 1, '2024-03-02 11:00:00'),
(28, 1, 10, 65, 2, '2024-03-02 08:00:00'),
(28, 1, 20, 85, 3, '2024-03-15 08:00:00'),
(29, 1, 10, 10, 1, '2024-01-15 19:20:00'),
(29, 1, 10, 20, 2, '2024-01-15 19:10:00'),
(29, 1, 30, 50, 3, '2024-01-20 10:00:00'),
(29, 1, 5, 55, 4, '2024-06-03 15:35:00'),
(29, 1, 10, 65, 1, '2024-02-04 11:00:00'),
(29, 1, 10, 75, 2, '2024-02-21 08:00:00'),
(29, 1, 30, 105, 3, '2024-02-10 08:00:00'),
(29, 1, 20, 125, 6, '2024-02-05 16:00:00'),
(29, 1, 30, 155, 3, '2024-03-01 08:00:00'),
(29, 2, -30, 125, 5, '2024-03-05 10:00:00'),
(30, 1, 10, 10, 1, '2024-02-06 09:00:00'),
(30, 1, 10, 20, 2, '2024-02-21 08:00:00'),
(30, 1, 20, 40, 3, '2024-02-10 08:00:00'),
(30, 1, 5, 45, 4, '2024-06-01 11:05:00'),
(30, 1, 10, 55, 1, '2024-06-25 20:05:00'),
(30, 1, 0, 55, 2, '2024-06-25 20:10:00'),
(30, 1, 20, 75, 3, '2024-06-28 08:00:00'),
(31, 1, 10, 10, 1, '2024-03-06 19:05:00'),
(31, 1, 10, 20, 2, '2024-03-06 19:10:00'),
(31, 1, 20, 40, 3, '2024-03-20 08:00:00'),
(31, 1, 5, 45, 4, '2024-06-02 20:35:00'),
(31, 1, 20, 65, 6, '2024-03-25 16:00:00'),
(32, 1, 10, 10, 1, '2024-01-15 19:20:00'),
(32, 1, 10, 20, 2, '2024-01-15 19:10:00'),
(32, 1, 20, 40, 3, '2024-01-28 10:00:00'),
(32, 1, 5, 45, 4, '2024-06-01 17:05:00'),
(32, 1, 10, 55, 1, '2024-03-06 19:05:00'),
(32, 1, 30, 85, 3, '2024-03-15 08:00:00'),
(33, 1, 10, 10, 1, '2024-03-06 19:05:00'),
(33, 1, 10, 20, 2, '2024-03-06 19:10:00'),
(33, 1, 20, 40, 3, '2024-03-20 08:00:00'),
(33, 1, 5, 45, 4, '2024-06-03 10:35:00'),
(33, 1, 10, 55, 1, '2024-06-25 20:05:00'),
(33, 1, 10, 65, 2, '2024-06-25 20:10:00'),
(33, 1, 20, 85, 3, '2024-06-28 08:00:00'),
(34, 1, 10, 10, 1, '2024-01-15 19:20:00'),
(34, 1, 10, 20, 2, '2024-01-15 19:10:00'),
(34, 1, 15, 35, 3, '2024-01-28 10:00:00'),
(34, 1, 5, 40, 4, '2024-06-01 20:35:00'),
(35, 1, 10, 10, 1, '2024-01-15 19:20:00'),
(35, 1, 10, 20, 2, '2024-01-15 19:10:00'),
(35, 1, 20, 40, 3, '2024-01-28 10:00:00'),
(35, 1, 5, 45, 4, '2024-06-02 11:35:00'),
(35, 1, 10, 55, 1, '2024-02-04 11:00:00'),
(35, 1, 10, 65, 2, '2024-02-18 08:00:00'),
(35, 1, 20, 85, 3, '2024-03-15 08:00:00'),
(35, 1, 20, 105, 6, '2024-03-20 16:00:00'),
(35, 1, 30, 135, 3, '2024-04-01 08:00:00'),
(35, 2, -30, 105, 5, '2024-04-05 10:00:00'),
(35, 1, 50, 155, 3, '2024-05-01 08:00:00'),
(35, 1, 30, 185, 3, '2024-06-01 08:00:00'),
(35, 2, -40, 145, 5, '2024-06-05 10:00:00'),
(35, 1, 50, 195, 3, '2024-07-01 08:00:00'),
(35, 1, 30, 225, 3, '2024-07-20 08:00:00'),
(35, 2, -30, 195, 5, '2024-07-18 10:00:00'),
(35, 1, 50, 245, 3, '2024-08-01 08:00:00'),
(35, 1, 50, 295, 3, '2024-08-15 08:00:00'),
(35, 2, -50, 245, 5, '2024-08-12 10:00:00'),
(35, 1, 50, 295, 3, '2024-08-20 08:00:00'),
(35, 1, 50, 345, 3, '2024-08-25 08:00:00'),
(35, 2, -30, 315, 5, '2024-08-22 10:00:00'),
(35, 1, 50, 365, 3, '2024-08-30 08:00:00'),
(35, 1, 50, 415, 3, '2024-09-05 08:00:00'),
(35, 2, -50, 365, 5, '2024-09-02 10:00:00'),
(35, 1, 50, 415, 3, '2024-09-10 08:00:00'),
(35, 1, 30, 445, 3, '2024-09-15 08:00:00'),
(35, 1, 50, 495, 3, '2024-09-20 08:00:00'),
(36, 1, 10, 10, 1, '2024-03-06 19:05:00'),
(36, 1, 10, 20, 2, '2024-03-06 19:10:00'),
(36, 1, 20, 40, 3, '2024-03-20 08:00:00'),
(36, 1, 5, 45, 4, '2024-06-01 18:25:00'),
(37, 1, 10, 10, 1, '2024-02-21 08:00:00'),
(37, 1, 10, 20, 2, '2024-02-21 09:00:00'),
(37, 1, 20, 40, 3, '2024-02-22 10:00:00'),
(37, 1, 5, 45, 4, '2024-06-01 21:05:00'),
(37, 1, 10, 55, 1, '2024-03-22 11:00:00'),
(37, 1, 10, 65, 2, '2024-03-22 10:00:00'),
(37, 1, 20, 85, 3, '2024-03-24 08:00:00'),
(37, 1, 20, 105, 6, '2024-03-28 16:00:00'),
(37, 1, 30, 135, 3, '2024-04-01 08:00:00'),
(37, 2, -30, 105, 5, '2024-04-05 10:00:00'),
(37, 1, 50, 155, 3, '2024-05-01 08:00:00'),
(37, 1, 30, 185, 3, '2024-06-01 08:00:00'),

-- 学生38（总分40）
(38, 1, 10, 10, 1, '2024-01-10 19:20:00'),
(38, 1, 0, 10, 2, '2024-01-10 19:25:00'),
(38, 1, 15, 25, 3, '2024-01-20 10:00:00'),
(38, 1, 5, 30, 4, '2024-06-01 19:35:00'),
(38, 1, 10, 40, 3, '2024-02-18 08:00:00'),

-- 学生39（总分290）
(39, 1, 10, 10, 1, '2024-03-02 08:00:00'),
(39, 1, 10, 20, 2, '2024-03-02 09:00:00'),
(39, 1, 20, 40, 3, '2024-03-03 10:00:00'),
(39, 1, 5, 45, 4, '2024-06-03 19:05:00'),
(39, 1, 10, 55, 1, '2024-03-03 09:00:00'),
(39, 1, 10, 65, 2, '2024-03-03 10:00:00'),
(39, 1, 20, 85, 3, '2024-03-10 08:00:00'),
(39, 1, 5, 90, 4, '2024-06-03 19:05:00'),
(39, 1, 20, 110, 6, '2024-03-12 16:00:00'),
(39, 1, 30, 140, 3, '2024-03-15 08:00:00'),
(39, 2, -30, 110, 5, '2024-03-18 10:00:00'),
(39, 1, 30, 140, 3, '2024-04-01 08:00:00'),
(39, 1, 30, 170, 3, '2024-05-01 08:00:00'),
(39, 2, -40, 130, 5, '2024-05-05 10:00:00'),
(39, 1, 30, 160, 3, '2024-06-01 08:00:00'),
(39, 1, 50, 210, 3, '2024-07-01 08:00:00'),
(39, 2, -30, 180, 5, '2024-07-05 10:00:00'),
(39, 1, 50, 230, 3, '2024-07-15 08:00:00'),
(39, 1, 30, 260, 3, '2024-08-01 08:00:00'),
(39, 2, -40, 220, 5, '2024-08-05 10:00:00'),
(39, 1, 50, 270, 3, '2024-08-15 08:00:00'),
(39, 1, 20, 290, 6, '2024-08-20 16:00:00'),

-- 学生40（总分140）
(40, 1, 10, 10, 1, '2024-02-04 08:00:00'),
(40, 1, 10, 20, 2, '2024-02-04 09:00:00'),
(40, 1, 20, 40, 3, '2024-02-05 08:00:00'),
(40, 1, 5, 45, 4, '2024-06-01 10:35:00'),
(40, 1, 10, 55, 1, '2024-02-18 08:00:00'),
(40, 1, 10, 65, 2, '2024-02-18 09:00:00'),
(40, 1, 20, 85, 3, '2024-02-19 10:00:00'),
(40, 1, 5, 90, 4, '2024-06-02 10:35:00'),
(40, 1, 20, 110, 3, '2024-03-01 08:00:00'),
(40, 2, -30, 80, 5, '2024-03-05 10:00:00'),
(40, 1, 30, 110, 3, '2024-04-01 08:00:00'),
(40, 1, 30, 140, 3, '2024-05-01 08:00:00'),

-- 学生41（总分410）
(41, 1, 10, 10, 1, '2024-03-05 08:00:00'),
(41, 1, 10, 20, 2, '2024-03-05 09:00:00'),
(41, 1, 20, 40, 3, '2024-03-10 08:00:00'),
(41, 1, 5, 45, 4, '2024-06-02 15:05:00'),
(41, 1, 10, 55, 1, '2024-03-06 09:00:00'),
(41, 1, 10, 65, 2, '2024-03-06 10:00:00'),
(41, 1, 20, 85, 3, '2024-03-15 08:00:00'),
(41, 1, 20, 105, 6, '2024-03-20 16:00:00'),
(41, 1, 30, 135, 3, '2024-04-01 08:00:00'),
(41, 2, -30, 105, 5, '2024-04-05 10:00:00'),
(41, 1, 30, 135, 3, '2024-05-01 08:00:00'),
(41, 1, 30, 165, 3, '2024-06-01 08:00:00'),
(41, 2, -40, 125, 5, '2024-06-05 10:00:00'),
(41, 1, 50, 175, 3, '2024-07-01 08:00:00'),
(41, 1, 50, 225, 3, '2024-07-20 08:00:00'),
(41, 2, -30, 195, 5, '2024-07-22 10:00:00'),
(41, 1, 50, 245, 3, '2024-08-01 08:00:00'),
(41, 1, 50, 295, 3, '2024-08-15 08:00:00'),
(41, 2, -40, 255, 5, '2024-08-18 10:00:00'),
(41, 1, 50, 305, 3, '2024-08-25 08:00:00'),
(41, 1, 50, 355, 3, '2024-09-01 08:00:00'),
(41, 2, -30, 325, 5, '2024-09-05 10:00:00'),
(41, 1, 50, 375, 3, '2024-09-10 08:00:00'),
(41, 1, 20, 395, 6, '2024-09-15 16:00:00'),
(41, 1, 15, 410, 2, '2024-09-15 09:00:00'),

-- 学生42（总分75）
(42, 1, 10, 10, 1, '2024-03-02 08:00:00'),
(42, 1, 10, 20, 2, '2024-03-02 09:00:00'),
(42, 1, 20, 40, 3, '2024-03-03 10:00:00'),
(42, 1, 5, 45, 4, '2024-06-01 19:35:00'),
(42, 1, 10, 55, 1, '2024-03-03 09:00:00'),
(42, 1, 10, 65, 2, '2024-03-03 10:00:00'),
(42, 1, 10, 75, 3, '2024-03-10 08:00:00'),

-- 学生43（总分500）
(43, 1, 10, 10, 1, '2024-03-22 08:00:00'),
(43, 1, 10, 20, 2, '2024-03-22 09:00:00'),
(43, 1, 20, 40, 3, '2024-03-23 10:00:00'),
(43, 1, 5, 45, 4, '2024-06-03 16:35:00'),
(43, 1, 10, 55, 1, '2024-03-23 11:00:00'),
(43, 1, 10, 65, 2, '2024-03-24 08:00:00'),
(43, 1, 20, 85, 3, '2024-03-25 08:00:00'),
(43, 1, 20, 105, 6, '2024-03-28 16:00:00'),
(43, 1, 30, 135, 3, '2024-04-01 08:00:00'),
(43, 2, -30, 105, 5, '2024-04-05 10:00:00'),
(43, 1, 50, 155, 3, '2024-05-01 08:00:00'),
(43, 1, 30, 185, 3, '2024-06-01 08:00:00'),
(43, 2, -40, 145, 5, '2024-06-05 10:00:00'),
(43, 1, 50, 195, 3, '2024-07-01 08:00:00'),
(43, 1, 50, 245, 3, '2024-07-20 08:00:00'),
(43, 2, -30, 215, 5, '2024-07-22 10:00:00'),
(43, 1, 50, 265, 3, '2024-08-01 08:00:00'),
(43, 1, 50, 315, 3, '2024-08-15 08:00:00'),
(43, 2, -40, 275, 5, '2024-08-18 10:00:00'),
(43, 1, 50, 325, 3, '2024-08-25 08:00:00'),
(43, 1, 50, 375, 3, '2024-09-01 08:00:00'),
(43, 2, -30, 345, 5, '2024-09-05 10:00:00'),
(43, 1, 50, 395, 3, '2024-09-10 08:00:00'),
(43, 1, 50, 445, 3, '2024-09-15 08:00:00'),
(43, 2, -40, 405, 5, '2024-09-18 10:00:00'),
(43, 1, 50, 455, 3, '2024-09-20 08:00:00'),
(43, 1, 30, 485, 3, '2024-09-25 08:00:00'),
(43, 1, 15, 500, 2, '2024-09-25 09:00:00'),

-- 学生44（总分120）
(44, 1, 10, 10, 1, '2024-03-22 08:00:00'),
(44, 1, 10, 20, 2, '2024-03-22 09:00:00'),
(44, 1, 20, 40, 3, '2024-03-23 10:00:00'),
(44, 1, 5, 45, 4, '2024-06-01 20:05:00'),
(44, 1, 10, 55, 1, '2024-03-24 08:00:00'),
(44, 1, 10, 65, 2, '2024-03-24 09:00:00'),
(44, 1, 20, 85, 3, '2024-03-25 08:00:00'),
(44, 2, -30, 55, 5, '2024-03-28 10:00:00'),
(44, 1, 20, 75, 6, '2024-03-28 16:00:00'),
(44, 1, 30, 105, 3, '2024-04-01 08:00:00'),
(44, 1, 15, 120, 2, '2024-04-05 09:00:00'),

-- 学生45（总分620）
(45, 1, 10, 10, 1, '2024-03-16 08:00:00'),
(45, 1, 10, 20, 2, '2024-03-16 09:00:00'),
(45, 1, 20, 40, 3, '2024-03-17 10:00:00'),
(45, 1, 5, 45, 4, '2024-06-02 09:35:00'),
(45, 1, 10, 55, 1, '2024-03-17 11:00:00'),
(45, 1, 10, 65, 2, '2024-03-18 08:00:00'),
(45, 1, 20, 85, 3, '2024-03-20 08:00:00'),
(45, 1, 20, 105, 6, '2024-03-22 16:00:00'),
(45, 1, 30, 135, 3, '2024-04-01 08:00:00'),
(45, 2, -30, 105, 5, '2024-04-05 10:00:00'),
(45, 1, 30, 135, 3, '2024-05-01 08:00:00'),
(45, 1, 30, 165, 3, '2024-06-01 08:00:00'),
(45, 2, -40, 125, 5, '2024-06-05 10:00:00'),
(45, 1, 50, 175, 3, '2024-07-01 08:00:00'),
(45, 1, 50, 225, 3, '2024-07-20 08:00:00'),
(45, 2, -30, 195, 5, '2024-07-22 10:00:00'),
(45, 1, 50, 245, 3, '2024-08-01 08:00:00'),
(45, 1, 50, 295, 3, '2024-08-15 08:00:00'),
(45, 2, -40, 255, 5, '2024-08-18 10:00:00'),
(45, 1, 50, 305, 3, '2024-08-25 08:00:00'),
(45, 1, 50, 355, 3, '2024-09-01 08:00:00'),
(45, 2, -30, 325, 5, '2024-09-05 10:00:00'),
(45, 1, 50, 375, 3, '2024-09-10 08:00:00'),
(45, 1, 50, 425, 3, '2024-09-15 08:00:00'),
(45, 2, -40, 385, 5, '2024-09-18 10:00:00'),
(45, 1, 50, 435, 3, '2024-09-20 08:00:00'),
(45, 1, 50, 485, 3, '2024-09-25 08:00:00'),
(45, 2, -30, 455, 5, '2024-09-28 10:00:00'),
(45, 1, 50, 505, 3, '2024-10-01 08:00:00'),
(45, 1, 50, 555, 3, '2024-10-05 08:00:00'),
(45, 2, -40, 515, 5, '2024-10-08 10:00:00'),
(45, 1, 50, 565, 3, '2024-10-10 08:00:00'),
(45, 1, 50, 615, 3, '2024-10-12 08:00:00'),
(45, 1, 5, 620, 4, '2024-10-12 09:30:00'),

-- 学生46（总分30）
(46, 1, 10, 10, 1, '2024-03-05 08:00:00'),
(46, 1, 10, 20, 2, '2024-03-05 09:00:00'),
(46, 1, 10, 30, 3, '2024-03-10 08:00:00'),

-- 学生47（总分360）
(47, 1, 10, 10, 1, '2024-02-12 08:00:00'),
(47, 1, 10, 20, 2, '2024-02-12 09:00:00'),
(47, 1, 20, 40, 3, '2024-02-13 10:00:00'),
(47, 1, 5, 45, 4, '2024-06-01 09:35:00'),
(47, 1, 10, 55, 1, '2024-02-14 08:00:00'),
(47, 1, 10, 65, 2, '2024-02-14 09:00:00'),
(47, 1, 20, 85, 3, '2024-02-18 08:00:00'),
(47, 1, 20, 105, 6, '2024-02-20 16:00:00'),
(47, 1, 30, 135, 3, '2024-03-01 08:00:00'),
(47, 2, -30, 105, 5, '2024-03-05 10:00:00'),
(47, 1, 30, 135, 3, '2024-04-01 08:00:00'),
(47, 1, 30, 165, 3, '2024-05-01 08:00:00'),
(47, 2, -40, 125, 5, '2024-05-05 10:00:00'),
(47, 1, 50, 175, 3, '2024-06-01 08:00:00'),
(47, 1, 50, 225, 3, '2024-07-01 08:00:00'),
(47, 2, -30, 195, 5, '2024-07-05 10:00:00'),
(47, 1, 50, 245, 3, '2024-07-15 08:00:00'),
(47, 1, 50, 295, 3, '2024-08-01 08:00:00'),
(47, 2, -40, 255, 5, '2024-08-05 10:00:00'),
(47, 1, 50, 305, 3, '2024-08-15 08:00:00'),
(47, 1, 50, 355, 3, '2024-08-25 08:00:00'),
(47, 1, 5, 360, 4, '2024-08-25 09:30:00'),

-- 学生48（总分170）
(48, 1, 10, 10, 1, '2024-03-22 08:00:00'),
(48, 1, 10, 20, 2, '2024-03-22 09:00:00'),
(48, 1, 20, 40, 3, '2024-03-23 10:00:00'),
(48, 1, 5, 45, 4, '2024-06-02 19:45:00'),
(48, 1, 10, 55, 1, '2024-03-24 08:00:00'),
(48, 1, 10, 65, 2, '2024-03-24 09:00:00'),
(48, 1, 20, 85, 3, '2024-03-25 08:00:00'),
(48, 1, 20, 105, 6, '2024-03-28 16:00:00'),
(48, 1, 30, 135, 3, '2024-04-01 08:00:00'),
(48, 2, -30, 105, 5, '2024-04-05 10:00:00'),
(48, 1, 30, 135, 3, '2024-05-01 08:00:00'),
(48, 1, 20, 155, 6, '2024-05-05 16:00:00'),
(48, 1, 15, 170, 2, '2024-05-10 09:00:00'),

-- 学生49（总分440）
(49, 1, 10, 10, 1, '2024-02-18 08:00:00'),
(49, 1, 10, 20, 2, '2024-02-18 09:00:00'),
(49, 1, 20, 40, 3, '2024-02-19 10:00:00'),
(49, 1, 5, 45, 4, '2024-06-03 07:35:00'),
(49, 1, 10, 55, 1, '2024-02-19 11:00:00'),
(49, 1, 10, 65, 2, '2024-02-20 08:00:00'),
(49, 1, 20, 85, 3, '2024-02-22 08:00:00'),
(49, 1, 20, 105, 6, '2024-02-25 16:00:00'),
(49, 1, 30, 135, 3, '2024-03-01 08:00:00'),
(49, 2, -30, 105, 5, '2024-03-05 10:00:00'),
(49, 1, 30, 135, 3, '2024-04-01 08:00:00'),
(49, 1, 30, 165, 3, '2024-05-01 08:00:00'),
(49, 2, -40, 125, 5, '2024-05-05 10:00:00'),
(49, 1, 50, 175, 3, '2024-06-01 08:00:00'),
(49, 1, 50, 225, 3, '2024-07-01 08:00:00'),
(49, 2, -30, 195, 5, '2024-07-05 10:00:00'),
(49, 1, 50, 245, 3, '2024-07-15 08:00:00'),
(49, 1, 50, 295, 3, '2024-08-01 08:00:00'),
(49, 2, -40, 255, 5, '2024-08-05 10:00:00'),
(49, 1, 50, 305, 3, '2024-08-15 08:00:00'),
(49, 1, 50, 355, 3, '2024-08-25 08:00:00'),
(49, 2, -30, 325, 5, '2024-08-28 10:00:00'),
(49, 1, 50, 375, 3, '2024-09-01 08:00:00'),
(49, 1, 50, 425, 3, '2024-09-05 08:00:00'),
(49, 1, 15, 440, 2, '2024-09-05 09:00:00'),

-- 学生50（总分85）
(50, 1, 10, 10, 1, '2024-03-22 08:00:00'),
(50, 1, 10, 20, 2, '2024-03-22 09:00:00'),
(50, 1, 20, 40, 3, '2024-03-23 10:00:00'),
(50, 1, 5, 45, 4, '2024-06-01 16:05:00'),
(50, 1, 10, 55, 1, '2024-03-24 08:00:00'),
(50, 1, 10, 65, 2, '2024-03-24 09:00:00'),
(50, 1, 10, 75, 3, '2024-03-25 08:00:00'),
(50, 1, 10, 85, 3, '2024-04-01 08:00:00');


-- =============================================
-- 插入花卉品种配置数据
-- =============================================
INSERT INTO `seed` (`variety`, `description`, `image`, `stage0_image`, `stage1_image`, `stage2_image`, `stage3_image`, `price`, `sunlight_max`, `water_max`, `nutrient_max`, `is_deleted`, `created_at`) VALUES
                                                                                                                                                                                                             ('玫瑰', '象征着浪漫与爱情，是花卉中最受欢迎的品种之一', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/rose.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/rose_seed.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/rose_sprout.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/rose_leaf.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/rose_bloom.jpg', 100, 100, 100, 100, 0, '2024-01-01 08:00:00'),
                                                                                                                                                                                                             ('向日葵', '永远追逐阳光，象征着希望与忠诚', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/sunflower.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/sunflower_seed.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/sunflower_sprout.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/sunflower_leaf.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/sunflower_bloom.jpg', 80, 120, 80, 80, 0, '2024-01-01 08:00:00'),
                                                                                                                                                                                                             ('郁金香', '优雅与高贵的象征，荷兰的国花', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/tulip.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/tulip_seed.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/tulip_sprout.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/tulip_leaf.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/tulip_bloom.jpg', 120, 90, 90, 90, 0, '2024-01-01 08:00:00'),
                                                                                                                                                                                                             ('百合', '纯洁与美好的象征，常被用于表达祝福', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/lily.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/lily_seed.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/lily_sprout.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/lily_leaf.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/lily_bloom.jpg', 150, 100, 110, 100, 0, '2024-01-01 08:00:00'),
                                                                                                                                                                                                             ('樱花', '春日浪漫的象征，花期虽短但绚烂至极', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/cherry.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/cherry_seed.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/cherry_sprout.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/cherry_leaf.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/cherry_bloom.jpg', 200, 110, 90, 90, 0, '2024-01-01 08:00:00'),
                                                                                                                                                                                                             ('兰花', '君子之花，象征着高洁与优雅', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/orchid.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/orchid_seed.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/orchid_sprout.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/orchid_leaf.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/orchid_bloom.jpg', 250, 80, 120, 90, 0, '2024-01-01 08:00:00'),
                                                                                                                                                                                                             ('牡丹', '花中之王，象征着富贵与繁荣', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/peony.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/peony_seed.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/peony_sprout.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/peony_leaf.jpg', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/flower/peony_bloom.jpg', 300, 100, 100, 100, 0, '2024-01-01 08:00:00');


-- =============================================
-- 插入花卉实例数据（每个学生一朵花）
-- =============================================
INSERT INTO `flower` (`user_id`, `seed_id`, `sunlight`, `water`, `nutrient`, `growth_value`, `stage`, `is_unlocked`, `created_at`, `updated_at`) VALUES
                                                                                                                                                     (11, 1, 85, 90, 75, 83, 2, 0, '2024-02-01 10:00:00', '2024-06-25 20:00:00'),
                                                                                                                                                     (12, 2, 60, 55, 70, 62, 1, 0, '2024-02-02 10:00:00', '2024-06-20 15:00:00'),
                                                                                                                                                     (13, 3, 95, 85, 90, 90, 2, 0, '2024-02-03 10:00:00', '2024-06-28 18:00:00'),
                                                                                                                                                     (14, 1, 40, 35, 30, 35, 0, 0, '2024-02-04 10:00:00', '2024-06-15 12:00:00'),
                                                                                                                                                     (15, 4, 100, 95, 90, 95, 2, 0, '2024-02-05 10:00:00', '2024-06-30 20:00:00'),
                                                                                                                                                     (16, 2, 50, 45, 55, 50, 1, 0, '2024-02-06 10:00:00', '2024-06-10 14:00:00'),
                                                                                                                                                     (17, 5, 100, 100, 100, 100, 3, 1, '2024-02-07 10:00:00', '2024-07-01 08:00:00'),
                                                                                                                                                     (18, 3, 30, 25, 20, 25, 0, 0, '2024-02-08 10:00:00', '2024-06-05 09:00:00'),
                                                                                                                                                     (19, 6, 75, 80, 70, 75, 2, 0, '2024-02-09 10:00:00', '2024-06-22 16:00:00'),
                                                                                                                                                     (20, 7, 90, 85, 80, 85, 2, 0, '2024-02-10 10:00:00', '2024-06-25 19:00:00'),
                                                                                                                                                     (21, 1, 70, 65, 60, 65, 1, 0, '2024-02-11 10:00:00', '2024-06-18 11:00:00'),
                                                                                                                                                     (22, 2, 100, 95, 100, 98, 3, 0, '2024-02-12 10:00:00', '2024-06-29 17:00:00'),
                                                                                                                                                     (23, 3, 55, 50, 45, 50, 1, 0, '2024-02-13 10:00:00', '2024-06-12 13:00:00'),
                                                                                                                                                     (24, 4, 35, 30, 40, 35, 0, 0, '2024-02-14 10:00:00', '2024-06-08 10:00:00'),
                                                                                                                                                     (25, 5, 80, 75, 70, 75, 2, 0, '2024-02-15 10:00:00', '2024-06-24 15:00:00'),
                                                                                                                                                     (26, 6, 45, 40, 50, 45, 1, 0, '2024-02-16 10:00:00', '2024-06-14 12:00:00'),
                                                                                                                                                     (27, 7, 65, 60, 55, 60, 1, 0, '2024-02-17 10:00:00', '2024-06-19 14:00:00'),
                                                                                                                                                     (28, 1, 25, 30, 20, 25, 0, 0, '2024-02-18 10:00:00', '2024-06-02 08:00:00'),
                                                                                                                                                     (29, 2, 85, 80, 75, 80, 2, 0, '2024-02-19 10:00:00', '2024-06-26 18:00:00'),
                                                                                                                                                     (30, 3, 50, 45, 55, 50, 1, 0, '2024-02-20 10:00:00', '2024-06-16 10:00:00'),
                                                                                                                                                     (31, 4, 70, 65, 60, 65, 1, 0, '2024-02-21 10:00:00', '2024-06-21 17:00:00'),
                                                                                                                                                     (32, 5, 40, 35, 30, 35, 0, 0, '2024-02-22 10:00:00', '2024-06-06 09:00:00'),
                                                                                                                                                     (33, 6, 60, 55, 65, 60, 1, 0, '2024-02-23 10:00:00', '2024-06-17 14:00:00'),
                                                                                                                                                     (34, 7, 20, 25, 15, 20, 0, 0, '2024-02-24 10:00:00', '2024-06-01 08:00:00'),
                                                                                                                                                     (35, 1, 90, 85, 80, 85, 2, 0, '2024-02-25 10:00:00', '2024-06-27 19:00:00'),
                                                                                                                                                     (36, 2, 35, 30, 25, 30, 0, 0, '2024-02-26 10:00:00', '2024-06-04 10:00:00'),
                                                                                                                                                     (37, 3, 95, 90, 95, 93, 2, 0, '2024-02-27 10:00:00', '2024-06-30 20:00:00'),
                                                                                                                                                     (38, 4, 45, 40, 35, 40, 1, 0, '2024-02-28 10:00:00', '2024-06-09 11:00:00'),
                                                                                                                                                     (39, 5, 75, 70, 65, 70, 1, 0, '2024-03-01 10:00:00', '2024-06-23 16:00:00'),
                                                                                                                                                     (40, 6, 55, 50, 45, 50, 1, 0, '2024-03-02 10:00:00', '2024-06-13 12:00:00'),
                                                                                                                                                     (41, 7, 80, 75, 85, 80, 2, 0, '2024-03-03 10:00:00', '2024-06-28 18:00:00'),
                                                                                                                                                     (42, 1, 30, 25, 20, 25, 0, 0, '2024-03-04 10:00:00', '2024-06-03 09:00:00'),
                                                                                                                                                     (43, 2, 70, 65, 60, 65, 1, 0, '2024-03-05 10:00:00', '2024-06-20 15:00:00'),
                                                                                                                                                     (44, 3, 40, 35, 45, 40, 1, 0, '2024-03-06 10:00:00', '2024-06-11 14:00:00'),
                                                                                                                                                     (45, 4, 85, 80, 75, 80, 2, 0, '2024-03-07 10:00:00', '2024-06-26 17:00:00'),
                                                                                                                                                     (46, 5, 25, 20, 15, 20, 0, 0, '2024-03-08 10:00:00', '2024-06-02 08:00:00'),
                                                                                                                                                     (47, 6, 65, 60, 55, 60, 1, 0, '2024-03-09 10:00:00', '2024-06-19 13:00:00'),
                                                                                                                                                     (48, 7, 50, 45, 40, 45, 1, 0, '2024-03-10 10:00:00', '2024-06-15 10:00:00'),
                                                                                                                                                     (49, 1, 75, 70, 65, 70, 1, 0, '2024-03-11 10:00:00', '2024-06-22 16:00:00'),
                                                                                                                                                     (50, 2, 35, 30, 25, 30, 0, 0, '2024-03-12 10:00:00', '2024-06-05 09:00:00');


-- =============================================
-- 插入商店道具数据
-- =============================================
INSERT INTO `shop_item` (`item_name`, `icon`, `price`, `attribute_type`, `boost_value`, `created_at`) VALUES
                                                                                                          ('阳光瓶', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/shop/sunlight_bottle.png', 10, 1, 10, '2024-01-01 08:00:00'),
                                                                                                          ('大阳光瓶', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/shop/sunlight_bottle_large.png', 25, 1, 30, '2024-01-01 08:00:00'),
                                                                                                          ('水壶', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/shop/water_can.png', 10, 2, 10, '2024-01-01 08:00:00'),
                                                                                                          ('大水壶', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/shop/water_can_large.png', 25, 2, 30, '2024-01-01 08:00:00'),
                                                                                                          ('肥料袋', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/shop/fertilizer_bag.png', 10, 3, 10, '2024-01-01 08:00:00'),
                                                                                                          ('大肥料袋', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/shop/fertilizer_bag_large.png', 25, 3, 30, '2024-01-01 08:00:00'),
                                                                                                          ('奇迹药水', 'https://qingyun.oss-cn-hangzhou.aliyuncs.com/shop/miracle_potion.png', 50, 1, 50, '2024-01-01 08:00:00');


-- =============================================
-- 插入系统通知数据
-- =============================================
INSERT INTO `notice` (`user_id`, `notice_title`, `notice_content`, `notice_status`, `notice_type`, `push_time`) VALUES
                                                                                                                    (11, '课程即将开始', '您已选课程【初中数学·函数专题】第一讲即将在10分钟后开始', 1, 1, '2024-01-10 18:50:00'),
                                                                                                                    (11, '作业已发布', '【一次函数基础练习】已发布，截止时间为2024-01-20 23:59:59', 1, 2, '2024-01-12 08:00:00'),
                                                                                                                    (11, '作业批改完成', '您的作业【一次函数基础练习】已批改，得分：38/40', 1, 4, '2024-01-19 20:00:00'),
                                                                                                                    (11, '课程即将开始', '您已选课程【初中物理·力学基础】第一讲即将在10分钟后开始', 1, 1, '2024-01-15 18:50:00'),
                                                                                                                    (11, '课堂已开始', '【初中数学·函数专题】的课堂「第二讲·正比例函数与一次函数」已开始', 1, 1, '2024-01-17 19:00:00'),
                                                                                                                    (12, '作业已发布', '【时态与语态专项练习】已发布，截止时间为2024-01-28 23:59:59', 1, 2, '2024-01-20 08:00:00'),
                                                                                                                    (12, '作业批改完成', '您的作业【时态与语态专项练习】已批改，得分：36/40', 0, 4, '2024-01-26 20:30:00'),
                                                                                                                    (12, '课程即将开始', '您已选课程【初中英语·语法精讲】第二讲即将在10分钟后开始', 1, 1, '2024-01-27 18:50:00'),
                                                                                                                    (13, '作业已发布', '【力学基础单元测试】已发布，截止时间为2024-01-30 23:59:59', 1, 2, '2024-01-20 08:00:00'),
                                                                                                                    (13, '课程即将开始', '您已选课程【高中物理·电磁学】第一讲即将在10分钟后开始', 0, 1, '2024-02-20 08:50:00'),
                                                                                                                    (14, '作业批改完成', '您的作业【一次函数基础练习】已批改，得分：10/40', 1, 4, '2024-01-20 10:30:00'),
                                                                                                                    (14, '考试已发布', '【二次函数单元测试】已发布，截止时间为2024-02-10 23:59:59', 1, 3, '2024-02-01 08:00:00'),
                                                                                                                    (15, '作业批改完成', '您的作业【一次函数基础练习】已批改，得分：40/40', 1, 4, '2024-01-20 11:00:00'),
                                                                                                                    (15, '课程即将开始', '您已选课程【高中语文·作文写作】第一讲即将在10分钟后开始', 0, 1, '2024-03-16 08:50:00'),
                                                                                                                    (16, '作业已发布', '【力学基础单元测试】已发布，截止时间为2024-01-30 23:59:59', 0, 2, '2024-01-20 08:00:00'),
                                                                                                                    (17, '作业批改完成', '您的考试【二次函数单元测试】已批改，得分：40/40', 1, 4, '2024-02-09 20:00:00'),
                                                                                                                    (17, '课堂已开始', '【初中生物·遗传与进化】的课堂「第一讲·基因与DNA」已开始', 1, 1, '2024-06-25 20:00:00'),
                                                                                                                    (18, '作业已发布', '【一次函数基础练习】已发布，截止时间为2024-01-20 23:59:59', 1, 2, '2024-01-12 08:00:00'),
                                                                                                                    (19, '课程即将开始', '您已选课程【初中物理·力学基础】第四讲即将在10分钟后开始', 1, 1, '2024-02-12 15:50:00'),
                                                                                                                    (20, '作业批改完成', '您的作业【一次函数基础练习】已批改，得分：40/40', 1, 4, '2024-01-20 11:00:00'),
                                                                                                                    (20, '作业已发布', '【从句综合测试】已发布，截止时间为2024-02-18 23:59:59', 1, 2, '2024-02-10 08:00:00'),
                                                                                                                    (21, '作业已发布', '【元素与化合物-第一单元练习】已发布，截止时间为2024-03-20 23:59:59', 1, 2, '2024-03-10 08:00:00'),
                                                                                                                    (22, '课程即将开始', '您已选课程【初中数学·函数专题】第四讲即将在10分钟后开始', 0, 1, '2024-01-31 17:50:00'),
                                                                                                                    (23, '课堂已开始', '【初中生物·遗传与进化】的课堂「第一讲·基因与DNA」已开始', 1, 1, '2024-06-25 20:00:00'),
                                                                                                                    (24, '作业已发布', '【元素与化合物-第一单元练习】已发布，截止时间为2024-03-20 23:59:59', 0, 2, '2024-03-10 08:00:00'),
                                                                                                                    (25, '课堂已开始', '【中国古代史】的课堂「第一讲·夏商周」已开始', 1, 1, '2024-06-28 10:00:00'),
                                                                                                                    (26, '作业已发布', '【Python-变量与数据类型练习】已发布，截止时间为2024-07-15 23:59:59', 0, 2, '2024-07-03 08:00:00'),
                                                                                                                    (27, '课程即将开始', '您已选课程【初中地理·自然地理】第一讲即将在10分钟后开始', 1, 1, '2024-03-02 08:50:00'),
                                                                                                                    (28, '作业已发布', '【时态与语态专项练习】已发布，截止时间为2024-01-28 23:59:59', 1, 2, '2024-01-20 08:00:00'),
                                                                                                                    (29, '作业批改完成', '您的作业【力学基础单元测试】已批改，得分：40/40', 1, 4, '2024-01-30 09:30:00'),
                                                                                                                    (30, '课程即将开始', '您已选课程【高中物理·电磁学】第一讲即将在10分钟后开始', 0, 1, '2024-02-20 08:50:00'),
                                                                                                                    (31, '作业已发布', '【有机化学-第一单元练习】已发布，截止时间为2024-06-30 23:59:59', 1, 2, '2024-06-20 08:00:00'),
                                                                                                                    (32, '课程即将开始', '您已选课程【初中物理·力学基础】第五讲即将在10分钟后开始', 1, 1, '2024-02-19 15:50:00'),
                                                                                                                    (33, '课堂已开始', '【初中生物·遗传与进化】的课堂「第一讲·基因与DNA」已开始', 1, 1, '2024-06-25 20:00:00'),
                                                                                                                    (34, '系统警告', '您的账号已被封禁，封禁原因：恶意刷屏攻击他人', 0, 0, '2024-06-15 10:00:00'),
                                                                                                                    (35, '课程即将开始', '您已选课程【初中物理·力学基础】第二讲即将在10分钟后开始', 1, 1, '2024-01-22 15:50:00'),
                                                                                                                    (36, '作业已发布', '【元素与化合物-第一单元练习】已发布，截止时间为2024-03-20 23:59:59', 0, 2, '2024-03-10 08:00:00'),
                                                                                                                    (37, '作业批改完成', '您的考试【二次函数单元测试】已批改，得分：40/40', 1, 4, '2024-02-09 20:00:00'),
                                                                                                                    (37, '课堂已开始', '【Python编程入门】的课堂「第一讲·变量与数据类型」已开始', 0, 1, '2024-07-10 19:00:00'),
                                                                                                                    (39, '课程即将开始', '您已选课程【初中地理·自然地理】第一讲即将在10分钟后开始', 0, 1, '2024-03-02 08:50:00'),
                                                                                                                    (40, '课堂已开始', '【中国古代史】的课堂「第一讲·夏商周」已开始', 1, 1, '2024-06-28 10:00:00'),
                                                                                                                    (41, '课程审核结果', '您创建的课程【高中政治·哲学基础】审核结果为：驳回，原因：课程内容部分观点有争议', 1, 0, '2024-03-10 16:00:00'),
                                                                                                                    (43, '作业已发布', '【Python-变量与数据类型练习】已发布，截止时间为2024-07-15 23:59:59', 0, 2, '2024-07-03 08:00:00'),
                                                                                                                    (44, '课程即将开始', '您已选课程【Python编程入门】第一讲即将在10分钟后开始', 0, 1, '2024-07-10 18:50:00'),
                                                                                                                    (45, '作业批改完成', '您的作业【力学基础单元测试】已批改，得分：40/40', 1, 4, '2024-01-30 09:30:00'),
                                                                                                                    (47, '课堂已开始', '【初中生物·遗传与进化】的课堂「第一讲·基因与DNA」已开始', 0, 1, '2024-06-25 20:00:00'),
                                                                                                                    (48, '课程即将开始', '您已选课程【Python编程入门】第一讲即将在10分钟后开始', 0, 1, '2024-07-10 18:50:00'),
                                                                                                                    (49, '课堂已开始', '【中国古代史】的课堂「第一讲·夏商周」已开始', 1, 1, '2024-06-28 10:00:00'),
                                                                                                                    (50, '作业已发布', '【Python-变量与数据类型练习】已发布，截止时间为2024-07-15 23:59:59', 0, 2, '2024-07-03 08:00:00');


-- =============================================
-- 插入学情分析数据
-- =============================================
INSERT INTO `student_analysis` (`user_id`, `total_study_duration`, `assignment_correct_rate`, `week_study_duration`, `updated_at`) VALUES
                                                                                                                                       (11, 48600, 95.50, 3600, '2024-06-30 23:59:59'),
                                                                                                                                       (12, 32400, 85.00, 2700, '2024-06-30 23:59:59'),
                                                                                                                                       (13, 43200, 90.00, 5400, '2024-06-30 23:59:59'),
                                                                                                                                       (14, 21600, 65.00, 1800, '2024-06-30 23:59:59'),
                                                                                                                                       (15, 54000, 92.50, 5400, '2024-06-30 23:59:59'),
                                                                                                                                       (16, 16200, 70.00, 1800, '2024-06-30 23:59:59'),
                                                                                                                                       (17, 70200, 98.00, 7200, '2024-06-30 23:59:59'),
                                                                                                                                       (18, 10800, 55.00, 900, '2024-06-30 23:59:59'),
                                                                                                                                       (19, 37800, 82.50, 3600, '2024-06-30 23:59:59'),
                                                                                                                                       (20, 48600, 88.00, 5400, '2024-06-30 23:59:59'),
                                                                                                                                       (21, 27000, 75.00, 2700, '2024-06-30 23:59:59'),
                                                                                                                                       (22, 54000, 92.00, 5400, '2024-06-30 23:59:59'),
                                                                                                                                       (23, 21600, 72.00, 1800, '2024-06-30 23:59:59'),
                                                                                                                                       (24, 10800, 50.00, 900, '2024-06-30 23:59:59'),
                                                                                                                                       (25, 37800, 78.00, 3600, '2024-06-30 23:59:59'),
                                                                                                                                       (26, 16200, 60.00, 1800, '2024-06-30 23:59:59'),
                                                                                                                                       (27, 27000, 72.00, 2700, '2024-06-30 23:59:59'),
                                                                                                                                       (28, 10800, 55.00, 900, '2024-06-30 23:59:59'),
                                                                                                                                       (29, 43200, 85.00, 5400, '2024-06-30 23:59:59'),
                                                                                                                                       (30, 16200, 62.00, 1800, '2024-06-30 23:59:59'),
                                                                                                                                       (31, 27000, 70.00, 2700, '2024-06-30 23:59:59'),
                                                                                                                                       (32, 21600, 68.00, 1800, '2024-06-30 23:59:59'),
                                                                                                                                       (33, 32400, 76.00, 3600, '2024-06-30 23:59:59'),
                                                                                                                                       (34, 5400, 30.00, 0, '2024-06-30 23:59:59'),
                                                                                                                                       (35, 43200, 82.00, 5400, '2024-06-30 23:59:59'),
                                                                                                                                       (36, 10800, 55.00, 900, '2024-06-30 23:59:59'),
                                                                                                                                       (37, 64800, 95.00, 7200, '2024-06-30 23:59:59'),
                                                                                                                                       (38, 10800, 52.00, 900, '2024-06-30 23:59:59'),
                                                                                                                                       (39, 27000, 72.00, 2700, '2024-06-30 23:59:59'),
                                                                                                                                       (40, 16200, 60.00, 1800, '2024-06-30 23:59:59'),
                                                                                                                                       (41, 32400, 78.00, 3600, '2024-06-30 23:59:59'),
                                                                                                                                       (42, 10800, 50.00, 900, '2024-06-30 23:59:59'),
                                                                                                                                       (43, 37800, 80.00, 5400, '2024-06-30 23:59:59'),
                                                                                                                                       (44, 16200, 62.00, 1800, '2024-06-30 23:59:59'),
                                                                                                                                       (45, 43200, 85.00, 5400, '2024-06-30 23:59:59'),
                                                                                                                                       (46, 5400, 40.00, 0, '2024-06-30 23:59:59'),
                                                                                                                                       (47, 32400, 76.00, 3600, '2024-06-30 23:59:59'),
                                                                                                                                       (48, 16200, 58.00, 1800, '2024-06-30 23:59:59'),
                                                                                                                                       (49, 32400, 74.00, 3600, '2024-06-30 23:59:59'),
                                                                                                                                       (50, 10800, 52.00, 900, '2024-06-30 23:59:59');


-- =============================================
-- 插入敏感词数据
-- =============================================
INSERT INTO `sensitive_word` (`word`, `created_at`) VALUES
                                                        ('色情', '2024-01-01 08:00:00'),
                                                        ('暴力', '2024-01-01 08:00:00'),
                                                        ('赌博', '2024-01-01 08:00:00'),
                                                        ('毒品', '2024-01-01 08:00:00'),
                                                        ('政治敏感', '2024-01-01 08:00:00'),
                                                        ('诈骗', '2024-01-01 08:00:00'),
                                                        ('谣言', '2024-01-01 08:00:00'),
                                                        ('反动', '2024-01-01 08:00:00'),
                                                        ('恐怖主义', '2024-01-01 08:00:00'),
                                                        ('邪教', '2024-01-01 08:00:00');


-- =============================================
-- 插入课程问题数据
-- =============================================
INSERT INTO `course_problem` (`course_id`, `user_id`, `title`, `content`, `reply_count`, `created_at`, `updated_at`) VALUES
                                                                                                                         (1, 11, '关于函数定义域的疑问', '老师，定义域和值域有什么区别？我总是搞混这两个概念', 2, '2024-01-15 20:00:00', '2024-01-16 10:00:00'),
                                                                                                                         (1, 13, '一次函数的斜率怎么理解？', 'y=kx+b中的k到底代表什么？为什么叫斜率？', 2, '2024-01-18 19:30:00', '2024-01-19 09:00:00'),
                                                                                                                         (3, 12, '一般现在时和现在进行时的区别', '什么时候用一般现在时，什么时候用现在进行时？', 1, '2024-01-22 20:00:00', '2024-01-23 10:00:00'),
                                                                                                                         (5, 11, '牛顿第二定律的应用', 'F=ma在实际解题中怎么用？有没有什么技巧？', 1, '2024-01-20 19:00:00', '2024-01-21 08:00:00'),
                                                                                                                         (5, 16, '受力分析中如何确定力的方向？', '受力分析时总是搞不清楚力的方向，有什么好的方法吗？', 0, '2024-01-25 18:00:00', '2024-01-25 18:00:00'),
                                                                                                                         (9, 15, '文言文翻译中如何确定词义？', '文言文中的实词和虚词怎么区分？翻译时怎么确定具体词义？', 1, '2024-02-08 20:00:00', '2024-02-09 10:00:00'),
                                                                                                                         (7, 14, '化学方程式配平的技巧', '氧化还原反应的方程式配平总是出错，有什么技巧？', 0, '2024-03-10 19:00:00', '2024-03-10 19:00:00'),
                                                                                                                         (15, 37, 'Python中列表和元组的区别', 'list和tuple有什么区别？什么时候用列表，什么时候用元组？', 1, '2024-07-05 20:00:00', '2024-07-06 09:00:00');


-- =============================================
-- 插入课程问题回复数据
-- =============================================
INSERT INTO `course_problem_reply` (`problem_id`, `user_id`, `content`, `created_at`, `updated_at`) VALUES
                                                                                                        (1, 1, '定义域是x的取值范围，值域是y的取值范围。简单说，定义域是输入，值域是输出。', '2024-01-16 08:00:00', '2024-01-16 08:00:00'),
                                                                                                        (1, 11, '明白了，谢谢老师！', '2024-01-16 10:00:00', '2024-01-16 10:00:00'),
                                                                                                        (2, 1, 'k是斜率，表示直线与x轴正方向的夹角的正切值。k>0时直线向右上方倾斜，k<0时向右下方倾斜。', '2024-01-19 08:00:00', '2024-01-19 08:00:00'),
                                                                                                        (2, 13, '原来是这样，那我记住了！', '2024-01-19 09:00:00', '2024-01-19 09:00:00'),
                                                                                                        (3, 2, '一般现在时表示经常性动作或客观事实，现在进行时表示此刻正在进行的动作。', '2024-01-23 09:00:00', '2024-01-23 09:00:00'),
                                                                                                        (4, 3, '先确定研究对象，进行受力分析，然后沿加速度方向建立坐标系，代入F=ma求解。', '2024-01-21 08:00:00', '2024-01-21 08:00:00'),
                                                                                                        (6, 5, '推荐先掌握120个常见实词和20个虚词，然后结合上下文推断。可以多读经典篇章积累语感。', '2024-02-09 08:00:00', '2024-02-09 08:00:00'),
                                                                                                        (8, 10, '列表是可变的（可以增删改），元组是不可变的（创建后不能修改）。需要修改数据时用列表，需要保护数据不变时用元组。', '2024-07-06 09:00:00', '2024-07-06 09:00:00');


-- =============================================
-- 插入用户反馈数据
-- =============================================
INSERT INTO `feedback` (`user_id`, `content`, `reply_content`, `reply_time`, `status`, `created_at`, `updated_at`) VALUES
                                                                                                                       (11, '课程内容很好，但是视频播放有时候会卡顿，希望能优化一下服务器', '感谢您的反馈！我们已经升级了视频服务器，请再次尝试。', '2024-02-01 10:00:00', 1, '2024-01-25 20:00:00', '2024-02-01 10:00:00'),
                                                                                                                       (12, '希望增加更多英语阅读理解的练习，目前的题目有点少', '已收到建议，我们会在下个月增加阅读专项题库。', '2024-02-10 14:00:00', 1, '2024-02-05 19:00:00', '2024-02-10 14:00:00'),
                                                                                                                       (13, '物理课的实验视频非常棒，希望能更多一些', '感谢认可！我们会继续录制更多高质量实验视频。', '2024-02-20 09:00:00', 1, '2024-02-15 18:00:00', '2024-02-20 09:00:00'),
                                                                                                                       (15, '作文批改功能很实用，但评语可以再详细一些', '好的，我们会优化批改标准，提供更详细的改进建议。', NULL, 0, '2024-03-10 20:00:00', '2024-03-10 20:00:00'),
                                                                                                                       (17, '希望能增加数学竞赛相关的课程内容', '已列入规划，预计下个学期推出竞赛专题系列。', '2024-04-01 11:00:00', 1, '2024-03-25 19:00:00', '2024-04-01 11:00:00'),
                                                                                                                       (22, '花卉培育功能很有意思，但积分获取速度有点慢', '感谢建议！我们正在优化积分规则，增加更多获取积分的途径。', NULL, 0, '2024-06-20 18:00:00', '2024-06-20 18:00:00'),
                                                                                                                       (37, 'Python课程很好，希望增加更多实战项目', '已收到，正在准备第二期实战项目课程！', '2024-07-15 16:00:00', 1, '2024-07-10 20:00:00', '2024-07-15 16:00:00'),
                                                                                                                       (45, '作文素材库很丰富，对我的写作帮助很大', '感谢认可！我们会持续更新素材库。', '2024-06-01 10:00:00', 1, '2024-05-20 19:00:00', '2024-06-01 10:00:00');