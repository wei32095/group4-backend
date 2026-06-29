package com.jycz.qingyun.model.vo;

import lombok.Data;
import java.util.List;

@Data
public class NoticeListVO {
    private List<NoticeVO> location;
    private long total;
    private int pageNum;
    private int pageSize;
    private int pages;
    private long unreadCount;
    private long pendingGradeCount;
}
