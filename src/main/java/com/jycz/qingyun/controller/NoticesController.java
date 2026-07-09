package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.vo.NoticeListVO;
import com.jycz.qingyun.service.NoticeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/qingyun/notices")
public class NoticesController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping
    public ApiResult<NoticeListVO> getNotices(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        return noticeService.getNotices(userId, page, size);
    }

    @PutMapping("/read/{noticeId}")
    public ApiResult<Void> markRead(@PathVariable Long noticeId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        noticeService.markRead(noticeId, userId);
        return ApiResult.success();
    }

    @PutMapping("/read-all")
    public ApiResult<Void> markAllRead(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        noticeService.markAllRead(userId);
        return ApiResult.success();
    }

    @DeleteMapping("/{noticeId}")
    public ApiResult<Void> deleteNotice(@PathVariable Long noticeId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        noticeService.deleteNotice(noticeId, userId);
        return ApiResult.success();
    }
}
