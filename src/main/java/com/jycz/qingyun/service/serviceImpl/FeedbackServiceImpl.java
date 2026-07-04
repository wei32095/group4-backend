package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jycz.qingyun.mapper.FeedbackMapper;
import com.jycz.qingyun.mapper.UserMapper;
import com.jycz.qingyun.model.entity.Feedback;
import com.jycz.qingyun.model.entity.User;
import com.jycz.qingyun.model.vo.FeedbackDetailVO;
import com.jycz.qingyun.model.vo.FeedbackListVO;
import com.jycz.qingyun.model.vo.FeedbackVO;
import com.jycz.qingyun.service.FeedbackService;
import com.jycz.qingyun.service.NoticeService;
import com.jycz.qingyun.utils.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NoticeService noticeService;

    @Override
    public void submit(Long userId, String content) {
        Feedback feedback = new Feedback();
        feedback.setUserId(userId);
        feedback.setContent(content);
        feedback.setStatus(0);
        feedbackMapper.insert(feedback);
    }

    @Override
    public FeedbackListVO getFeedbackList(Integer pageNum, Integer pageSize) {
        Page<Feedback> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<Feedback>()
                .orderByDesc(Feedback::getCreatedAt);

        IPage<Feedback> feedbackPage = feedbackMapper.selectPage(page, wrapper);

        // 批量查询提交人信息
        List<Long> userIds = feedbackPage.getRecords().stream()
                .map(Feedback::getUserId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, User> userMap = userIds.isEmpty() ? Map.of() :
                userMapper.selectList(
                        new LambdaQueryWrapper<User>().in(User::getId, userIds)
                ).stream().collect(Collectors.toMap(User::getId, u -> u));

        // 转换 VO
        List<FeedbackVO> records = new ArrayList<>();
        for (Feedback fb : feedbackPage.getRecords()) {
            FeedbackVO vo = new FeedbackVO();
            vo.setId(fb.getId());
            vo.setUserId(fb.getUserId());
            User submitter = userMap.get(fb.getUserId());
            vo.setUserName(submitter != null ? submitter.getName() : "未知用户");
            vo.setUserRole(submitter != null ? submitter.getRole() : null);
            vo.setContent(fb.getContent());
            vo.setStatus(fb.getStatus());
            vo.setCreatedAt(fb.getCreatedAt());
            records.add(vo);
        }

        FeedbackListVO result = new FeedbackListVO();
        result.setRecords(records);
        result.setTotal(feedbackPage.getTotal());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setPages((int) feedbackPage.getPages());
        return result;
    }

    @Override
    public FeedbackDetailVO getFeedbackDetail(Long id) {
        Feedback fb = feedbackMapper.selectById(id);
        if (fb == null) {
            throw new BusinessException(404, "反馈不存在");
        }

        User submitter = userMapper.selectById(fb.getUserId());

        FeedbackDetailVO vo = new FeedbackDetailVO();
        vo.setId(fb.getId());
        vo.setUserId(fb.getUserId());
        vo.setUserName(submitter != null ? submitter.getName() : "未知用户");
        vo.setUserRole(submitter != null ? submitter.getRole() : null);
        vo.setContent(fb.getContent());
        vo.setStatus(fb.getStatus());
        vo.setReplyContent(fb.getReplyContent());
        vo.setReplyTime(fb.getReplyTime());
        vo.setCreatedAt(fb.getCreatedAt());
        return vo;
    }

    @Override
    @Transactional
    public void replyFeedback(Long id, String replyContent) {
        Feedback fb = feedbackMapper.selectById(id);
        if (fb == null) {
            throw new BusinessException(404, "反馈不存在");
        }
        if (fb.getStatus() == 1) {
            throw new BusinessException(400, "该反馈已回复");
        }

        fb.setReplyContent(replyContent);
        fb.setReplyTime(LocalDateTime.now());
        fb.setStatus(1);
        feedbackMapper.updateById(fb);

        // 发送通知给提交反馈的用户
        noticeService.addNotice(fb.getUserId(), "反馈回复",
                "您提交的反馈已收到回复：" + replyContent, 0);
    }
}
