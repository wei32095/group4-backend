package com.jycz.qingyun.service;

import com.jycz.qingyun.model.vo.PointsRecordListVO;

public interface PointsRecordService {
    PointsRecordListVO getRecords(Long userId);

    /**
     * 加分
     * @param userId    用户ID
     * @param points    加分值（正数）
     * @param sourceType 来源类型：1-签到 2-投票 3-作业 4-自习 5-道具
     */
    void addPoints(Long userId, int points, int sourceType);

    /**
     * 扣分
     * @param userId    用户ID
     * @param points    扣分值（正数）
     * @param sourceType 来源类型
     * @throws RuntimeException 积分不足时抛出
     */
    void deductPoints(Long userId, int points, int sourceType);
    /**
     * 签到积分处理
     * @param userId 用户ID
     * @param checkStatus 签到状态：1-正常，2-迟到，3-缺勤
     */
    void handleCheckinPoints(Long userId, Integer checkStatus);

    /**
     * 投票正确加分
     */
    void handleVoteCorrectPoints(Long userId);

    /**
     * 作业批改积分（成绩 ÷ 5，保留整数）
     */
    void handleAssignmentGradePoints(Long userId, Integer score);

    /**
     * 问题被老师回复加分
     */
    void handleProblemRepliedPoints(Long userId);
    /**
     * 推荐习题全部正确加分
     */
    void handleRecommendationPoints(Long userId);

}
