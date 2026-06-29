package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.*;
import com.jycz.qingyun.model.vo.*;
import com.jycz.qingyun.service.AssignmentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/qingyun/assignment")
@RequiredArgsConstructor
public class AssignmentController {
//
    private final AssignmentService assignmentService;

    @PostMapping("/create")
    public ApiResult<AssignmentCreateVO> createAssignment(
            @Valid @RequestBody AssignmentCreateRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 2) {
            return ApiResult.error(403, "仅教师可发布作业");
        }

        AssignmentCreateVO response = assignmentService.createAssignment(request, userId);
        return ApiResult.success("作业发布成功", response);
    }

    @GetMapping("/student/list")
    public ApiResult<List<AssignmentStudentListVO>> getStudentAssignmentList(
            @RequestParam Long courseId,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 1) {
            return ApiResult.error(403, "仅学生可查看作业列表");
        }

        List<AssignmentStudentListVO> response = assignmentService.getStudentAssignmentList(courseId, userId);
        return ApiResult.success(response);
    }

    @GetMapping("/detail")
    public ApiResult<AssignmentDetailVO> getAssignmentDetail(
            @RequestParam Long assignmentId,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");

        AssignmentDetailVO response = assignmentService.getAssignmentDetail(assignmentId, userId);
        return ApiResult.success(response);
    }

    @PostMapping("/submit")
    public ApiResult<AssignmentSubmitVO> submitAssignment(
            @Valid @RequestBody AssignmentSubmitRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 1) {
            return ApiResult.error(403, "仅学生可提交作业");
        }

        AssignmentSubmitVO response = assignmentService.submitAssignment(request, userId);
        return ApiResult.success("作业提交成功", response);
    }

    @PutMapping("/grade")
    public ApiResult<AssignmentGradeVO> gradeAssignment(
            @Valid @RequestBody AssignmentGradeRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 2) {
            return ApiResult.error(403, "仅教师可批改作业");
        }

        AssignmentGradeVO response = assignmentService.gradeAssignment(request, userId);
        return ApiResult.success("批改成功", response);
    }

    @GetMapping("/teacher/list")
    public ApiResult<List<AssignmentTeacherListVO>> getTeacherAssignmentList(
            @RequestParam Long courseId,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 2) {
            return ApiResult.error(403, "仅教师可查看作业列表");
        }

        List<AssignmentTeacherListVO> response = assignmentService.getTeacherAssignmentList(courseId, userId);
        return ApiResult.success(response);
    }

    // ========== 新增：老师查看具体学生作业提交情况 ==========
    @GetMapping("/teacher/students")
    public ApiResult<AssignmentStudentGradeVO> getStudentGrades(
            @RequestParam Long assignmentId,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 2) {
            return ApiResult.error(403, "仅教师可查看");
        }

        AssignmentStudentGradeVO response = assignmentService.getStudentGrades(assignmentId, userId);
        return ApiResult.success(response);
    }
}