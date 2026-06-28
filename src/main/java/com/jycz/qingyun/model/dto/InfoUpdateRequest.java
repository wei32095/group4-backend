package com.jycz.qingyun.model.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class InfoUpdateRequest {

    @Size(max = 50, message = "姓名最长不超过50字")
    private String name;

    private String phone;

    private String avatar;

    private String bio;
}
