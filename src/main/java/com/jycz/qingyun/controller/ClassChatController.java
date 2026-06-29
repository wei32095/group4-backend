package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.dto.ClassChatSendRequest;
import com.jycz.qingyun.model.vo.ClassChatVO;
import com.jycz.qingyun.service.ClassChatService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/qingyun/class/chat")//
@RequiredArgsConstructor
public class ClassChatController {

    private final ClassChatService classChatService;

    /**
     * 发送消息
     * POST /api/v1/class/chat/send
     */
    @PostMapping("/send")
    public ApiResult<ClassChatVO> sendMessage(
            @Valid @RequestBody ClassChatSendRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");

        ClassChatVO response = classChatService.sendMessage(request, userId);
        return ApiResult.success("发送成功", response);
    }

    /**
     * 获取消息列表
     * GET /api/v1/class/chat/list?classId={classId}&pageNum=1&pageSize=20
     */
    @GetMapping("/list")
    public ApiResult<List<ClassChatVO>> getMessageList(
            @RequestParam Long classId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");

        List<ClassChatVO> response = classChatService.getMessageList(classId, userId, pageNum, pageSize);
        return ApiResult.success(response);
    }
}