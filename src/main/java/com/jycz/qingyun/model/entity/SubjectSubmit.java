package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("subject_submit")
public class SubjectSubmit {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long assignmentId;

    private Long userId;

    private Long questionId;

    private String answerPicture;

    private Integer subjectScore;

    private String teacherComment;

    private Integer finishStatus;

    private Integer gradingStatus;

    private LocalDateTime finishTime;

    private LocalDateTime gradingTime;
}