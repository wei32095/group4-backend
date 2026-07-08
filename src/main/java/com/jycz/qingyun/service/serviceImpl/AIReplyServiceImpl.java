package com.jycz.qingyun.service.serviceImpl;

import com.jycz.qingyun.mapper.CourseProblemMapper;
import com.jycz.qingyun.mapper.CourseProblemReplyMapper;
import com.jycz.qingyun.mapper.UserMapper;
import com.jycz.qingyun.model.entity.CourseProblem;
import com.jycz.qingyun.model.entity.CourseProblemReply;
import com.jycz.qingyun.model.entity.User;
import com.jycz.qingyun.service.AIReplyService;
import com.jycz.qingyun.service.AIService;
import com.jycz.qingyun.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIReplyServiceImpl implements AIReplyService {

    private final CourseProblemMapper courseProblemMapper;
    private final CourseProblemReplyMapper courseProblemReplyMapper;
    private final UserMapper userMapper;
    private final AIService aiService;
    private final NoticeService noticeService;

    @Override
    @Async
    @Transactional
    public void generateAIRepliesAsync(Long problemId, String title, String content) {
        try {
            log.info("开始异步 AI 回复: problemId={}", problemId);

            // 1. 等待 2 秒（模拟正常用户回复的思考时间）
            Thread.sleep(2000);

            // 2. 查询已有回复（避免 AI 重复已有内容）
            List<CourseProblemReply> existingReplies = courseProblemReplyMapper.selectByProblemId(problemId);
            List<Map<String, String>> existingReplyList = new ArrayList<>();
            for (CourseProblemReply reply : existingReplies) {
                User user = userMapper.selectById(reply.getUserId());
                Map<String, String> map = new HashMap<>();
                map.put("userName", user != null ? user.getName() : "未知用户");
                map.put("content", reply.getContent());
                existingReplyList.add(map);
            }

            // 3. 调用 AI 生成回复
            String aiContent = aiService.generateReply(title, content, existingReplyList);

            // 4. 保存 AI 回复到数据库
            CourseProblemReply aiReply = new CourseProblemReply();
            aiReply.setProblemId(problemId);
            aiReply.setUserId(aiService.getAiUserId());
            aiReply.setContent(aiContent);
            aiReply.setIsAi(1);
            aiReply.setCreatedAt(LocalDateTime.now());
            courseProblemReplyMapper.insert(aiReply);

            // 5. 更新问题回复数
            CourseProblem problem = courseProblemMapper.selectById(problemId);
            if (problem != null) {
                problem.setReplyCount(problem.getReplyCount() + 1);
                courseProblemMapper.updateById(problem);
            }

            // 6. 发送通知给问题发布者（AI 已回复）
            if (problem != null) {
                User author = userMapper.selectById(problem.getUserId());
                if (author != null) {
                    noticeService.addNotice(
                            problem.getUserId(),
                            "🤖 AI助教已回复",
                            "你的问题「" + title + "」已被 AI 助教回复，快去查看吧！",
                            12
                    );
                }
            }

            log.info("AI 回复成功: problemId={}", problemId);

        } catch (Exception e) {
            log.error("AI 回复失败: problemId={}, error={}", problemId, e.getMessage(), e);
        }
    }
}