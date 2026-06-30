package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.dto.CourseAuditRequest;
import com.jycz.qingyun.model.vo.CourseAuditVO;
import com.jycz.qingyun.service.CourseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/qingyun/course")
@RequiredArgsConstructor
public class CourseAuditController {

    private final CourseService courseService;

    /**
     * 管理员审核课程
     * PUT /qingyun/course/audit
     */
    @PutMapping("/audit")
    public ApiResult<CourseAuditVO> auditCourse(
            @Valid @RequestBody CourseAuditRequest request,
            HttpServletRequest httpRequest) {

        Long adminId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 3) {
            return ApiResult.error(403, "仅管理员可审核课程");
        }

        CourseAuditVO response = courseService.auditCourse(request, adminId);
        return ApiResult.success("审核操作成功", response);
    }
}