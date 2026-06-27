package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.dto.CourseCreateRequest;
import com.jycz.qingyun.model.dto.CourseResponse;
import com.jycz.qingyun.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping("/create")
    public ApiResult<CourseResponse> createCourse(
            @RequestBody CourseCreateRequest request,
            HttpServletRequest httpRequest) {

        // 模拟从Token中获取用户信息（实际项目中用JWT解析）
        Long teacherId = 1L;
        String teacherName = "王老师";

        try {
            CourseResponse response = courseService.createCourse(request, teacherId, teacherName);
            return ApiResult.success("课程创建成功", response);
        } catch (RuntimeException e) {
            return ApiResult.error(400, e.getMessage());
        }
    }

    @GetMapping("/list")
    public ApiResult<List<CourseResponse>> getCourseList(HttpServletRequest httpRequest) {
        Long teacherId = 1L;

        try {
            List<CourseResponse> list = courseService.getCourseList(teacherId);
            return ApiResult.success(list);
        } catch (RuntimeException e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @GetMapping("/{courseId}")
    public ApiResult<CourseResponse> getCourseDetail(@PathVariable Long courseId) {
        try {
            CourseResponse response = courseService.getCourseDetail(courseId);
            return ApiResult.success(response);
        } catch (RuntimeException e) {
            return ApiResult.error(404, e.getMessage());
        }
    }
}