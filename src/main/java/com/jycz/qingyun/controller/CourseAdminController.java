package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.vo.AdminUserCourseVO;
import com.jycz.qingyun.model.vo.CourseAdminListVO;
import com.jycz.qingyun.model.vo.CoursePendingVO;
import com.jycz.qingyun.service.CourseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

    @GetMapping("/admin/pending")
    public ApiResult<List<CoursePendingVO>> getPendingCourseList(
            HttpServletRequest httpRequest) {

        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 3) {
            return ApiResult.error(403, "仅管理员可查看");
        }

        List<CoursePendingVO> response = courseService.getPendingCourseList();
        return ApiResult.success(response);
    }

    /**
     * 管理员查询课程列表（模糊查询 + 筛选）
     * GET /qingyun/course/admin/list?keyword=张&filterStatus=active&pageNum=1&pageSize=10
     */
    @GetMapping("/admin/list")
    public ApiResult<Map<String, Object>> getAdminCourseList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String filterStatus,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest httpRequest) {

        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 3) {
            return ApiResult.error(403, "仅管理员可查看");
        }

        Map<String, Object> response = courseService.getAdminCourseList(keyword, filterStatus, pageNum, pageSize);
        return ApiResult.success(response);
    }
}