package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.vo.WeakPointVO;
import com.jycz.qingyun.service.WeakPointService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/qingyun/weak-points")
@RequiredArgsConstructor
public class WeakPointController {

    private final WeakPointService weakPointService;

    /**
     * 获取薄弱知识点列表（支持按课程/作业筛选）
     * GET /qingyun/weak-points/list?courseId=8&assignmentId=21
     */
    @GetMapping("/list")
    public ApiResult<List<WeakPointVO>> getWeakPointsList(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long assignmentId,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 1) {
            return ApiResult.error(403, "仅学生可查看");
        }

        List<WeakPointVO> response = weakPointService.getWeakPointsList(userId, courseId, assignmentId);
        return ApiResult.success(response);
    }
}