package com.jycz.qingyun.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckinSubmitRequest {

    @NotNull(message = "课堂ID不能为空")
    private Long classId;
}