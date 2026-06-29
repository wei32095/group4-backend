package com.jycz.qingyun.service;

import com.jycz.qingyun.model.vo.PointsRecordListVO;

public interface PointsRecordService {
    PointsRecordListVO getRecords(Long userId, int page, int size);
}
