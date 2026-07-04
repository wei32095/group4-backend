package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jycz.qingyun.model.dto.CourseProblemRequest;
import com.jycz.qingyun.model.dto.ProblemReplyRequest;
import com.jycz.qingyun.model.entity.*;
import com.jycz.qingyun.model.vo.CourseProblemVO;
import com.jycz.qingyun.model.vo.ProblemDetailVO;
import com.jycz.qingyun.mapper.*;
import com.jycz.qingyun.service.CourseProblemService;
import com.jycz.qingyun.service.NoticeService;
import com.jycz.qingyun.service.PointsRecordService;
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
public class CourseProblemServiceImpl implements CourseProblemService {

    private final CourseProblemMapper courseProblemMapper;
    private final CourseProblemReplyMapper courseProblemReplyMapper;
    private final CourseMapper courseMapper;
    private final CourseStudentMapper courseStudentMapper;
    private final UserMapper userMapper;
    private final NoticeService noticeService;
    private final PointsRecordService pointsRecordService;

    // ========== 1. 发布问题 ==========
    @Override
    @Transactional
    public CourseProblemVO createProblem(CourseProblemRequest request, Long userId) {
        // 1. 查询课程
        Course course = courseMapper.selectById(request.getCourseId());
        if (course == null) {
            throw new BusinessException(404, "课程不存在");
        }

        // 2. 校验课程状态（只有 active 才能发布问题）
        if (!"active".equals(course.getStatus())) {
            throw new BusinessException(400, "该课程已结束，无法发布问题");
        }

        // 3. 校验用户是否已加入该课程
        LambdaQueryWrapper<CourseStudent> csWrapper = new LambdaQueryWrapper<>();
        csWrapper.eq(CourseStudent::getCourseId, request.getCourseId())
                .eq(CourseStudent::getUserId, userId);
        if (courseStudentMapper.selectCount(csWrapper) == 0) {
            throw new BusinessException(403, "您未加入该课程，无法发布问题");
        }

        // 4. 创建问题
        CourseProblem problem = new CourseProblem();
        problem.setCourseId(request.getCourseId());
        problem.setUserId(userId);
        problem.setTitle(request.getTitle());
        problem.setContent(request.getContent());
        problem.setReplyCount(0);
        courseProblemMapper.insert(problem);

        log.info("问题发布成功: problemId={}, userId={}, courseId={}",
                problem.getId(), userId, request.getCourseId());

        // 5. 获取用户信息
        User student = userMapper.selectById(userId);

        // 6. 发送问题发布通知给教师
        if (course != null && student != null) {
            noticeService.sendProblemNotice(
                    course.getUserId(),
                    student.getName(),
                    request.getTitle()
            );
        }

        return CourseProblemVO.builder()
                .problemId(problem.getId())
                .courseId(problem.getCourseId())
                .userId(problem.getUserId())
                .userName(student != null ? student.getName() : "未知用户")
                .userAvatar(student != null ? student.getAvatar() : null)
                .userRole(student != null ? student.getRole() : null)
                .title(problem.getTitle())
                .content(problem.getContent())
                .replyCount(0)
                .createdAt(problem.getCreatedAt())
                .build();
    }


    // ========== 2. 问题列表 ==========
    @Override
    public List<CourseProblemVO> getProblemList(Long courseId, Long userId, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;

        // 1. 校验课程是否存在
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException(404, "课程不存在");
        }

        // 2. ✅ 校验权限：教师必须是该课程创建者，学生必须已加入
        boolean isTeacher = course.getUserId().equals(userId);
        if (!isTeacher) {
            LambdaQueryWrapper<CourseStudent> csWrapper = new LambdaQueryWrapper<>();
            csWrapper.eq(CourseStudent::getCourseId, courseId)
                    .eq(CourseStudent::getUserId, userId);
            if (courseStudentMapper.selectCount(csWrapper) == 0) {
                throw new BusinessException(403, "您未加入该课程，无法查看问题列表");
            }
        }

