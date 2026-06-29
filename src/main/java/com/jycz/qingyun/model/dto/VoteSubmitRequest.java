package com.jycz.qingyun.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VoteSubmitRequest {

    @NotNull(message = "投票ID不能为空")
    private Long voteId;

    @NotBlank(message = "请选择选项")
    private String selectedOption;
}