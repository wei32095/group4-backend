package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("notice_publish")
public class NoticePublish {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String noticeTitle;
    private String noticeContent;
    private Integer targetRole;
    private Integer recipientCount;
    private LocalDateTime pushTime;
}
