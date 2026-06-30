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

    /**
     * 发送加入课程通知（给学生）
     */
    void sendJoinCourseNotice(Long userId, String courseTitle);

    /**
     * 发送课堂开始通知（给课程所有学生）
     */
    void sendClassStartNotice(Long courseId, String className);

    /**
     * 发送作业发布通知（给课程所有学生）
     */
    void sendAssignmentNotice(Long courseId, String assignmentTitle, String deadline);

    /**
     * 发送作业批改通知（给学生）
     */
    void sendGradeNotice(Long userId, String assignmentTitle, Integer score);

    /**
     * 发送课程结束通知（给课程所有学生）
     */
    void sendCourseEndNotice(Long courseId, String courseTitle);

    /**
     * 发送学生发布问题通知（给教师）
     */
    void sendProblemNotice(Long teacherId, String studentName, String problemTitle);

    /**
     * 发送学生提交作业通知（给教师）
     */
    void sendSubmitNotice(Long teacherId, String studentName, String assignmentTitle);

    /**
     * 发送课程审核通过通知（给教师）
     */
    void sendAuditSuccessNotice(Long teacherId, String courseTitle);
    /**
     * 发送问题被回复通知（给问题发布者）
     */
    void sendProblemRepliedNotice(Long questionAuthorId, String replierName, String problemTitle);
}
