package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jycz.qingyun.model.dto.ClassChatSendRequest;
import com.jycz.qingyun.model.entity.Class;
import com.jycz.qingyun.model.entity.ClassChat;
import com.jycz.qingyun.model.entity.CourseStudent;
import com.jycz.qingyun.model.entity.User;
import com.jycz.qingyun.model.vo.ClassChatVO;
import com.jycz.qingyun.mapper.ClassChatMapper;
import com.jycz.qingyun.mapper.ClassMapper;
import com.jycz.qingyun.mapper.CourseStudentMapper;
import com.jycz.qingyun.mapper.UserMapper;
import com.jycz.qingyun.service.ClassChatService;
import com.jycz.qingyun.utils.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor//
public class ClassChatServiceImpl implements ClassChatService {

    private final ClassChatMapper classChatMapper;
    private final ClassMapper classMapper;
    private final CourseStudentMapper courseStudentMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public ClassChatVO sendMessage(ClassChatSendRequest request, Long userId) {
        // 1. 校验课堂是否存在
        Class clazz = classMapper.selectById(request.getClassId());
        if (clazz == null) {
            throw new BusinessException(404, "课堂不存在");
        }

        // 2. 校验用户是否已加入该课程（学生和老师都可以发言）
        LambdaQueryWrapper<CourseStudent> csWrapper = new LambdaQueryWrapper<>();
        csWrapper.eq(CourseStudent::getCourseId, clazz.getCourseId())
                .eq(CourseStudent::getUserId, userId);
        if (courseStudentMapper.selectCount(csWrapper) == 0) {
            // 检查是否是课堂的老师
            if (!clazz.getUserId().equals(userId)) {
                throw new BusinessException(403, "您未加入该课程，无法发言");
            }
        }

        // 3. 校验课堂状态（已结束不能发言）
        if ("ended".equals(clazz.getStatus())) {
            throw new BusinessException(400, "课堂已结束，无法发言");
        }

        // 4. 校验消息类型
        if (request.getMessageType() != 1 && request.getMessageType() != 2) {
            throw new BusinessException(400, "消息类型只支持 1-文字 或 2-图片");
        }

        // 5. 创建消息
        ClassChat chat = new ClassChat();
        chat.setClassId(request.getClassId());
        chat.setUserId(userId);
        chat.setMessageType(request.getMessageType());
        chat.setContent(request.getContent());
        chat.setSentTime(LocalDateTime.now());

        classChatMapper.insert(chat);
        log.info("消息发送成功: classId={}, userId={}, type={}", request.getClassId(), userId, request.getMessageType());

        // 6. 获取用户信息
        User user = userMapper.selectById(userId);

        return ClassChatVO.builder()
                .id(chat.getId())
                .userId(userId)
                .userName(user != null ? user.getName() : "未知用户")
                .userAvatar(user != null ? user.getAvatar() : null)
                .messageType(chat.getMessageType())
                .content(chat.getContent())
                .sentTime(chat.getSentTime())
                .build();
    }

    @Override
    public List<ClassChatVO> getMessageList(Long classId, Long userId, Integer pageNum, Integer pageSize) {
        // 1. 校验课堂是否存在
        Class clazz = classMapper.selectById(classId);
        if (clazz == null) {
            throw new BusinessException(404, "课堂不存在");
        }

        // 2. 校验用户是否已加入该课程
        LambdaQueryWrapper<CourseStudent> csWrapper = new LambdaQueryWrapper<>();
        csWrapper.eq(CourseStudent::getCourseId, clazz.getCourseId())
                .eq(CourseStudent::getUserId, userId);
        if (courseStudentMapper.selectCount(csWrapper) == 0) {
            if (!clazz.getUserId().equals(userId)) {
                throw new BusinessException(403, "您未加入该课程，无法查看");
            }
        }

        // 3. 查询消息列表
        int offset = (pageNum - 1) * pageSize;
        LambdaQueryWrapper<ClassChat> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassChat::getClassId, classId)
                .orderByDesc(ClassChat::getSentTime)
                .last("LIMIT " + offset + "," + pageSize);
        List<ClassChat> chatList = classChatMapper.selectList(wrapper);

        if (chatList.isEmpty()) {
            return new ArrayList<>();
        }

        // 4. 批量查询用户信息
        List<Long> userIds = chatList.stream()
                .map(ClassChat::getUserId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        // 5. 组装 VO（按发送时间正序返回，方便展示）
        List<ClassChatVO> result = new ArrayList<>();
        for (ClassChat chat : chatList) {
            User user = userMap.get(chat.getUserId());
            result.add(ClassChatVO.builder()
                    .id(chat.getId())
                    .userId(chat.getUserId())
                    .userName(user != null ? user.getName() : "未知用户")
                    .userAvatar(user != null ? user.getAvatar() : null)
                    .messageType(chat.getMessageType())
                    .content(chat.getContent())
                    .sentTime(chat.getSentTime())
                    .build());
        }

        // 按发送时间正序排列（最新的在最后）
        result.sort((a, b) -> a.getSentTime().compareTo(b.getSentTime()));
        return result;
    }
}