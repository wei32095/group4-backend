package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class CourseReviewSummaryVO {
//
    private Long courseId;
    private Long totalReviews;
    private Double avgStar;
    private Map<Integer, Integer> starStats;  // {1: 3, 2: 1, 3: 5, 4: 8, 5: 12}
}