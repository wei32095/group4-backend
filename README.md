<div align="center">

# 🌱 青耘学堂 · QingYun

**教学管理平台 — 后端服务**

Spring Boot 3 + MyBatis-Plus 单体应用 · 学生/教师/管理员三端

</div>

---

## 目录

- [项目概览](#项目概览)
- [技术栈](#技术栈)
- [系统架构](#系统架构)
- [功能模块](#功能模块)
- [快速开始](#快速开始)
- [项目结构](#项目结构)
- [API 概览](#api-概览)
- [数据库](#数据库)
- [开发规范](#开发规范)
- [团队](#团队)

---

## 项目概览

青耘学堂是一个面向高校/培训机构的教学管理平台，提供课程管理、课堂互动、作业批改、学情分析、积分激励等完整闭环功能。

**三类用户角色：**

| 角色 | 能力 |
|------|------|
| 👨‍🎓 学生 | 加入课程、签到投票、提交作业、自习计时、积分种花 |
| 👩‍🏫 教师 | 创建课程/课堂、布置作业、批改打分、查看学情 |
| 🛡️ 管理员 | 审核课程、管理种子配置、封禁用户、维护敏感词 |

### 设计亮点

- **花卉激励系统**：学生通过学习获得积分，积分兑换道具培育虚拟花卉，将学习行为游戏化
- **自习室防作弊**：切屏检测 > 3 次自动标记无效自习，保证学习时长数据可信
- **敏感词实时过滤**：基于 DFA 算法的字典树，在聊天、评价、提问等入口实时拦截
- **微信小程序适配**：内置小程序登录 mock 模式，方便本地开发调试

---

## 技术栈

| 层 | 选型 |
|---|---|
| **框架** | Spring Boot 3.2.7 · Java 17 |
| **ORM** | MyBatis-Plus 3.5.16 |
| **数据库** | MySQL 8.x（utf8mb4） |
| **缓存** | Redis（验证码存储） |
| **认证** | JWT（Auth0 java-jwt 4.4.0） |
| **文件存储** | 阿里云 OSS |
| **构建工具** | Maven |
| **辅助库** | Lombok · spring-boot-starter-validation · spring-boot-devtools |

---

## 系统架构

### 三层架构

```
Controller（接收请求 · 校验权限 · 返回 ApiResult）
    ↕
Service Interface + ServiceImpl（业务逻辑 · @Transactional）
    ↕
Mapper（MyBatis-Plus BaseMapper · LambdaQueryWrapper）
```

- Controller 通过 `request.getAttribute("userId")` 获取当前用户，不依赖 Spring Security
- 角色校验在 Controller 层完成，管理员接口统一 `/qingyun/*/admin/**` 路径
- 无 Manager/DAO 层，Mapper 直接注入 ServiceImpl

### 认证流程

```
请求 → JWT 拦截器（除白名单外全部拦截）
         → 解析 Authorization: Bearer <token>
         → userId + role 写入 request attribute
         → 放行到 Controller
```

### 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {  },
  "timestamp": "2026-07-04T12:00:00Z"
}
```

业务异常通过 `BusinessException(code, message)` 抛出，由 `GlobalExceptionHandler` 统一捕获返回。

---

## 功能模块

### 🔐 用户认证
- 账号密码注册/登录
- 微信小程序登录（支持 mock 模式）
- 个人信息修改、密码修改
- 验证码发送（Redis 存储，用完即销毁）

### 📚 课程管理
- 教师创建课程 → 管理员审核 → 学生通过课程码加入
- 课程详情、学生/教师视角课程列表（分页）
- 课程评价（含敏感词过滤）
- 课程问答（发布问题 + 回复）

### 🏫 课堂互动
- 教师创建/结束课堂
- 学生签到
- 课堂投票（多选项，实时查看结果）
- 课堂聊天（经敏感词过滤）

### 📝 作业系统
- 教师创建作业（含客观题 + 主观题）
- 学生在线提交
- 教师批改打分

### ⏱️ 自习室
- 正向计时 / 倒计时两种模式
- 有效时长自动写入学情分析

### 🌸 花卉激励系统
- 积分获取 → 种子商店 → 培育花卉 → 开花
- 三维属性：阳光 / 水分 / 养份，道具兑换增加属性
- 四个生长阶段：种子 → 发芽 → 长叶 → 开花
- 生长进度实时计算

### 📊 学情分析
- 累计学习时长 & 周学习时长
- 作业正确率统计
- 自然周滚动归零

### 🔔 通知系统
- 系统通知分页展示

### 🛡️ 管理端
- 课程审核（通过/驳回）
- 花卉种子 CRUD
- 用户封禁/解封
- 敏感词管理

---

## 快速开始

### 前置要求

- JDK 17+
- MySQL 8.x
- Redis（可选，仅验证码功能依赖）
- Maven

### 1. 克隆项目

```bash
git clone https://github.com/your-org/qingyun.git
cd qingyun
```

### 2. 初始化数据库

```bash
mysql -u root -p < src/main/resources/db/init.sql
```

### 3. 配置 application.yml

```bash
cp src/main/resources/application.yml.example src/main/resources/application.yml
```

修改 `application.yml` 中的配置：

| 配置项 | 说明 |
|--------|------|
| `spring.datasource.username/password` | 数据库连接 |
| `spring.data.redis.host` | Redis 地址 |
| `jwt.secret` | JWT 密钥 |
| `wx.mock` | 微信 mock 模式（本地开发建议 true） |
| `oss.*` | 阿里云 OSS（如不需上传可留空） |

### 4. 运行

```bash
# 开发模式
mvn spring-boot:run

# 打包运行
mvn clean package -DskipTests
java -jar target/qingyun-0.0.1-SNAPSHOT.jar

# 指定 profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 5. 健康检查

```bash
curl http://localhost:8080/hello
# → Hello, this is qingyun!
```

---

## 项目结构

```
src/main/java/com/jycz/qingyun/
├── config/                  # 配置：MyBatis分页、WebMVC拦截器、Redis、OSS、全局异常
│   ├── GlobalExceptionHandler.java
│   ├── MybatisConfig.java
│   ├── OssConfig.java
│   ├── RedisConfig.java
│   └── WebMvcConfig.java
├── constants/               # 常量定义
├── controller/              # 26个控制器
│   ├── LoginController.java           # 登录注册
│   ├── MpLoginController.java         # 微信小程序登录
│   ├── VerifyCodeController.java      # 验证码
│   ├── InfoController.java            # 个人信息
│   ├── CourseController.java          # 课程（学生/教师）
│   ├── CourseAdminController.java     # 课程管理（管理员）
│   ├── CourseAuditController.java     # 课程审核
│   ├── ClassController.java           # 课堂管理
│   ├── CheckinController.java         # 签到
│   ├── VoteController.java            # 投票
│   ├── ClassChatController.java       # 课堂聊天
│   ├── AssignmentController.java      # 作业
│   ├── StudyRoomController.java       # 自习室
│   ├── CourseReviewController.java    # 课程评价
│   ├── CourseProblemController.java   # 课程问答
│   ├── FlowersController.java         # 花卉系统
│   ├── SeedAdminController.java       # 种子管理（管理员）
│   ├── AnalysisController.java        # 学情分析
│   ├── NoticesController.java         # 通知
│   ├── FileController.java            # 文件上传
│   ├── SensitiveWordController.java   # 敏感词管理（管理员）
│   ├── BanUserController.java         # 用户封禁（管理员）
│   ├── FeedbackController.java        # 反馈
│   └── TryController.java             # 健康检查
├── interceptor/
│   └── JwtInterceptor.java          # JWT 拦截器
├── mapper/                  # 23个 Mapper 接口
├── model/
│   ├── dto/                 # 32个请求体
│   ├── entity/              # 24个实体类（对应 23 张表）
│   └── vo/                  # 38个响应体
├── service/
│   └── serviceImpl/         # 服务实现
└── utils/
    ├── JwtUtil.java              # JWT 工具
    ├── WxUtil.java               # 微信工具
    ├── BusinessException.java    # 业务异常
    ├── SensitiveWordFilter.java  # DFA 敏感词过滤
    └── ApiResult.java            # 统一响应
```

---

## API 概览

完整 API 清单见 [docs/project-features.md](docs/project-features.md)。

### 核心接口

| 方法 | 路径 | 说明 | 角色 |
|------|------|------|------|
| POST | `/qingyun/login` | 账号密码登录 | 无 |
| POST | `/qingyun/register` | 注册 | 无 |
| POST | `/qingyun/login/mp` | 微信小程序登录 | 无 |
| GET | `/qingyun/info` | 获取个人信息 | 已登录 |
| POST | `/qingyun/course/create` | 创建课程 | 教师 |
| POST | `/qingyun/course/join` | 加入课程 | 学生 |
| POST | `/qingyun/studyroom/create` | 开始自习 | 已登录 |
| POST | `/qingyun/assignment/create` | 创建作业 | 教师 |
| POST | `/qingyun/flowers/plant` | 播种 | 已登录 |
| GET | `/qingyun/analysis/report` | 学情报告 | 已登录 |
| PUT | `/qingyun/course/audit` | 审核课程 | 管理员 |

---

## 数据库

23 张表，覆盖全部功能模块：

| 模块 | 表 |
|------|-----|
| 用户 | `user` |
| 课程 | `course`, `course_student` |
| 课程评价 | `course_review` |
| 课程问答 | `course_problem`, `course_problem_reply` |
| 课堂 | `class` |
| 签到 | `class_check` |
| 投票 | `class_vote`, `vote_record` |
| 聊天 | `class_chat` |
| 作业 | `assignment`, `question`, `object_submit`, `subject_submit` |
| 自习室 | `study_room` |
| 学情 | `student_analysis` |
| 花卉 | `seed`, `flower`, `points_record`, `shop_item` |
| 通知 | `notice` |
| 敏感词 | `sensitive_word` |

建表 SQL 见 `src/main/resources/db/init.sql`。

---

## 开发规范

- **分层职责**：Controller 接收校验 → Service 业务逻辑 → Mapper 数据访问（无 Manager/DAO 层）
- **实体**：Lombok `@Data`，字段与数据库列名一致
- **API 返回**：全部返回 `ApiResult<T>`，异常抛 `BusinessException`
- **事务**：服务层使用 `@Transactional` 声明式事务
- **请求校验**：`@Valid` + `jakarta.validation` 注解
- **敏感词过滤**：所有文本输入（聊天、评价、提问）经过 `SensitiveWordFilter`

---

## 团队

| 成员 | 角色 |
|------|------|
| **王欣悦** | 负责人 |
| **孙唯一** | 负责人 |

---

<div align="center">
Built with Spring Boot · MyBatis-Plus · ❤️
</div>
