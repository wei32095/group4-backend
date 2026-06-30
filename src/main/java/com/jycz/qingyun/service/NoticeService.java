package com.jycz.qingyun.service;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.vo.NoticeListVO;

public interface NoticeService {
    ApiResult<NoticeListVO> getNotices(Long userId, int page, int size);

    /**
     * 写入一条通知
     * @param userId        接收通知的用户
     * @param noticeTitle   通知标题
     * @param noticeContent 通知内容
     * @param noticeType    通知类型：0-通用，1-上课提醒，2-作业发布，3-作业提交，4-批改完成
     */
    void addNotice(Long userId, String noticeTitle, String noticeContent, Integer noticeType);

    /** 标记单条通知为已读 */
    void markRead(Long noticeId, Long userId);

    /** 标记当前用户所有通知为已读 */
    void markAllRead(Long userId);
}
