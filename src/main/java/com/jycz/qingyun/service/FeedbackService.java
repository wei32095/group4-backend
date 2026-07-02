package com.jycz.qingyun.service;

import com.jycz.qingyun.model.vo.FeedbackDetailVO;
import com.jycz.qingyun.model.vo.FeedbackListVO;

public interface FeedbackService {

    /**
     * 提交反馈（学生/老师）
     */
    void submit(Long userId, String content);

    /**
     * 管理员查看反馈列表（分页，按创建时间倒序）
     */
    FeedbackListVO getFeedbackList(Integer pageNum, Integer pageSize);

    /**
     * 管理员查看单条反馈详情（含提交人信息、回复内容）
     */
    FeedbackDetailVO getFeedbackDetail(Long id);

    /**
     * 管理员回复反馈
     */
    void replyFeedback(Long id, String replyContent);
}
