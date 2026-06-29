package com.jycz.qingyun.service;

import com.jycz.qingyun.model.dto.ClassChatSendRequest;
import com.jycz.qingyun.model.vo.ClassChatVO;

import java.util.List;

public interface ClassChatService {
//
    ClassChatVO sendMessage(ClassChatSendRequest request, Long userId);

    List<ClassChatVO> getMessageList(Long classId, Long userId, Integer pageNum, Integer pageSize);
}