package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.dto.CourseReviewSubmitRequest;
import com.jycz.qingyun.model.vo.CourseReviewSummaryVO;
import com.jycz.qingyun.model.vo.CourseReviewVO;
import com.jycz.qingyun.service.CourseReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/qingyun/review")
@RequiredArgsConstructor
public class CourseReviewController {

    private final CourseReviewService courseReviewService;

    /**
     * 提交/更新课程评价
     */
    @PostMapping("/submit")
    public ApiResult<CourseReviewVO> submitReview(
            @Valid @RequestBody CourseReviewSubmitRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        // 只有学生可以评价
        if (role == null || role != 1) {
            return ApiResult.error(403, "仅学生可评价课程");
        }

        CourseReviewVO response = courseReviewService.submitReview(request, userId);
        return ApiResult.success(response);
    }

    /**
     * 查看课程评价列表（支持按星级筛选）
     */
    @GetMapping("/list")
    public ApiResult<Object> getReviewList(
            @RequestParam Long courseId,
            @RequestParam(required = false) Integer star,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        // 获取评价列表
        List<CourseReviewVO> list = courseReviewService.getReviewList(courseId, star, pageNum, pageSize);
        Long total = courseReviewService.countReviews(courseId, star);
        CourseReviewSummaryVO summary = courseReviewService.getReviewSummary(courseId);

        // 计算总页数
        int pages = (int) Math.ceil((double) total / pageSize);

        // 使用 Map 作为结果
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("pages", pages);
        result.put("avgStar", summary.getAvgStar());
        result.put("starStats", summary.getStarStats());
        result.put("list", list);

        return ApiResult.success(result);
    }

    /**
     * 获取课程评价概览（平均分、星级统计）
     */
    @GetMapping("/summary")
    public ApiResult<CourseReviewSummaryVO> getReviewSummary(
            @RequestParam Long courseId) {

        CourseReviewSummaryVO response = courseReviewService.getReviewSummary(courseId);
        return ApiResult.success(response);
    }
}