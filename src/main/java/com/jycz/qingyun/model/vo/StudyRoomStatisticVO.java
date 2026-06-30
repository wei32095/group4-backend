package com.jycz.qingyun.model.vo;

import lombok.Data;

@Data
public class StudyRoomStatisticVO {
    private Integer weekStudyDuration;   // 本周有效学习时长（秒）
    private Integer monthStudyDuration;  // 本月有效学习时长（秒）
    private Integer weekValidCount;      // 本周有效学习次数
    private Integer monthValidCount;     // 本月有效学习次数
}
