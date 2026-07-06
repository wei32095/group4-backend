package com.jycz.qingyun.service;

import com.jycz.qingyun.model.dto.RecommendationSubmitRequest;
import com.jycz.qingyun.model.vo.RecommendationListVO;
import com.jycz.qingyun.model.vo.RecommendationSubmitVO;

import java.util.List;

public interface RecommendationService {

    /** 获取推荐习题列表 */
    List<RecommendationListVO> getRecommendationList(Long userId);

    /** 提交推荐习题练习结果 */
    RecommendationSubmitVO submitRecommendation(RecommendationSubmitRequest request, Long userId);
}