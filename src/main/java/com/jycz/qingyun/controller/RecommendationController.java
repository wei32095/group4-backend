package com.jycz.qingyun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.entity.Recommendation;
import com.jycz.qingyun.mapper.RecommendationMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/qingyun/recommendation")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationMapper recommendationMapper;
    private final ObjectMapper objectMapper;

    /**
     * 获取智能推荐习题
     * GET /qingyun/recommendation/list?assignmentId=1
     */
    @GetMapping("/list")
    public ApiResult<List<Map<String, Object>>> getRecommendations(
            @RequestParam Long assignmentId,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");

        // 查询最新的未完成推荐记录
        LambdaQueryWrapper<Recommendation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Recommendation::getUserId, userId)
                .eq(Recommendation::getAssignmentId, assignmentId)
                .eq(Recommendation::getStatus, 0)
                .orderByDesc(Recommendation::getCreatedAt)
                .last("LIMIT 1");

        Recommendation rec = recommendationMapper.selectOne(wrapper);

        if (rec == null) {
            return ApiResult.error(404, "暂无推荐习题");
        }

        try {
            List<Map<String, Object>> questions = objectMapper.readValue(
                    rec.getQuestions(), List.class);
            return ApiResult.success(questions);
        } catch (Exception e) {
            log.error("解析推荐习题失败: {}", e.getMessage());
            return ApiResult.error(500, "解析推荐习题失败");
        }
    }

    /**
     * 标记推荐习题已完成
     * PUT /qingyun/recommendation/complete
     */
    @PutMapping("/complete")
    public ApiResult<Void> completeRecommendation(
            @RequestParam Long recommendationId,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");

        Recommendation rec = recommendationMapper.selectById(recommendationId);
        if (rec == null) {
            return ApiResult.error(404, "推荐记录不存在");
        }

        if (!rec.getUserId().equals(userId)) {
            return ApiResult.error(403, "无权操作");
        }

        if (rec.getStatus() == 1) {
            return ApiResult.success("已完成", null);
        }

        rec.setStatus(1);
        recommendationMapper.updateById(rec);

        return ApiResult.success("练习完成", null);
    }
}