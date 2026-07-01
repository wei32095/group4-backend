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
        return ApiResult.success();
    }

    @GetMapping("/list")
    public ApiResult<List<ClassStudentVO>> getClassList(
            @RequestParam Long courseId,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || (role != 1 && role != 2)) {
            return ApiResult.error(403, "无权查看课堂列表");
        }

        List<ClassStudentVO> response = classService.getClassList(courseId, userId, role);
        return ApiResult.success(response);
    }
}