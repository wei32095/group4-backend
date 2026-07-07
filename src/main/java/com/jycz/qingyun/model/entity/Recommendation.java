package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("recommendation")
public class Recommendation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long assignmentId;

    private String questions;

    private Integer status;      // 0-待练习，1-已完成

    private Long parentId;       // 新增：父推荐ID

    private Integer isCompleted; // 新增：0-进行中，1-全部正确完成

}