package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.vo.StudentAnalysisVO;
import com.jycz.qingyun.service.AnalysisService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/qingyun/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    /**
     * 学生学情分析
     * GET /qingyun/analysis/student?periodType=week
     */
    @GetMapping("/student")
    public ApiResult<StudentAnalysisVO> getStudentAnalysis(
            @RequestParam(defaultValue = "week") String periodType,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 1) {
            return ApiResult.error(403, "仅学生可查看学情分析");
        }

        StudentAnalysisVO response = analysisService.getStudentAnalysis(userId, periodType);
        return ApiResult.success(response);
    }
}