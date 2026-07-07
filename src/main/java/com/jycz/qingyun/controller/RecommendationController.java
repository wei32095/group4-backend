package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.dto.RecommendationSubmitRequest;
import com.jycz.qingyun.model.vo.RecommendationQuestionVO;
import com.jycz.qingyun.model.vo.RecommendationSubmitVO;
import com.jycz.qingyun.service.RecommendationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/qingyun/recommendation")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * 获取推荐习题（单题）
     * GET /qingyun/recommendation/question?weakPointId=1
     */
    @GetMapping("/question")
    public ApiResult<RecommendationQuestionVO> getRecommendationQuestion(
            @RequestParam Long weakPointId,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 1) {
            return ApiResult.error(403, "仅学生可查看");
        }

        RecommendationQuestionVO response = recommendationService.getRecommendationQuestion(weakPointId, userId);
        return ApiResult.success(response);
    }

    /**
     * 提交推荐习题答案
     * POST /qingyun/recommendation/submit
     */
    @PostMapping("/submit")
    public ApiResult<RecommendationSubmitVO> submitRecommendation(
            @Valid @RequestBody RecommendationSubmitRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 1) {
            return ApiResult.error(403, "仅学生可操作");
        }

        RecommendationSubmitVO response = recommendationService.submitRecommendation(request, userId);
        return ApiResult.success(response);
    }
}