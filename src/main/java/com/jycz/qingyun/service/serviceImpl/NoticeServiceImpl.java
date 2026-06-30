package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jycz.qingyun.mapper.NoticeMapper;
import com.jycz.qingyun.mapper.CourseStudentMapper;
import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.entity.Notice;
import com.jycz.qingyun.model.entity.CourseStudent;
import com.jycz.qingyun.model.vo.NoticeListVO;
import com.jycz.qingyun.model.vo.NoticeVO;
import com.jycz.qingyun.service.NoticeService;
import com.jycz.qingyun.utils.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private CourseStudentMapper courseStudentMapper;

    // ========== 原有方法 ==========

    @Override
    public ApiResult<NoticeListVO> getNotices(Long userId, int page, int size) {
        Page<Notice> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<Notice>()
                .eq(Notice::getUserId, userId)
                .orderByDesc(Notice::getPushTime);

        Page<Notice> result = noticeMapper.selectPage(pageObj, wrapper);

        List<NoticeVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        long unreadCount = noticeMapper.countUnread(userId);
        long pendingGradeCount = noticeMapper.countPendingGrade(userId);

        NoticeListVO listVO = new NoticeListVO();
        listVO.setLocation(voList);
        listVO.setTotal(result.getTotal());
        listVO.setPageNum((int) result.getCurrent());
        listVO.setPageSize((int) result.getSize());
        listVO.setPages((int) result.getPages());
        listVO.setUnreadCount(unreadCount);
        listVO.setPendingGradeCount(pendingGradeCount);

        return ApiResult.success(listVO);
    }

    @Override
    public void addNotice(Long userId, String noticeTitle, String noticeContent, Integer noticeType) {
        Notice notice = new Notice();
        notice.setUserId(userId);
        notice.setNoticeTitle(noticeTitle);
        notice.setNoticeContent(noticeContent);
        notice.setNoticeStatus(0);
        notice.setNoticeType(noticeType);
        notice.setPushTime(LocalDateTime.now());
        noticeMapper.insert(notice);
    }

    @Override
    public void markRead(Long noticeId, Long userId) {
        Notice notice = noticeMapper.selectById(noticeId);
        if (notice == null) return;

        if (!notice.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作该通知");
        }

        if (notice.getNoticeStatus() == 1) return;

        notice.setNoticeStatus(1);
        noticeMapper.updateById(notice);
    }

    @Override
    public void markAllRead(Long userId) {
        Notice update = new Notice();
        update.setNoticeStatus(1);

        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<Notice>()
                .eq(Notice::getUserId, userId)
                .eq(Notice::getNoticeStatus, 0);

        noticeMapper.update(update, wrapper);
    }

    private NoticeVO toVO(Notice notice) {
        NoticeVO vo = new NoticeVO();
        vo.setId(notice.getId());
        vo.setNoticeTitle(notice.getNoticeTitle());
        vo.setNoticeContent(notice.getNoticeContent());
        vo.setNoticeStatus(notice.getNoticeStatus());
        vo.setNoticeType(notice.getNoticeType());
        vo.setPushTime(notice.getPushTime());
        return vo;
    }

    // ========== 通知触发方法 ==========

    /**
     * 批量给学生发送通知
     */
    private void batchAddNotice(List<Long> userIds, String title, String content, Integer type) {
        for (Long userId : userIds) {
            addNotice(userId, title, content, type);
        }
    }

    /**
     * 获取课程所有学生ID
     */
    private List<Long> getCourseStudentIds(Long courseId) {
        return courseStudentMapper.selectList(
                        new LambdaQueryWrapper<CourseStudent>()
                                .eq(CourseStudent::getCourseId, courseId)
                ).stream()
                .map(CourseStudent::getUserId)
                .distinct()
                .collect(Collectors.toList());
    }


    // ---------- 学生端通知 ----------

    @Override
    public void sendJoinCourseNotice(Long userId, String courseTitle) {
        String title = "加入课程成功";
        String content = "您已成功加入【" + courseTitle + "】，开始学习吧！";
        addNotice(userId, title, content, 1);
    }

    @Override
    public void sendClassStartNotice(Long courseId, String className) {
        String title = "课堂已开始";
        String content = "【" + className + "】已开始，请进入课堂参与学习！";
        List<Long> studentIds = getCourseStudentIds(courseId);
        batchAddNotice(studentIds, title, content, 2);
    }

    @Override
    public void sendAssignmentNotice(Long courseId, String assignmentTitle, String deadline) {
        String title = "新作业发布";
        String content = "【" + assignmentTitle + "】已发布，截止时间：" + deadline;
        List<Long> studentIds = getCourseStudentIds(courseId);
        batchAddNotice(studentIds, title, content, 3);
    }

    @Override
    public void sendGradeNotice(Long userId, String assignmentTitle, Integer score) {
        String title = "作业已批改";
        String content = "您的作业【" + assignmentTitle + "】已批改，得分：" + score + "分";
        addNotice(userId, title, content, 4);
    }

    @Override
    public void sendCourseEndNotice(Long courseId, String courseTitle) {
        String title = "课程已结束";
        String content = "【" + courseTitle + "】已结束，感谢您的参与！";
        List<Long> studentIds = getCourseStudentIds(courseId);
        batchAddNotice(studentIds, title, content, 5);
    }


    // ---------- 老师端通知 ----------

    @Override
    public void sendProblemNotice(Long teacherId, String studentName, String problemTitle) {
        String title = "学生发布新问题";
        String content = studentName + "发布了一个新问题：【" + problemTitle + "】，请及时查看和回复。";
        addNotice(teacherId, title, content, 6);
    }


    @Override
    public void sendSubmitNotice(Long teacherId, String studentName, String assignmentTitle) {
        String title = "学生提交作业";
        String content = studentName + "已提交作业【" + assignmentTitle + "】，请及时批改。";
        addNotice(teacherId, title, content, 8);
    }

    @Override
    public void sendAuditSuccessNotice(Long teacherId, String courseTitle) {
        String title = "课程审核通过";
        String content = "您的课程【" + courseTitle + "】已审核通过，学生可加入学习！";
        addNotice(teacherId, title, content, 9);
    }
    @Override
    public void sendProblemRepliedNotice(Long questionAuthorId, String replierName, String problemTitle) {
        String title = "你的问题有新回复";
        String content = replierName + "回复了你的问题：【" + problemTitle + "】，请及时查看。";
        addNotice(questionAuthorId, title, content, 10);
    }
}