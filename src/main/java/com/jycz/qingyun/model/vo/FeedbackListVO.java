package com.jycz.qingyun.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 反馈列表 - 分页包装
 */
@Data
public class FeedbackListVO {
    private List<FeedbackVO> records;
    private long total;
    private int pageNum;
    private int pageSize;
    private int pages;
}
