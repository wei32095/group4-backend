package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("feedback")
public class Feedback {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String content;
    private String replyContent;
    private LocalDateTime replyTime;
    private Integer status;     // 0-待处理，1-已回复
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
