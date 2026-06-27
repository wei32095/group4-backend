package com.jycz.qingyun.service;

import com.jycz.qingyun.model.dto.CourseCreateRequest;
import com.jycz.qingyun.model.dto.CourseResponse;
import java.util.List;

public interface CourseService {
    CourseResponse createCourse(CourseCreateRequest request, Long teacherId, String teacherName);
    CourseResponse getCourseDetail(Long courseId);
    List<CourseResponse> getCourseList(Long teacherId);
}