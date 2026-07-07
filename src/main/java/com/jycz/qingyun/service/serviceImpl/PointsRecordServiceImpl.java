package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jycz.qingyun.mapper.PointsRecordMapper;
import com.jycz.qingyun.mapper.UserMapper;
import com.jycz.qingyun.model.entity.PointsRecord;
import com.jycz.qingyun.model.vo.PointsRecordListVO;
import com.jycz.qingyun.model.vo.PointsRecordVO;
import com.jycz.qingyun.service.PointsRecordService;
import com.jycz.qingyun.utils.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PointsRecordServiceImpl implements PointsRecordService {

    @Autowired
    private PointsRecordMapper pointsRecordMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public void addPoints(Long userId, int points, int sourceType) {
        Integer currentPoints = userMapper.getPoints(userId);
        if (currentPoints == null) {
            currentPoints = 0;
        }

        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setChangeType(1);
        record.setChangePoints(points);
        record.setLeftPoints(currentPoints + points);
        record.setSourceType(sourceType);
        record.setChangeTime(LocalDateTime.now());
        pointsRecordMapper.insert(record);

        userMapper.updatePoints(userId, points);

        log.info("加分成功: userId={}, points={}, sourceType={}, newBalance={}",
                userId, points, sourceType, currentPoints + points);
    }

    @Override
    @Transactional
    public void deductPoints(Long userId, int points, int sourceType) {
        int affected = userMapper.deductPointsIfSufficient(userId, points);
        if (affected == 0) {
            Integer currentPoints = userMapper.getPoints(userId);
            throw new BusinessException(400, "积分不足，当前积分：" + (currentPoints != null ? currentPoints : 0));
        }

        Integer newBalance = userMapper.getPoints(userId);

        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setChangeType(2);
        record.setChangePoints(points);
        record.setLeftPoints(newBalance);
        record.setSourceType(sourceType);
        record.setChangeTime(LocalDateTime.now());
        pointsRecordMapper.insert(record);

        log.info("扣分成功: userId={}, points={}, sourceType={}, newBalance={}",
                userId, points, sourceType, newBalance);
    }

    @Override
    public PointsRecordListVO getRecords(Long userId) {
        Integer currentPoints = userMapper.getPoints(userId);
        if (currentPoints == null) {
            currentPoints = 0;
        }

        LambdaQueryWrapper<PointsRecord> wrapper = new LambdaQueryWrapper<PointsRecord>()
                .eq(PointsRecord::getUserId, userId)
                .orderByDesc(PointsRecord::getChangeTime);

        List<PointsRecord> records = pointsRecordMapper.selectList(wrapper);

        List<PointsRecordVO> voList = records.stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        PointsRecordListVO listVO = new PointsRecordListVO();
        listVO.setCurrentPoints(currentPoints);
        listVO.setRecords(voList);
        return listVO;
    }

    // ========== 积分规则方法 ==========

    @Override
    @Transactional
    public void handleCheckinPoints(Long userId, Integer checkStatus) {
        if (checkStatus == 1) {
            addPoints(userId, 5, 1);
            log.info("签到成功加分: userId={}, +5分", userId);
        } else if (checkStatus == 3) {
            deductPoints(userId, 5, 1);
            log.info("缺勤扣分: userId={}, -5分", userId);
        }
    }

    @Override
    @Transactional
    public void handleVoteCorrectPoints(Long userId) {
        addPoints(userId, 5, 2);
        log.info("投票正确加分: userId={}, +5分", userId);
    }

    @Override
    @Transactional
    public void handleAssignmentGradePoints(Long userId, Integer score) {
        if (score == null || score <= 0) {
            return;
        }
        int points = score / 5;
        if (points > 0) {
            addPoints(userId, points, 3);
            log.info("作业批改加分: userId={}, score={}, points={}", userId, score, points);
        }
    }

    @Override
    @Transactional
    public void handleProblemRepliedPoints(Long userId) {
        addPoints(userId, 5, 6);
        log.info("问题被老师回复加分: userId={}, +5分", userId);
    }

    // ========== 新增：推荐习题全部正确加分 ==========
    @Override
    @Transactional
    public void handleRecommendationPoints(Long userId) {
        addPoints(userId, 5, 7);
        log.info("推荐习题全部正确加分: userId={}, +5分", userId);
    }

    private PointsRecordVO toVO(PointsRecord record) {
        PointsRecordVO vo = new PointsRecordVO();
        vo.setId(record.getId());
        vo.setUserId(record.getUserId());
        vo.setChangeType(record.getChangeType());
        vo.setChangePoints(record.getChangePoints());
        vo.setLeftPoints(record.getLeftPoints());
        vo.setSourceType(record.getSourceType());
        vo.setChangeTime(record.getChangeTime());
        return vo;
    }
}