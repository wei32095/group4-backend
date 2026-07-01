package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CheckinSubmitVO {//

    private Long classId;
    private String classTitle;
    private String checkStatus;
    private LocalDateTime checkinTime;
}