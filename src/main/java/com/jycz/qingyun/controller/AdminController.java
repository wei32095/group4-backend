package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.AdminNoticePublishRequest;
import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.vo.AdminDashboardVO;
import com.jycz.qingyun.model.vo.AdminUserListVO;
import com.jycz.qingyun.service.NoticeService;
import com.jycz.qingyun.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    /**
     * 管理员查看用户列表（分页）
     * GET /qingyun/admin/users?pageNum=1&pageSize=10
     */
    @GetMapping("/users")
    public ApiResult<AdminUserListVO> getUsers(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest httpRequest) {

        Integer role = (Integer) httpRequest.getAttribute("role");
        if (role == null || role != 3) {
            return ApiResult.error(403, "仅管理员可查看");
        }

        AdminUserListVO vo = userService.getAdminUserList(pageNum, pageSize);
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
}
