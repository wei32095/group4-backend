package com.jycz.qingyun.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class FlowerListVO {
    private List<FlowerVO> location;
    private long total;
    private int pageNum;
    private int pageSize;
    private int pages;
}
