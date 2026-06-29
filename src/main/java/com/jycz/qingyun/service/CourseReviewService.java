package com.jycz.qingyun.service;

import com.jycz.qingyun.model.dto.CourseReviewSubmitRequest;
import com.jycz.qingyun.model.vo.CourseReviewSummaryVO;
import com.jycz.qingyun.model.vo.CourseReviewVO;

import java.util.List;

public interface CourseReviewService {

    /**
     * 提交/更新课程评价
     */
    CourseReviewVO submitReview(CourseReviewSubmitRequest request, Long userId);

    /**
     * 获取课程评价列表
     */
    List<CourseReviewVO> getReviewList(Long courseId, Integer star, Integer pageNum, Integer pageSize);

    /**
     * 获取课程评价总数
     */
    Long countReviews(Long courseId, Integer star);

    /**
     * 获取课程评价概览（平均分、星级统计）
     */
    CourseReviewSummaryVO getReviewSummary(Long courseId);
}