package com.jycz.qingyun.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class VoteCreateRequest {

    @NotNull(message = "课堂ID不能为空")
    private Long classId;

    @NotBlank(message = "投票题目不能为空")
    private String heading;

    @NotNull(message = "选项不能为空")
    private List<String> options;

    private String correctOption;

    private Integer duration;
}