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

    @GetMapping("/admin/list")
    public ApiResult<Map<String, Object>> getAdminCourseList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer auditStatus,
            @RequestParam(required = false) String status,
            HttpServletRequest httpRequest) {

        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 3) {
            return ApiResult.error(403, "仅管理员可查看");
        }

        // 1. 获取分页数据
        List<CourseAdminListVO> list = courseService.getAdminCourseList(pageNum, pageSize, keyword, auditStatus, status);

        // 2. 计算总数（需要单独查询）
        LambdaQueryWrapper<com.jycz.qingyun.model.entity.Course> countWrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            countWrapper.like(com.jycz.qingyun.model.entity.Course::getCourseTitle, keyword);
        }
        if (auditStatus != null) {
            countWrapper.eq(com.jycz.qingyun.model.entity.Course::getAuditStatus, auditStatus);
        }
        if (status != null && !status.isEmpty()) {
            countWrapper.eq(com.jycz.qingyun.model.entity.Course::getStatus, status);
        }
        // 需要注入 courseMapper 或使用 courseService 的方法

        // 3. 组装分页结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        // 前端自己处理分页

        return ApiResult.success(result);
    }
}