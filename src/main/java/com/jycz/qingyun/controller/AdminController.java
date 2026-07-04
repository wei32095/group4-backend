package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.AdminNoticePublishRequest;
import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.dto.FeedbackReplyRequest;
import com.jycz.qingyun.model.vo.AdminDashboardVO;
import com.jycz.qingyun.model.vo.AdminNoticeListVO;
import com.jycz.qingyun.model.vo.AdminUserListVO;
import com.jycz.qingyun.model.vo.FeedbackDetailVO;
import com.jycz.qingyun.model.vo.FeedbackListVO;
import com.jycz.qingyun.service.FeedbackService;
import com.jycz.qingyun.service.NoticeService;
import com.jycz.qingyun.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/qingyun/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final NoticeService noticeService;
    private final FeedbackService feedbackService;

    /**
     * 管理员查看用户列表（分页）
     * GET /qingyun/admin/users?pageNum=1&pageSize=10&keyword=&role=&status=
     */
    @GetMapping("/users")
    public ApiResult<AdminUserListVO> getUsers(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer role,
            @RequestParam(required = false) Integer status,
            HttpServletRequest httpRequest) {

        Integer myRole = (Integer) httpRequest.getAttribute("role");
        if (myRole == null || myRole != 3) {
            return ApiResult.error(403, "仅管理员可查看");
        }

        AdminUserListVO vo = userService.getAdminUserList(pageNum, pageSize, keyword, role, status);
        return ApiResult.success(vo);
    }

    /**
     * 管理员看板数据
     * GET /qingyun/admin/dashboard
     */
    @GetMapping("/dashboard")
    public ApiResult<AdminDashboardVO> getDashboard(HttpServletRequest httpRequest) {
        Integer role = (Integer) httpRequest.getAttribute("role");
        if (role == null || role != 3) {
            return ApiResult.error(403, "仅管理员可查看");
        }

        AdminDashboardVO vo = userService.getDashboard();
        return ApiResult.success(vo);
    }

    /**
     * 管理员发布通知
     * POST /qingyun/admin/notices
     */
    @PostMapping("/notices")
    public ApiResult<Void> publishNotice(
            @Valid @RequestBody AdminNoticePublishRequest request,
            HttpServletRequest httpRequest) {

        Integer role = (Integer) httpRequest.getAttribute("role");
        if (role == null || role != 3) {
            return ApiResult.error(403, "仅管理员可操作");
        }

        int count = noticeService.publishAdminNotice(
                request.getNoticeTitle(),
                request.getNoticeContent(),
                request.getTargetRole()
        );

        return ApiResult.success("通知已推送给 " + count + " 人", null);
    }

    /**
     * 管理员查看已发布通知列表（分页）
     * GET /qingyun/admin/view_notices?pageNum=1&pageSize=10
     */
    @GetMapping("/view_notices")
    public ApiResult<AdminNoticeListVO> getNotices(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest httpRequest) {

        Integer role = (Integer) httpRequest.getAttribute("role");
        if (role == null || role != 3) {
            return ApiResult.error(403, "仅管理员可查看");
        }

        return noticeService.getPublishedNotices(pageNum, pageSize);
    }

    /**
     * 管理员查看反馈列表（分页）
     * GET /qingyun/admin/feedbacks?pageNum=1&pageSize=10
     */
    @GetMapping("/feedback")
    public ApiResult<FeedbackListVO> getFeedbacks(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest httpRequest) {

        Integer role = (Integer) httpRequest.getAttribute("role");
        if (role == null || role != 3) {
            return ApiResult.error(403, "仅管理员可查看");
        }

        FeedbackListVO vo = feedbackService.getFeedbackList(pageNum, pageSize);
        return ApiResult.success(vo);
    }

    /**
     * 管理员查看单条反馈详情
     * GET /qingyun/admin/feedbacks/{id}
     */
    @GetMapping("/feedback/{id}")
    public ApiResult<FeedbackDetailVO> getFeedbackDetail(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        Integer role = (Integer) httpRequest.getAttribute("role");
        if (role == null || role != 3) {
            return ApiResult.error(403, "仅管理员可查看");
        }

        FeedbackDetailVO vo = feedbackService.getFeedbackDetail(id);
        return ApiResult.success(vo);
    }

    /**
     * 管理员回复反馈
     * PUT /qingyun/admin/feedback/reply/{id}
     */
    @PutMapping("/feedback/reply/{id}")
    public ApiResult<Void> replyFeedback(
            @PathVariable Long id,
            @Valid @RequestBody FeedbackReplyRequest request,
            HttpServletRequest httpRequest) {

        Integer role = (Integer) httpRequest.getAttribute("role");
        if (role == null || role != 3) {
            return ApiResult.error(403, "仅管理员可操作");
        }

        feedbackService.replyFeedback(id, request.getReplyContent());
        log.info("管理员回复反馈: feedbackId={}", id);
        return ApiResult.success("回复成功", null);
    }
}
