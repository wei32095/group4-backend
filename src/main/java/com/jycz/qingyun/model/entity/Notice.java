package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("notice")
public class Notice {
    private Long id;
    private Long userId;
    private String noticeTitle;
    private String noticeContent;
    private Integer noticeStatus;
    private Integer noticeType;
    private LocalDateTime pushTime;
}
