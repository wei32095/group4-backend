package com.jycz.qingyun.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 管理员发布通知 - 请求体
 */
@Data
public class AdminNoticePublishRequest {

    @NotBlank(message = "通知标题不能为空")
    @Size(max = 100, message = "通知标题最长100字")
    private String noticeTitle;

    @NotBlank(message = "通知内容不能为空")
    private String noticeContent;

    /**
     * 发布范围：1-学生，2-教师，null-全部用户
     */
    private Integer targetRole;
}
