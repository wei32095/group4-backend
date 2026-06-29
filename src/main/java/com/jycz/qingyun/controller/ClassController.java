package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.dto.ClassCreateRequest;
import com.jycz.qingyun.model.vo.ClassCreateVO;
import com.jycz.qingyun.model.vo.ClassStudentVO;
import com.jycz.qingyun.service.ClassService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/qingyun/class")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;

    /**
     * 老师发起课堂
     * POST /api/v1/class/create
     */
    @PostMapping("/create")
    public ApiResult<ClassCreateVO> createClass(
            @Valid @RequestBody ClassCreateRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 2) {
            return ApiResult.error(403, "仅教师可创建课堂");
        }

        ClassCreateVO response = classService.createClass(request, userId);
        return ApiResult.success("课堂创建成功", response);
    }

    /**
     * 老师结束课堂
     * PUT /api/v1/class/end?classId={classId}
     */
    @PutMapping("/end")
    public ApiResult<Void> endClass(
            @RequestParam Long classId,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 2) {
            return ApiResult.error(403, "仅教师可结束课堂");
        }

        classService.endClass(classId, userId);
        return ApiResult.success();  // ← 无参调用
    }

    /**
     * 学生查看课堂列表
     * GET /api/v1/class/student/list?courseId={courseId}
     */
    @GetMapping("/student/list")
    public ApiResult<List<ClassStudentVO>> getStudentClassList(
            @RequestParam Long courseId,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 1) {
            return ApiResult.error(403, "仅学生可查看课堂列表");
        }

        List<ClassStudentVO> response = classService.getStudentClassList(courseId, userId);
        return ApiResult.success(response);
    }
}