package com.jycz.qingyun.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class PointsRecordListVO {
    private Integer currentPoints;
    private List<PointsRecordVO> records;
}
