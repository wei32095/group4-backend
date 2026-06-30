package com.jycz.qingyun.service;

import com.jycz.qingyun.model.vo.PointsRecordListVO;

public interface PointsRecordService {
    PointsRecordListVO getRecords(Long userId, int page, int size);

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
}
