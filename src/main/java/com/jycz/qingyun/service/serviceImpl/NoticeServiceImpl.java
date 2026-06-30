package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jycz.qingyun.mapper.NoticeMapper;
import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.entity.Notice;
import com.jycz.qingyun.model.vo.NoticeListVO;
import com.jycz.qingyun.model.vo.NoticeVO;
import com.jycz.qingyun.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public ApiResult<NoticeListVO> getNotices(Long userId, int page, int size) {
        // 1. 分页查询当前用户的通知，按推送时间倒序
        Page<Notice> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<Notice>()
                .eq(Notice::getUserId, userId)
                .orderByDesc(Notice::getPushTime);

        Page<Notice> result = noticeMapper.selectPage(pageObj, wrapper);

        // 2. 转换 VO
        List<NoticeVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        // 3. 统计未读数和待批改数
        long unreadCount = noticeMapper.countUnread(userId);
        long pendingGradeCount = noticeMapper.countPendingGrade(userId);

        // 4. 组装结果
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
        notice.setNoticeStatus(0);  // 未读
        notice.setNoticeType(noticeType);
        notice.setPushTime(LocalDateTime.now());
        noticeMapper.insert(notice);
    }

    @Override
    public void markRead(Long noticeId, Long userId) {
        Notice notice = noticeMapper.selectById(noticeId);
        if (notice == null) return;  // 通知不存在，幂等忽略

        // 只能标记自己的通知
        if (!notice.getUserId().equals(userId)) {
            throw new com.jycz.qingyun.utils.BusinessException(403, "无权操作该通知");
        }

        if (notice.getNoticeStatus() == 1) return;  // 已读，幂等

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
}
