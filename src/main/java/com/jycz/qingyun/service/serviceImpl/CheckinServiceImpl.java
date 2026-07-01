package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jycz.qingyun.model.dto.CheckinSubmitRequest;
import com.jycz.qingyun.model.entity.Class;
import com.jycz.qingyun.model.entity.ClassCheck;
import com.jycz.qingyun.model.entity.CourseStudent;
import com.jycz.qingyun.model.entity.User;
import com.jycz.qingyun.model.vo.CheckinResultVO;
import com.jycz.qingyun.model.vo.CheckinSubmitVO;
import com.jycz.qingyun.mapper.ClassCheckMapper;
import com.jycz.qingyun.mapper.ClassMapper;
import com.jycz.qingyun.mapper.CourseStudentMapper;
import com.jycz.qingyun.mapper.UserMapper;
import com.jycz.qingyun.service.CheckinService;
import com.jycz.qingyun.service.PointsRecordService;
import com.jycz.qingyun.utils.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor//
public class CheckinServiceImpl implements CheckinService {

    private final ClassCheckMapper classCheckMapper;
    private final ClassMapper classMapper;
    private final CourseStudentMapper courseStudentMapper;
    private final UserMapper userMapper;
    private final PointsRecordService pointsRecordService;  // ← 新增
    @Override
    @Transactional
    public CheckinSubmitVO submitCheckin(CheckinSubmitRequest request, Long studentId) {
        // 1. 查询课堂
        Class clazz = classMapper.selectById(request.getClassId());
        if (clazz == null) {
            throw new BusinessException(404, "课堂不存在");
        }

        // 2. 校验课堂是否已结束
        if ("ended".equals(clazz.getStatus())) {
            throw new BusinessException(400, "课堂已结束");
        }

        // 3. 校验学生是否加入该课程
        LambdaQueryWrapper<CourseStudent> csWrapper = new LambdaQueryWrapper<>();
        csWrapper.eq(CourseStudent::getCourseId, clazz.getCourseId())
                .eq(CourseStudent::getUserId, studentId);
        if (courseStudentMapper.selectCount(csWrapper) == 0) {
            throw new BusinessException(403, "您未加入该课程");
        }

        // 4. 检查是否已签到
        LambdaQueryWrapper<ClassCheck> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(ClassCheck::getClassId, request.getClassId())
                .eq(ClassCheck::getUserId, studentId);
        ClassCheck existing = classCheckMapper.selectOne(checkWrapper);
        if (existing != null) {
            throw new BusinessException(409, "您已签到");
        }

        // 5. 计算签到状态
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime classStartTime = clazz.getCreateTime();
        long minutesDiff = ChronoUnit.MINUTES.between(classStartTime, now);

        int checkStatusNum;
        String checkStatusStr;

        if (minutesDiff <= 5) {
            checkStatusNum = 1;
            checkStatusStr = "已签到";
        } else if (minutesDiff <= 10) {
            checkStatusNum = 2;
            checkStatusStr = "迟到";
        } else {
            throw new BusinessException(400, "已超过签到时间（超过10分钟）");
        }

        // 6. 保存签到记录
        ClassCheck classCheck = new ClassCheck();
        classCheck.setClassId(request.getClassId());
        classCheck.setUserId(studentId);
        classCheck.setCheckStatus(checkStatusNum);
        classCheck.setCheckinTime(now);
        classCheckMapper.insert(classCheck);



        // 8. 积分处理
        pointsRecordService.handleCheckinPoints(studentId, checkStatusNum);

        return CheckinSubmitVO.builder()
                .classId(clazz.getId())
                .classTitle(clazz.getClassTitle())
                .checkStatus(checkStatusStr)
                .checkinTime(now)
                .build();
    }

    @Override
    public CheckinResultVO getCheckinResult(Long classId, Long teacherId) {
        // 1. 查询课堂
        Class clazz = classMapper.selectById(classId);
        if (clazz == null) {
            throw new BusinessException(404, "课堂不存在");
        }

        // 2. 校验教师权限
        if (!clazz.getUserId().equals(teacherId)) {
            throw new BusinessException(403, "您不是该课堂的老师");
        }

        // 3. 获取该课程所有学生
        LambdaQueryWrapper<CourseStudent> csWrapper = new LambdaQueryWrapper<>();
        csWrapper.eq(CourseStudent::getCourseId, clazz.getCourseId());
        List<CourseStudent> allStudents = courseStudentMapper.selectList(csWrapper);

        // 4. 获取已签到记录
        LambdaQueryWrapper<ClassCheck> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(ClassCheck::getClassId, classId);
        List<ClassCheck> checkins = classCheckMapper.selectList(checkWrapper);
        Map<Long, ClassCheck> checkinMap = checkins.stream()
                .collect(Collectors.toMap(ClassCheck::getUserId, c -> c));

        // 5. 组装结果
        int normalCount = 0;
        int lateCount = 0;
        int absentCount = 0;
        List<CheckinResultVO.CheckinRecordVO> records = new ArrayList<>();

        for (CourseStudent cs : allStudents) {
            Long studentId = cs.getUserId();
            User student = userMapper.selectById(studentId);
            ClassCheck check = checkinMap.get(studentId);
            String checkStatusStr;
            LocalDateTime checkinTime = null;

            if (check != null) {
                checkinTime = check.getCheckinTime();
                if (check.getCheckStatus() == 1) {
                    checkStatusStr = "已签到";    // ← 中文
                    normalCount++;
                } else if (check.getCheckStatus() == 2) {
                    checkStatusStr = "迟到";      // ← 中文
                    lateCount++;
                } else {
                    checkStatusStr = "未知";
                }
            } else {
                checkStatusStr = "缺勤";          // ← 中文
                absentCount++;
            }

            records.add(CheckinResultVO.CheckinRecordVO.builder()
                    .userId(studentId)
                    .studentName(student != null ? student.getName() : "未知学生")
                    .checkStatus(checkStatusStr)   // ← 中文
                    .checkinTime(checkinTime)
                    .build());
        }

        return CheckinResultVO.builder()
                .classId(classId)
                .classTitle(clazz.getClassTitle())
                .totalStudents(allStudents.size())
                .normalCount(normalCount)
                .lateCount(lateCount)
                .absentCount(absentCount)
                .records(records)
                .build();
    }
}