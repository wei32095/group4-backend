package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("vote_record")
public class VoteRecord {//

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long voteId;

    private Long userId;

    private String selectedOption;

    private Integer isCorrect;

    private LocalDateTime submittedAt;
}