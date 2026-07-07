package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;


@Data
@TableName("recommendation")
public class Recommendation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long assignmentId;

    private Long weakPointId;     // 关联薄弱知识点ID

    private String question;      // 单个题目（JSON对象）

    private Integer isCorrect;    // 0-未答，1-正确，2-错误

}