package com.jycz.qingyun.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class PointsRecordListVO {
    private Integer currentPoints;
    private List<PointsRecordVO> location;
    private long total;
    private int pageNum;
    private int pageSize;
    private int pages;
}
