package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.dto.FeedbackSubmitRequest;
import com.jycz.qingyun.service.FeedbackService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/qingyun/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    /**
     * 提交反馈（学生/老师）
     * POST /qingyun/feedback
     */
    @PostMapping
    public ApiResult<Void> submit(
            @Valid @RequestBody FeedbackSubmitRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        feedbackService.submit(userId, request.getContent());
        log.info("用户提交反馈: userId={}", userId);
        return ApiResult.success("提交成功", null);
    }
}
