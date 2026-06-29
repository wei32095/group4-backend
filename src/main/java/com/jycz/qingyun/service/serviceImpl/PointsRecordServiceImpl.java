package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jycz.qingyun.mapper.PointsRecordMapper;
import com.jycz.qingyun.model.entity.PointsRecord;
import com.jycz.qingyun.model.vo.PointsRecordListVO;
import com.jycz.qingyun.model.vo.PointsRecordVO;
import com.jycz.qingyun.service.PointsRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PointsRecordServiceImpl implements PointsRecordService {

    @Autowired
    private PointsRecordMapper pointsRecordMapper;

    @Override
    public PointsRecordListVO getRecords(Long userId, int page, int size) {
        // 1. 查询当前总积分
        Integer currentPoints = pointsRecordMapper.getLatestPoints(userId);
        if (currentPoints == null) {
            currentPoints = 0;
        }

        // 2. 分页查询流水，按时间倒序
        Page<PointsRecord> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<PointsRecord> wrapper = new LambdaQueryWrapper<PointsRecord>()
                .eq(PointsRecord::getUserId, userId)
                .orderByDesc(PointsRecord::getChangeTime);

        Page<PointsRecord> result = pointsRecordMapper.selectPage(pageObj, wrapper);

        // 3. 转换 VO
        List<PointsRecordVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        // 4. 组装
        PointsRecordListVO listVO = new PointsRecordListVO();
        listVO.setCurrentPoints(currentPoints);
        listVO.setLocation(voList);
        listVO.setTotal(result.getTotal());
        listVO.setPageNum((int) result.getCurrent());
        listVO.setPageSize((int) result.getSize());
        listVO.setPages((int) result.getPages());
        return listVO;
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
