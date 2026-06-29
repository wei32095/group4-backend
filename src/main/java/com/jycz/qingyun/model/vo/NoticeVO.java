package com.jycz.qingyun.model.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NoticeVO {
    private Long id;
    private String noticeTitle;
    private String noticeContent;
    private Integer noticeStatus;
    private Integer noticeType;
    private LocalDateTime pushTime;
}
