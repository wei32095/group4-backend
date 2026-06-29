package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.vo.AnalysisReportVO;
import com.jycz.qingyun.service.AnalysisService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/qingyun/analysis")
public class AnalysisController {

    @Autowired
    private AnalysisService analysisService;

    @GetMapping("/report")
    public ApiResult<AnalysisReportVO> getReport(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        AnalysisReportVO vo = analysisService.getReport(userId);
        return ApiResult.success(vo);
    }
}
