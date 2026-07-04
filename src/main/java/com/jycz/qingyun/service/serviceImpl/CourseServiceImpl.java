package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jycz.qingyun.model.dto.CourseAuditRequest;
import com.jycz.qingyun.model.dto.CourseCreateRequest;
import com.jycz.qingyun.model.dto.CourseJoinRequest;
import com.jycz.qingyun.model.entity.Course;
import com.jycz.qingyun.model.entity.CourseStudent;
import com.jycz.qingyun.model.entity.User;
import com.jycz.qingyun.model.vo.*;
import com.jycz.qingyun.mapper.CourseMapper;
import com.jycz.qingyun.mapper.CourseStudentMapper;
import com.jycz.qingyun.mapper.UserMapper;
import com.jycz.qingyun.service.CourseService;
import com.jycz.qingyun.service.NoticeService;
import com.jycz.qingyun.utils.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;
    private final CourseStudentMapper courseStudentMapper;
    private final UserMapper userMapper;
    private final NoticeService noticeService;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random RANDOM = new Random();



    @Override
    @Transactional
    public CourseCreateVO createCourse(CourseCreateRequest request, Long teacherId) {
        LambdaQueryWrapper<Course> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(Course::getCourseTitle, request.getCourseTitle());
        if (courseMapper.selectCount(nameWrapper) > 0) {
            throw new BusinessException(409, "课程名称已存在");
        }

        String courseCode = generateUniqueCourseCode(request.getCourseTitle());

        Course course = new Course();
        course.setUserId(teacherId);
        course.setCourseTitle(request.getCourseTitle());
        course.setDescription(request.getDescription());
        course.setCover(request.getCover());
        course.setStudentCount(0);
        course.setCourseCode(courseCode);
        course.setStatus("pending");
        course.setAuditStatus(0);

        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        courseMapper.insert(course);
        log.info("课程创建成功: courseId={}, courseCode={}，, 等待管理员审核", course.getId(), courseCode);

        return CourseCreateVO.builder()
                .id(course.getId())
                .userId(course.getUserId())
                .courseTitle(course.getCourseTitle())
                .description(course.getDescription())
                .cover(course.getCover())
                .studentCount(course.getStudentCount())
                .courseCode(course.getCourseCode())
                .status(course.getStatus())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public CourseJoinVO joinCourse(CourseJoinRequest request, Long studentId) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getCourseCode, request.getCourseCode());
        Course course = courseMapper.selectOne(wrapper);

        if (course == null) {
            throw new BusinessException(404, "课程码不存在");
        }

        if (!"active".equals(course.getStatus())) {
            throw new BusinessException(400, "未找到该课程");
        }

        LambdaQueryWrapper<CourseStudent> csWrapper = new LambdaQueryWrapper<>();
        csWrapper.eq(CourseStudent::getCourseId, course.getId())
                .eq(CourseStudent::getUserId, studentId);
        if (courseStudentMapper.selectCount(csWrapper) > 0) {
            throw new BusinessException(409, "您已加入该课程");
        }

        CourseStudent courseStudent = new CourseStudent();
        courseStudent.setCourseId(course.getId());
        courseStudent.setUserId(studentId);
        courseStudent.setJoinedAt(LocalDateTime.now());
        courseStudentMapper.insert(courseStudent);

        course.setStudentCount(course.getStudentCount() + 1);
        courseMapper.updateById(course);

        User teacher = userMapper.selectById(course.getUserId());
        String teacherName = (teacher != null) ? teacher.getName() : "未知老师";

        // 7. 发送加入课程通知给学生
        noticeService.sendJoinCourseNotice(studentId, course.getCourseTitle());

        return CourseJoinVO.builder()
                .courseId(course.getId())
                .courseTitle(course.getCourseTitle())
                .description(course.getDescription())
                .cover(course.getCover())
                .studentCount(course.getStudentCount())
                .teacherName(teacherName)
                .joinedAt(courseStudent.getJoinedAt())
                .build();


    }

    @Override
    public CourseDetailVO getCourseDetail(Long courseId, Long userId, Integer role) {
        // 1. 查询课程
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException(404, "课程不存在");
        }

        // ✅ 定义变量
        LocalDateTime joinedAt = null;

        // 2. 权限校验
        if (role == 1) {
            // 学生：检查是否已加入，并获取加入时间
            LambdaQueryWrapper<CourseStudent> csWrapper = new LambdaQueryWrapper<>();
            csWrapper.eq(CourseStudent::getCourseId, courseId)
                    .eq(CourseStudent::getUserId, userId);
            CourseStudent cs = courseStudentMapper.selectOne(csWrapper);
            if (cs == null) {
                throw new BusinessException(403, "您未加入该课程，无权查看");
            }
            joinedAt = cs.getJoinedAt();  // ← 赋值
        } else if (role == 2) {
            // 教师：只能看自己的课程
            if (!course.getUserId().equals(userId)) {
                throw new BusinessException(403, "您不是该课程的教师，无权查看");
            }
        } else if (role == 3) {
            // 管理员：可以看所有课程
        } else {
            throw new BusinessException(403, "无权查看");
        }

        // 3. 获取教师信息
        User teacher = userMapper.selectById(course.getUserId());
        String teacherName = (teacher != null) ? teacher.getName() : "未知老师";
        String teacherAvatar = (teacher != null) ? teacher.getAvatar() : null;

        // 4. 返回
        return CourseDetailVO.builder()
                .id(course.getId())
                .courseTitle(course.getCourseTitle())
                .description(course.getDescription())
                .cover(course.getCover())
                .studentCount(course.getStudentCount())
                .courseCode(course.getCourseCode())
                .status(course.getStatus())

                .teacherName(teacherName)
                .teacherAvatar(teacherAvatar)
                .joinedAt(joinedAt)        // ← 使用变量
                .createdAt(course.getCreatedAt())
                .build();
    }

    @Override
    public List<CourseListStudentVO> getStudentCourseList(Long studentId, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;

        List<CourseStudent> courseStudents = courseStudentMapper.selectList(
                new LambdaQueryWrapper<CourseStudent>()
                        .eq(CourseStudent::getUserId, studentId)
                        .last("LIMIT " + offset + "," + pageSize)
        );

        if (courseStudents.isEmpty()) return new ArrayList<>();

        List<Long> courseIds = courseStudents.stream()
                .map(CourseStudent::getCourseId)
                .collect(Collectors.toList());

        List<Course> courses = courseMapper.selectBatchIds(courseIds);
        Map<Long, Course> courseMap = courses.stream()
                .collect(Collectors.toMap(Course::getId, c -> c));
        Map<Long, LocalDateTime> joinedTimeMap = courseStudents.stream()
                .collect(Collectors.toMap(CourseStudent::getCourseId, CourseStudent::getJoinedAt));

        List<Long> teacherIds = courses.stream().map(Course::getUserId).collect(Collectors.toList());
        Map<Long, User> teacherMap = userMapper.selectBatchIds(teacherIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        return courses.stream().map(course -> {
            User teacher = teacherMap.get(course.getUserId());
            return CourseListStudentVO.builder()
                    .courseId(course.getId())
                    .courseTitle(course.getCourseTitle())
                    .cover(course.getCover())
                    .teacherName(teacher != null ? teacher.getName() : "未知老师")
                    .studentCount(course.getStudentCount())
                    .status(course.getStatus())
                    .joinedAt(joinedTimeMap.get(course.getId()))
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<CourseListTeacherVO> getTeacherCourseList(Long teacherId, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;

        List<Course> courses = courseMapper.selectList(
                new LambdaQueryWrapper<Course>()
                        .eq(Course::getUserId, teacherId)
                        .eq(Course::getAuditStatus, 1)
                        .orderByDesc(Course::getCreatedAt)
                        .last("LIMIT " + offset + "," + pageSize)
        );

        if (courses.isEmpty()) return new ArrayList<>();

        return courses.stream().map(course ->
                CourseListTeacherVO.builder()
                        .courseId(course.getId())
                        .courseTitle(course.getCourseTitle())
                        .cover(course.getCover())
                        .studentCount(course.getStudentCount())
                        .courseCode(course.getCourseCode())
                        .status(course.getStatus())
                        .createdAt(course.getCreatedAt())
                        .build()
        ).collect(Collectors.toList());
    }

    // ========== 工具方法 ==========
    private String generateUniqueCourseCode(String courseTitle) {
        String prefix = getPinyinInitials(courseTitle);
        String year = String.valueOf(LocalDateTime.now().getYear()).substring(2);
        String code;
        int attempts = 0;
        int maxAttempts = 100;

        do {
            String random = generateRandomString(4);
            code = prefix + year + random;
            attempts++;
            if (attempts > maxAttempts) {
                random = generateRandomString(6);
                code = prefix + year + random;
            }
        } while (courseMapper.countByCourseCode(code) > 0);

        return code;
    }

    private String getPinyinInitials(String courseTitle) {
        if (courseTitle.length() >= 2) {
            return courseTitle.substring(0, 2).toUpperCase();
        }
        return courseTitle.toUpperCase();
    }

    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
    @Override
    @Transactional
    public CourseAuditVO auditCourse(CourseAuditRequest request, Long adminId) {
        // 1. 查询课程
        Course course = courseMapper.selectById(request.getCourseId());
        if (course == null) {
            throw new BusinessException(404, "课程不存在");
        }

        // 2. 检查是否已审核
        if (course.getAuditStatus() != null && course.getAuditStatus() != 0) {
            throw new BusinessException(400, "该课程已审核，请勿重复操作");
        }

        // 3. 更新审核状态
        course.setAuditStatus(request.getAuditStatus());
        course.setAuditRemark(request.getAuditRemark());
        course.setAuditTime(LocalDateTime.now());

        // 4. 如果审核通过，设置课程状态为 active
        if (request.getAuditStatus() == 1) {
            course.setStatus("active");
        } else {
            // 审核驳回，课程状态设为 archived
            course.setStatus("archived");
        }

        courseMapper.updateById(course);

        log.info("课程审核完成: courseId={}, adminId={}, status={}",
                course.getId(), adminId, request.getAuditStatus());

        // ✅ 如果审核通过，发送通知给教师
        if (request.getAuditStatus() == 1) {
            noticeService.sendAuditSuccessNotice(course.getUserId(), course.getCourseTitle());
        }else if (request.getAuditStatus() == 2) {
            // 审核驳回
            String remark = request.getAuditRemark() != null ? request.getAuditRemark() : "未填写原因";
            noticeService.sendAuditRejectNotice(course.getUserId(), course.getCourseTitle(), remark);
        }

        return CourseAuditVO.builder()
                .courseId(course.getId())
                .courseTitle(course.getCourseTitle())
                .auditStatus(course.getAuditStatus())
                .auditRemark(course.getAuditRemark())
                .auditTime(course.getAuditTime())
                .build();
    }

    @Override
    @Transactional
    public void endCourse(Long courseId, Long teacherId) {
        // 1. 查询课程
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException(404, "课程不存在");
        }

        // 2. 校验教师权限：只有该课程的创建者才能结束
        if (!course.getUserId().equals(teacherId)) {
            throw new BusinessException(403, "您不是该课程的教师");
        }

        // 3. 校验课程状态：已结束的不能重复结束
        if ("archived".equals(course.getStatus())) {
            throw new BusinessException(400, "课程已结束");
        }

        // 4. 结束课程：状态改为 archived
        course.setStatus("archived");
        courseMapper.updateById(course);

        log.info("课程已结束: courseId={}, teacherId={}", courseId, teacherId);

        // ✅ 发送课程结束通知给所有学生
        noticeService.sendCourseEndNotice(courseId, course.getCourseTitle());
    }

    @Override
    public List<AdminUserCourseVO> getAdminUserCourses(Long userId, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;

        // 1. 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        List<AdminUserCourseVO> allCourses = new ArrayList<>();

        if (user.getRole() == 1) {
            // 学生：查询已加入的课程
            List<CourseStudent> courseStudents = courseStudentMapper.selectList(
                    new LambdaQueryWrapper<CourseStudent>()
                            .eq(CourseStudent::getUserId, userId)
            );

            if (!courseStudents.isEmpty()) {
                List<Long> courseIds = courseStudents.stream()
                        .map(CourseStudent::getCourseId)
                        .collect(Collectors.toList());

                List<Course> courses = courseMapper.selectBatchIds(courseIds);
                Map<Long, Course> courseMap = courses.stream()
                        .collect(Collectors.toMap(Course::getId, c -> c));
                Map<Long, LocalDateTime> joinedTimeMap = courseStudents.stream()
                        .collect(Collectors.toMap(CourseStudent::getCourseId, CourseStudent::getJoinedAt));

                List<Long> teacherIds = courses.stream().map(Course::getUserId).collect(Collectors.toList());
                Map<Long, User> teacherMap = userMapper.selectBatchIds(teacherIds).stream()
                        .collect(Collectors.toMap(User::getId, u -> u));

                for (CourseStudent cs : courseStudents) {
                    Course course = courseMap.get(cs.getCourseId());
                    if (course == null) continue;
                    User teacher = teacherMap.get(course.getUserId());

                    allCourses.add(AdminUserCourseVO.builder()
                            .courseId(course.getId())
                            .courseTitle(course.getCourseTitle())
                            .teacherName(teacher != null ? teacher.getName() : "未知老师")
                            .studentCount(course.getStudentCount())
                            .courseCode(course.getCourseCode())
                            .status(course.getStatus())
                            .joinedAt(joinedTimeMap.get(course.getId()))
                            .relationType("joined")
                            .build());
                }
            }

        } else if (user.getRole() == 2) {
            // 教师：查询创建的课程
            List<Course> courses = courseMapper.selectList(
                    new LambdaQueryWrapper<Course>()
                            .eq(Course::getUserId, userId)
            );

            for (Course course : courses) {
                allCourses.add(AdminUserCourseVO.builder()
                        .courseId(course.getId())
                        .courseTitle(course.getCourseTitle())
                        .teacherName(user.getName())
                        .studentCount(course.getStudentCount())
                        .courseCode(course.getCourseCode())
                        .status(course.getStatus())
                        .joinedAt(null)
                        .relationType("created")
                        .build());
            }
        } else {
            throw new BusinessException(400, "该用户角色暂不支持查看课程");
        }

        // 手动分页
        int total = allCourses.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);

        if (start >= total) {
            return new ArrayList<>();
        }

        return allCourses.subList(start, end);
    }

    @Override
    public List<CoursePendingVO> getPendingCourseList() {
        // 1. 查询待审核课程（audit_status = 0）
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getAuditStatus, 0)
                .orderByDesc(Course::getCreatedAt);
        List<Course> courses = courseMapper.selectList(wrapper);

        if (courses.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 批量查询教师信息
        List<Long> teacherIds = courses.stream()
                .map(Course::getUserId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, User> teacherMap = userMapper.selectBatchIds(teacherIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        // 3. 组装结果
        return courses.stream().map(course -> {
            User teacher = teacherMap.get(course.getUserId());
            return CoursePendingVO.builder()
                    .courseId(course.getId())
                    .courseTitle(course.getCourseTitle())
                    .teacherId(course.getUserId())
                    .teacherName(teacher != null ? teacher.getName() : "未知老师")
                    .description(course.getDescription())
                    .cover(course.getCover())
                    .courseCode(course.getCourseCode())
                    .status(course.getStatus())
                    .auditStatus(course.getAuditStatus())
                    .createdAt(course.getCreatedAt())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getAdminCourseList(String keyword, String filterStatus, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;

        // 1. 构建查询条件
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索（课程名称 或 教师姓名）
        if (keyword != null && !keyword.trim().isEmpty()) {
            String trimmedKeyword = keyword.trim();

            // 先查询匹配的教师ID
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.like(User::getName, trimmedKeyword);
            List<User> users = userMapper.selectList(userWrapper);
            List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());

            // ✅ 关键修复：如果 userIds 为空，只按课程名称搜索
            if (userIds.isEmpty()) {
                wrapper.like(Course::getCourseTitle, trimmedKeyword);
            } else {
                // 如果 userIds 不为空，用 OR 条件
                wrapper.and(w -> w
                        .like(Course::getCourseTitle, trimmedKeyword)
                        .or()
                        .in(Course::getUserId, userIds)
                );
            }
        }

        // 筛选状态
        if (filterStatus != null && !filterStatus.isEmpty()) {
            switch (filterStatus) {
                case "active":
                    wrapper.eq(Course::getStatus, "active")
                            .eq(Course::getAuditStatus, 1);
                    break;
                case "pending":
                    wrapper.eq(Course::getAuditStatus, 0);
                    break;
                case "ended":
                    wrapper.eq(Course::getStatus, "archived")
                            .eq(Course::getAuditStatus, 1);
                    break;
                case "all":
                default:
                    break;
            }
        }

        wrapper.orderByDesc(Course::getCreatedAt);

        log.info("SQL: {}", wrapper.getCustomSqlSegment());

        // 2. 查询总数
        Long total = courseMapper.selectCount(wrapper);

        // 3. 查询分页数据
        wrapper.last("LIMIT " + offset + "," + pageSize);
        List<Course> courses = courseMapper.selectList(wrapper);

        // 4. 组装结果
        List<CourseAdminListVO> list = new ArrayList<>();
        if (!courses.isEmpty()) {
            List<Long> teacherIds = courses.stream()
                    .map(Course::getUserId)
                    .distinct()
                    .collect(Collectors.toList());
            Map<Long, User> teacherMap = userMapper.selectBatchIds(teacherIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u));

            list = courses.stream().map(course -> {
                User teacher = teacherMap.get(course.getUserId());
                return CourseAdminListVO.builder()
                        .courseId(course.getId())
                        .courseTitle(course.getCourseTitle())
                        .cover(course.getCover())
                        .teacherId(course.getUserId())
                        .teacherName(teacher != null ? teacher.getName() : "未知老师")
                        .studentCount(course.getStudentCount())
                        .courseCode(course.getCourseCode())
                        .status(course.getStatus())
                        .auditStatus(course.getAuditStatus())
                        .auditRemark(course.getAuditRemark())
                        .auditTime(course.getAuditTime())
                        .createdAt(course.getCreatedAt())
                        .build();
            }).collect(Collectors.toList());
        }

        // 5. 返回分页 Map
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("pages", (int) Math.ceil((double) total / pageSize));
        result.put("list", list);

        return result;
    }
}