package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.vo.AdminUserCourseVO;
import com.jycz.qingyun.service.CourseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/qingyun/course")
@RequiredArgsConstructor
public class CourseAdminController {

    private final CourseService courseService;

    /**
     * 管理员查看指定用户的所有课程
     * GET /qingyun/course/admin/user-courses?userId=2
     */
    @GetMapping("/admin/user-courses")
    public ApiResult<List<AdminUserCourseVO>> getAdminUserCourses(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest httpRequest) {

        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 3) {
            return ApiResult.error(403, "仅管理员可查看");
        }

        List<AdminUserCourseVO> response = courseService.getAdminUserCourses(userId, pageNum, pageSize);
        return ApiResult.success(response);
    }
}