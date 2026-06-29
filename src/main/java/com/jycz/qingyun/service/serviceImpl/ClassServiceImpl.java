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

@Slf4j
@Service
@RequiredArgsConstructor//
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
        clazz.setFileUrl(request.getFileUrl());
        clazz.setStatus("active");
        clazz.setCreateTime(LocalDateTime.now());  // ← 新增：设置创建时间

        classMapper.insert(clazz);
        log.info("课堂创建成功: classId={}, classTitle={}", clazz.getId(), clazz.getClassTitle());

        return ClassCreateVO.builder()
                .id(clazz.getId())
                .courseId(clazz.getCourseId())
                .userId(clazz.getUserId())
                .classTitle(clazz.getClassTitle())
                .fileUrl(clazz.getFileUrl())
                .status(clazz.getStatus())
                .createTime(clazz.getCreateTime())  // ← 新增：返回创建时间
                .build();
    }

    /**
     * 结束课堂
     */
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
        classMapper.updateById(clazz);
        log.info("课堂已结束: classId={}", classId);
    }

    @Override
    public List<ClassStudentVO> getStudentClassList(Long courseId, Long studentId) {
        LambdaQueryWrapper<CourseStudent> csWrapper = new LambdaQueryWrapper<>();
        csWrapper.eq(CourseStudent::getCourseId, courseId)
                .eq(CourseStudent::getUserId, studentId);
        if (courseStudentMapper.selectCount(csWrapper) == 0) {
            throw new BusinessException(403, "您未加入该课程");
        }

        LambdaQueryWrapper<Class> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Class::getCourseId, courseId)
                .orderByDesc(Class::getCreateTime);
        List<Class> classList = classMapper.selectList(wrapper);

        return classList.stream().map(clazz -> {
            Integer checkinStatus = null;

            if ("ended".equals(clazz.getStatus())) {
                LambdaQueryWrapper<ClassCheck> checkWrapper = new LambdaQueryWrapper<>();
                checkWrapper.eq(ClassCheck::getClassId, clazz.getId())
                        .eq(ClassCheck::getUserId, studentId);
                ClassCheck check = classCheckMapper.selectOne(checkWrapper);
                if (check != null) {
                    checkinStatus = check.getCheckStatus();
                } else {
                    checkinStatus = 3;
                }
            } else if ("active".equals(clazz.getStatus())) {
                LambdaQueryWrapper<ClassCheck> checkWrapper = new LambdaQueryWrapper<>();
                checkWrapper.eq(ClassCheck::getClassId, clazz.getId())
                        .eq(ClassCheck::getUserId, studentId);
                ClassCheck check = classCheckMapper.selectOne(checkWrapper);
                if (check != null) {
                    checkinStatus = check.getCheckStatus();
                }
            }

            return ClassStudentVO.builder()
                    .id(clazz.getId())
                    .classTitle(clazz.getClassTitle())
                    .status(clazz.getStatus())
                    .checkinStatus(checkinStatus)
                    .createTime(clazz.getCreateTime())  // ← 返回创建时间
                    .build();
        }).collect(Collectors.toList());
    }
}