        // 3. 查询问题列表
        LambdaQueryWrapper<CourseProblem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseProblem::getCourseId, courseId)
                .orderByDesc(CourseProblem::getCreatedAt)
                .last("LIMIT " + offset + "," + pageSize);
        List<CourseProblem> problems = courseProblemMapper.selectList(wrapper);

        if (problems.isEmpty()) {
            return new ArrayList<>();
        }

        // 4. 批量查询用户信息
        List<Long> userIds = problems.stream()
                .map(CourseProblem::getUserId)
                .distinct()
                .collect(Collectors.toList());

        final Map<Long, User> userMap;
        if (!userIds.isEmpty()) {
            userMap = userMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u));
        } else {
            userMap = new HashMap<>();
        }

        // 5. 组装结果
        return problems.stream().map(problem -> {
            User user = userMap.get(problem.getUserId());
            return CourseProblemVO.builder()
                    .problemId(problem.getId())
                    .courseId(problem.getCourseId())
                    .userId(problem.getUserId())
                    .userName(user != null ? user.getName() : "未知用户")
                    .userAvatar(user != null ? user.getAvatar() : null)
                    .userRole(user != null ? user.getRole() : null)
                    .title(problem.getTitle())
                    .content(problem.getContent())
                    .replyCount(problem.getReplyCount())
                    .createdAt(problem.getCreatedAt())
                    .build();
        }).collect(Collectors.toList());
    }


    // ========== 3. 问题详情 ==========
    @Override
    public ProblemDetailVO getProblemDetail(Long problemId, Long userId) {
        // 1. 查询问题
        CourseProblem problem = courseProblemMapper.selectById(problemId);
        if (problem == null) {
            throw new BusinessException(404, "问题不存在");
        }

        // 2. 校验课程
        Course course = courseMapper.selectById(problem.getCourseId());
        if (course == null) {
            throw new BusinessException(404, "课程不存在");
        }

        // 3. ✅ 校验权限：教师必须是该课程创建者，学生必须已加入
        boolean isTeacher = course.getUserId().equals(userId);
        if (!isTeacher) {
            LambdaQueryWrapper<CourseStudent> csWrapper = new LambdaQueryWrapper<>();
            csWrapper.eq(CourseStudent::getCourseId, problem.getCourseId())
                    .eq(CourseStudent::getUserId, userId);
            if (courseStudentMapper.selectCount(csWrapper) == 0) {
                throw new BusinessException(403, "您未加入该课程，无法查看问题详情");
            }
        }

        // 4. 获取提问者信息
        User asker = userMapper.selectById(problem.getUserId());

        // 5. 查询所有回复
        List<CourseProblemReply> replies = courseProblemReplyMapper.selectByProblemId(problemId);

        // 6. 批量查询回复者信息
        List<Long> replyUserIds = replies.stream()
                .map(CourseProblemReply::getUserId)
                .distinct()
                .collect(Collectors.toList());

        final Map<Long, User> userMap;
        if (!replyUserIds.isEmpty()) {
            userMap = userMapper.selectBatchIds(replyUserIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u));
        } else {
            userMap = new HashMap<>();
        }

        // 7. 获取课程教师ID（用于判断是否是教师回复）
        Long teacherId = course != null ? course.getUserId() : null;

        // 8. 组装回复列表
        List<ProblemDetailVO.ReplyVO> replyVOs = new ArrayList<>();
        for (CourseProblemReply reply : replies) {
            User user = userMap.get(reply.getUserId());
            boolean isTeacherReply = user != null && user.getId().equals(teacherId);
            replyVOs.add(ProblemDetailVO.ReplyVO.builder()
                    .replyId(reply.getId())
                    .userId(reply.getUserId())
                    .userName(user != null ? user.getName() : "未知用户")
                    .userAvatar(user != null ? user.getAvatar() : null)
                    .userRole(user != null ? user.getRole() : null)
                    .content(reply.getContent())
                    .isTeacher(isTeacherReply)
                    .createdAt(reply.getCreatedAt())
                    .build());
        }

        // 9. 返回结果
        return ProblemDetailVO.builder()
                .problemId(problem.getId())
                .courseId(problem.getCourseId())
                .userId(problem.getUserId())
                .userName(asker != null ? asker.getName() : "未知用户")
                .userAvatar(asker != null ? asker.getAvatar() : null)
                .userRole(asker != null ? asker.getRole() : null)
                .title(problem.getTitle())
                .content(problem.getContent())
                .createdAt(problem.getCreatedAt())
                .replies(replyVOs)
                .build();
    }


    // ========== 4. 回复问题 ==========
    @Override
    @Transactional
    public CourseProblemVO replyProblem(ProblemReplyRequest request, Long userId) {
        // 1. 查询问题
        CourseProblem problem = courseProblemMapper.selectById(request.getProblemId());
        if (problem == null) {
            throw new BusinessException(404, "问题不存在");
        }

        // 2. 校验课程
        Course course = courseMapper.selectById(problem.getCourseId());
        if (course == null) {
            throw new BusinessException(404, "课程不存在");
        }

        // 3. 校验用户是否已加入该课程
        boolean isTeacher = course.getUserId().equals(userId);
        if (!isTeacher) {
            LambdaQueryWrapper<CourseStudent> csWrapper = new LambdaQueryWrapper<>();
            csWrapper.eq(CourseStudent::getCourseId, problem.getCourseId())
                    .eq(CourseStudent::getUserId, userId);
            if (courseStudentMapper.selectCount(csWrapper) == 0) {
                throw new BusinessException(403, "您未加入该课程，无法回复");
            }
        }

        // 4. 创建回复
        CourseProblemReply reply = new CourseProblemReply();
        reply.setProblemId(request.getProblemId());
        reply.setUserId(userId);
        reply.setContent(request.getContent());
        courseProblemReplyMapper.insert(reply);

        // 5. 更新问题回复数
        problem.setReplyCount(problem.getReplyCount() + 1);
        courseProblemMapper.updateById(problem);

        log.info("回复成功: replyId={}, problemId={}, userId={}",
                reply.getId(), request.getProblemId(), userId);

        // 6. 获取回复者信息
        User replier = userMapper.selectById(userId);

        // 7. 发送问题被回复通知给问题发布者（自己回复自己不通知）
        if (!problem.getUserId().equals(userId) && replier != null) {
            noticeService.sendProblemRepliedNotice(
                    problem.getUserId(),
                    replier.getName(),
                    problem.getTitle()
            );
        }

        // 8. 教师回复问题加分
        if (course != null && course.getUserId().equals(userId)) {
            pointsRecordService.handleProblemRepliedPoints(problem.getUserId());
            log.info("教师回复问题，给问题发布者加分: problemId={}, authorId={}", problem.getId(), problem.getUserId());
        }

        // 9. 获取问题发布者信息
        User author = userMapper.selectById(problem.getUserId());

        return CourseProblemVO.builder()
                .problemId(problem.getId())
                .courseId(problem.getCourseId())
                .userId(problem.getUserId())
                .userName(author != null ? author.getName() : "未知用户")
                .userAvatar(author != null ? author.getAvatar() : null)
                .userRole(author != null ? author.getRole() : null)
                .title(problem.getTitle())
                .content(problem.getContent())
                .replyCount(problem.getReplyCount())
                .createdAt(problem.getCreatedAt())
                .build();
    }
}