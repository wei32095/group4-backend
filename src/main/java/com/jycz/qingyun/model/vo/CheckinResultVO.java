package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CheckinResultVO {//

    private Long classId;
    private String classTitle;
    private Integer totalStudents;
    private Integer normalCount;
    private Integer lateCount;
    private Integer absentCount;
    private List<CheckinRecordVO> records;

    @Data
    @Builder
    public static class CheckinRecordVO {
        private Long userId;
        private String studentName;
        private Integer checkStatus;
        private LocalDateTime checkinTime;
    }
}