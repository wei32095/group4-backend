package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jycz.qingyun.model.dto.ClassCreateRequest;
import com.jycz.qingyun.model.entity.Class;
import com.jycz.qingyun.model.entity.ClassCheck;
import com.jycz.qingyun.model.entity.Course;
import com.jycz.qingyun.model.entity.CourseStudent;
import com.jycz.qingyun.model.vo.ClassCreateVO;
import com.jycz.qingyun.model.vo.ClassStudentVO;
import com.jycz.qingyun.mapper.ClassCheckMapper;
import com.jycz.qingyun.mapper.ClassMapper;
import com.jycz.qingyun.mapper.CourseMapper;
import com.jycz.qingyun.mapper.CourseStudentMapper;
import com.jycz.qingyun.service.ClassService;
import com.jycz.qingyun.utils.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private final ClassMapper classMapper;
    private final CourseMapper courseMapper;
    private final CourseStudentMapper courseStudentMapper;
    private final ClassCheckMapper classCheckMapper;

    @Override
    @Transactional
    public ClassCreateVO createClass(ClassCreateRequest request, Long teacherId) {
        Course course = courseMapper.selectById(request.getCourseId());
        if (course == null) {
            throw new BusinessException(404, "课程不存在");
        }

        if (!course.getUserId().equals(teacherId)) {
            throw new BusinessException(403, "您不是该课程的教师，无权创建课堂");
        }

        Class clazz = new Class();
        clazz.setCourseId(request.getCourseId());
        clazz.setUserId(teacherId);
        clazz.setClassTitle(request.getClassTitle());
        clazz.setStatus("active");
        clazz.setCreateTime(LocalDateTime.now());
        clazz.setEndTime(null);

        classMapper.insert(clazz);
        log.info("课堂创建成功: classId={}, classTitle={}", clazz.getId(), clazz.getClassTitle());

        return ClassCreateVO.builder()
                .id(clazz.getId())
                .courseId(clazz.getCourseId())
                .userId(clazz.getUserId())
                .classTitle(clazz.getClassTitle())
                .status(clazz.getStatus())
                .createTime(clazz.getCreateTime())
                .build();
    }

    @Override
    @Transactional
    public void endClass(Long classId, Long teacherId) {
        Class clazz = classMapper.selectById(classId);
        if (clazz == null) {
            throw new BusinessException(404, "课堂不存在");
        }

        if (!clazz.getUserId().equals(teacherId)) {
            throw new BusinessException(403, "您不是该课堂的老师");
        }

        if ("ended".equals(clazz.getStatus())) {
            throw new BusinessException(400, "课堂已结束");
        }

        clazz.setStatus("ended");
        clazz.setEndTime(LocalDateTime.now());
        classMapper.updateById(clazz);

        long durationMinutes = java.time.Duration.between(clazz.getCreateTime(), clazz.getEndTime()).toMinutes();
        log.info("课堂已结束: classId={}, 时长={}分钟", classId, durationMinutes);
    }

    @Override
    public List<ClassStudentVO> getClassList(Long courseId, Long userId, Integer role) {
        // 1. 查询课程
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException(404, "课程不存在");
        }

        // 2. 如果是教师，必须是该课程创建者
        boolean isTeacher = course.getUserId().equals(userId);

        // 3. 如果不是教师，校验学生是否已加入该课程
        if (!isTeacher) {
            LambdaQueryWrapper<CourseStudent> csWrapper = new LambdaQueryWrapper<>();
            csWrapper.eq(CourseStudent::getCourseId, courseId)
                    .eq(CourseStudent::getUserId, userId);
            if (courseStudentMapper.selectCount(csWrapper) == 0) {
                throw new BusinessException(403, "您未加入该课程");
            }
        }

        // 4. 查询课堂列表
        LambdaQueryWrapper<Class> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Class::getCourseId, courseId)
                .orderByDesc(Class::getCreateTime);
        List<Class> classList = classMapper.selectList(wrapper);

        // 5. 组装数据
        return classList.stream().map(clazz -> {
            String checkinStatus = null;

            // 只有学生（role=1）才计算签到状态
            if (role == 1) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime classStartTime = clazz.getCreateTime();

                // 查询该学生的签到记录
                LambdaQueryWrapper<ClassCheck> checkWrapper = new LambdaQueryWrapper<>();
                checkWrapper.eq(ClassCheck::getClassId, clazz.getId())
                        .eq(ClassCheck::getUserId, userId);
                ClassCheck check = classCheckMapper.selectOne(checkWrapper);

                if (check != null) {
                    // ✅ 有签到记录：根据签到时间判断
                    long minutesDiff = ChronoUnit.MINUTES.between(classStartTime, check.getCheckinTime());
                    if (minutesDiff <= 5) {
                        checkinStatus = "已签到";
                    } else {
                        checkinStatus = "迟到";
                    }
                } else {
                    // ❌ 无签到记录
                    if ("ended".equals(clazz.getStatus())) {
                        // 课堂已结束，未签到 → 缺勤
                        checkinStatus = "缺勤";
                    } else {
                        // 课堂进行中：根据当前时间判断
                        long minutesDiff = ChronoUnit.MINUTES.between(classStartTime, now);
                        if (minutesDiff <= 10) {
                            checkinStatus = "未签到";
                        } else {
                            checkinStatus = "缺勤";
                        }
                    }
                }
            }

            return ClassStudentVO.builder()
                    .id(clazz.getId())
                    .classTitle(clazz.getClassTitle())
                    .status(clazz.getStatus())
                    .checkinStatus(checkinStatus)
                    .createTime(clazz.getCreateTime())
                    .endTime(clazz.getEndTime())
                    .build();
        }).collect(Collectors.toList());
    }
}