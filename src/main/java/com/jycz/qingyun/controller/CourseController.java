package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.dto.CourseCreateRequest;
import com.jycz.qingyun.model.dto.CourseJoinRequest;
import com.jycz.qingyun.model.vo.*;
import com.jycz.qingyun.service.CourseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/qingyun/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping("/create")
    public ApiResult<CourseCreateVO> createCourse(
            @Valid @RequestBody CourseCreateRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 2) {
            return ApiResult.error(403, "仅教师可创建课程");
        }

        CourseCreateVO response = courseService.createCourse(request, userId);
        return ApiResult.success("课程创建成功", response);
    }

    @PostMapping("/join")
    public ApiResult<CourseJoinVO> joinCourse(
            @Valid @RequestBody CourseJoinRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 1) {
            return ApiResult.error(403, "仅学生可加入课程");
        }

        CourseJoinVO response = courseService.joinCourse(request, userId);
        return ApiResult.success("加入课程成功", response);
    }

    @GetMapping("/detail")
    public ApiResult<CourseDetailVO> getCourseDetail(
            @RequestParam Long courseId,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        CourseDetailVO response = courseService.getCourseDetail(courseId, userId, role);
        return ApiResult.success(response);
    }

    /**
     * 学生课程列表
     * /qingyun/course/student/list
     */
    @GetMapping("/student/list")
    public ApiResult<List<CourseListStudentVO>> getStudentCourseList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 1) {
            return ApiResult.error(403, "仅学生可查看");
        }

        List<CourseListStudentVO> response = courseService.getStudentCourseList(userId, pageNum, pageSize);
        return ApiResult.success(response);
    }

    /**
     * 老师课程列表
     * GET /qingyun/course/teacher/list
     */
    @GetMapping("/teacher/list")
    public ApiResult<List<CourseListTeacherVO>> getTeacherCourseList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 2) {
            return ApiResult.error(403, "仅教师可查看");
        }

        List<CourseListTeacherVO> response = courseService.getTeacherCourseList(userId, pageNum, pageSize);
        return ApiResult.success(response);
    }

}