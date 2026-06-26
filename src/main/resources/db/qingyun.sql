```sql
-- ============================================================
-- 1. users — 用户表
-- ============================================================
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    password VARCHAR(255) NOT NULL COMMENT '登录密码',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像URL',
    role TINYINT DEFAULT 1 COMMENT '角色：1-学生 2-教师 3-管理员',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常 2-禁用'
);


-- ============================================================
-- 2. courses — 课程表
-- ============================================================
CREATE TABLE courses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID（学生输入此ID加入课程）',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    title VARCHAR(200) NOT NULL COMMENT '课程名称',
    description TEXT COMMENT '课程描述',
    cover VARCHAR(255) COMMENT '封面图URL',
    category VARCHAR(50) COMMENT '课程分类',
    status TINYINT DEFAULT 1 COMMENT '状态：1-待审核 2-已上架 3-已下架',
    FOREIGN KEY (teacher_id) REFERENCES users(id)
);


-- ============================================================
-- 3. course_enrollments — 选课记录表
-- ============================================================
CREATE TABLE course_enrollments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    UNIQUE KEY uk_user_course (user_id, course_id)
);


-- ============================================================
-- 4. classes — 课堂表
-- ============================================================
CREATE TABLE classes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课堂ID',
    course_id BIGINT NOT NULL COMMENT '所属课程ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    title VARCHAR(200) NOT NULL COMMENT '课堂名称（老师每次命名）',
    file_url VARCHAR(500) COMMENT '课件附件地址',
    start_at DATETIME COMMENT '开始时间',
    end_at DATETIME COMMENT '结束时间',
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (teacher_id) REFERENCES users(id)
);


-- ============================================================
-- 5. class_check — 课堂签到表
-- ============================================================
CREATE TABLE class_check (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '签到记录ID',
    class_id BIGINT NOT NULL COMMENT '课堂ID',
    user_id BIGINT NOT NULL COMMENT '签到学生ID',
    checkin_at DATETIME COMMENT '签到时间',
    FOREIGN KEY (class_id) REFERENCES classes(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);


-- ============================================================
-- 6. class_votes — 课堂投票题目表
-- ============================================================
CREATE TABLE class_votes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '投票ID',
    class_id BIGINT NOT NULL COMMENT '课堂ID',
    heading VARCHAR(500) NOT NULL COMMENT '选择题题目',
    options JSON NOT NULL COMMENT '选项列表，如 ["A. xxx", "B. xxx"]',
    correct_option VARCHAR(10) COMMENT '正确答案（用于自动判分）',
    FOREIGN KEY (class_id) REFERENCES classes(id)
);


-- ============================================================
-- 7. vote_records — 投票记录表
-- ============================================================
CREATE TABLE vote_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    vote_id BIGINT NOT NULL COMMENT '投票ID',
    user_id BIGINT NOT NULL COMMENT '学生ID',
    selected_option VARCHAR(10) NOT NULL COMMENT '学生选的选项',
    is_correct TINYINT DEFAULT 0 COMMENT '是否正确：0-错误 1-正确',
    FOREIGN KEY (vote_id) REFERENCES class_votes(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);


-- ============================================================
-- 8. class_chat — 课堂聊天表
-- ============================================================
CREATE TABLE class_chat (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    class_id BIGINT NOT NULL COMMENT '课堂ID',
    user_id BIGINT NOT NULL COMMENT '发送人ID',
    message_type TINYINT DEFAULT 1 COMMENT '消息类型：1-文字 2-图片 3-表情',
    content TEXT NOT NULL COMMENT '消息内容',
    sent_at DATETIME COMMENT '发送时间',
    FOREIGN KEY (class_id) REFERENCES classes(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);


-- ============================================================
-- 9. course_reviews — 课程评价表
-- ============================================================
CREATE TABLE course_reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    user_id BIGINT NOT NULL COMMENT '评价人（学生ID）',
    rating TINYINT NOT NULL COMMENT '1-5星评分',
    comment TEXT COMMENT '文字评价',
    created_at DATETIME COMMENT '评价时间',
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    CHECK (rating BETWEEN 1 AND 5)
);


-- ============================================================
-- 10. assignments — 作业考试表
-- ============================================================
CREATE TABLE assignments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '作业/考试ID',
    course_id BIGINT NOT NULL COMMENT '所属课程ID',
    teacher_id BIGINT NOT NULL COMMENT '发布教师ID',
    title VARCHAR(200) NOT NULL COMMENT '作业/考试名称',
    type TINYINT DEFAULT 1 COMMENT '类型：1-作业 2-考试',
    content JSON NOT NULL COMMENT '题目内容，JSON数组存储',
    total_score INT NOT NULL COMMENT '满分分值',
    created_at DATETIME COMMENT '发布时间',
    deadline DATETIME COMMENT '截止时间',
    duration_min INT COMMENT '考试倒计时（分钟，仅考试用）',
    status TINYINT DEFAULT 1 COMMENT '状态：1-发布中 2-已截止',
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (teacher_id) REFERENCES users(id)
);


-- ============================================================
-- 11. submissions — 作业提交记录表
-- ============================================================
CREATE TABLE submissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '提交记录ID',
    assignment_id BIGINT NOT NULL COMMENT '作业/考试ID',
    user_id BIGINT NOT NULL COMMENT '学生ID',
    objective_score INT DEFAULT 0 COMMENT '客观题得分（系统自动批改）',
    subjective_score INT DEFAULT 0 COMMENT '主观题得分（老师批改后填入）',
    total_score INT DEFAULT 0 COMMENT '总分',
    comment JSON COMMENT '评语，JSON数据存储',
    status TINYINT DEFAULT 1 COMMENT '状态：1-待批改 2-已批改',
    submitted_at DATETIME COMMENT '提交时间',
    FOREIGN KEY (assignment_id) REFERENCES assignments(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);


-- ============================================================
-- 12. study_sessions — 自习室记录表
-- ============================================================
CREATE TABLE study_sessions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '自习记录ID',
    user_id BIGINT NOT NULL COMMENT '学生ID',
    mode TINYINT DEFAULT 1 COMMENT '模式：1-正向计时 2-倒计时',
    planned_duration INT COMMENT '计划时长（秒，倒计时用）',
    actual_duration INT DEFAULT 0 COMMENT '有效时长（秒）',
    distraction_count INT DEFAULT 0 COMMENT '切屏次数',
    is_valid TINYINT DEFAULT 1 COMMENT '是否有效：1-有效 2-无效（切屏超3次）',
    start_at DATETIME COMMENT '开始时间',
    end_at DATETIME COMMENT '结束时间',
    FOREIGN KEY (user_id) REFERENCES users(id)
);


-- ============================================================
-- 13. points_records — 积分流水表
-- ============================================================
CREATE TABLE points_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '积分记录ID',
    user_id BIGINT NOT NULL COMMENT '学生ID',
    change_points INT NOT NULL COMMENT '变动积分数（正数增加，负数消耗）',
    source VARCHAR(30) NOT NULL COMMENT '变动积分来源：签到/答题正确/作业/考试/自习/兑换',
    source_id BIGINT COMMENT '来源记录ID',
    created_at DATETIME COMMENT '流水时间',
    FOREIGN KEY (user_id) REFERENCES users(id)
);


-- ============================================================
-- 14. flower_gardens — 花卉培育记录表
-- ============================================================
CREATE TABLE flower_gardens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '培育记录ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '学生ID（每人仅一条记录）',
    flower_type VARCHAR(50) NOT NULL COMMENT '当前培育的花卉种类',
    growth_value INT DEFAULT 0 COMMENT '当前生长值（0~100）',
    stage TINYINT DEFAULT 1 COMMENT '阶段：1-种子 2-发芽 3-长叶 4-开花',
    is_bloomed TINYINT DEFAULT 0 COMMENT '是否开花：0-未开花 1-已开花',
    total_bloomed_count INT DEFAULT 0 COMMENT '累计已开花数量（排行榜依据）',
    FOREIGN KEY (user_id) REFERENCES users(id)
);


-- ============================================================
-- 15. notifications — 系统通知表
-- ============================================================
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    user_id BIGINT NOT NULL COMMENT '接收人ID',
    type VARCHAR(30) NOT NULL COMMENT '通知类型：course_join/homework_publish/homework_graded/exam_publish/exam_graded/class_start',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT NOT NULL COMMENT '通知内容',
    link VARCHAR(500) COMMENT '跳转链接',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读：0-未读 1-已读',
    created_at DATETIME COMMENT '通知时间',
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```