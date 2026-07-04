package com.jycz.qingyun.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AdminNoticeListVO {
    private List<AdminNoticeVO> records;
    private long total;
    private int pageNum;
    private int pageSize;
    private int pages;

    @Data
    public static class AdminNoticeVO {
        private Long id;
        private String noticeTitle;
        private String noticeContent;
        private Integer targetRole;
        private String targetRoleName;
        private Integer recipientCount;
        private LocalDateTime pushTime;
    }
}
