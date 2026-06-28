package com.jycz.qingyun.service.serviceImpl;

import com.jycz.qingyun.mapper.CourseMapper;
import com.jycz.qingyun.model.dto.CourseCreateRequest;
import com.jycz.qingyun.model.dto.CourseResponse;
import com.jycz.qingyun.model.entity.Course;
import com.jycz.qingyun.service.CourseService;
import com.jycz.qingyun.utils.CourseCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public CourseResponse createCourse(CourseCreateRequest request, Long teacherId, String teacherName) {
        // 1. 参数校验
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new RuntimeException("课程名称不能为空");
        }

        // 2. 创建课程实体
        Course course = new Course();
        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course.setCoverImage(request.getCoverImage());
        course.setTeacherId(teacherId);
        course.setTeacherName(teacherName);
        course.setCourseCode(CourseCodeGenerator.generateCode());
        course.setStatus("active");
        course.setCreatedAt(LocalDateTime.now());

        // 3. 保存到数据库
        courseMapper.insert(course);

        // 4. 构造响应
        return convertToResponse(course);
    }

    @Override
    public CourseResponse getCourseDetail(Long courseId) {
        Course course = courseMapper.findById(courseId);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        return convertToResponse(course);
    }

    @Override
    public List<CourseResponse> getCourseList(Long teacherId) {
        List<Course> courses = courseMapper.findByTeacherId(teacherId);
        List<CourseResponse> responses = new ArrayList<>();
        for (Course course : courses) {
            responses.add(convertToResponse(course));
        }
        return responses;
    }

    private CourseResponse convertToResponse(Course course) {
        CourseResponse response = new CourseResponse();
        response.setCourseId(course.getId());
        response.setName(course.getName());
        response.setDescription(course.getDescription());
        response.setCoverImage(course.getCoverImage());
        response.setTeacherId(course.getTeacherId());
        response.setTeacherName(course.getTeacherName());
        response.setCourseCode(course.getCourseCode());
        response.setStatus(course.getStatus());
        response.setCreatedAt(course.getCreatedAt());
        return response;
    }
}