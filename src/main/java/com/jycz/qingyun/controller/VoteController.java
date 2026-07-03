package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.dto.VoteCreateRequest;
import com.jycz.qingyun.model.dto.VoteSubmitRequest;
import com.jycz.qingyun.model.vo.VoteListVO;
import com.jycz.qingyun.model.vo.VoteCreateVO;
import com.jycz.qingyun.model.vo.VoteResultVO;
import com.jycz.qingyun.model.vo.VoteSubmitVO;
import com.jycz.qingyun.service.VoteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j//
@RestController
@RequestMapping("/qingyun/vote")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/create")
    public ApiResult<VoteCreateVO> createVote(
            @Valid @RequestBody VoteCreateRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 2) {
            return ApiResult.error(403, "仅教师可发起投票");
        }

        VoteCreateVO response = voteService.createVote(request, userId);
        return ApiResult.success("投票发起成功", response);
    }

    @PostMapping("/submit")
    public ApiResult<VoteSubmitVO> submitVote(
            @Valid @RequestBody VoteSubmitRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 1) {
            return ApiResult.error(403, "仅学生可投票");
        }

        VoteSubmitVO response = voteService.submitVote(request, userId);
        return ApiResult.success("投票成功", response);
    }

    @GetMapping("/result")
    public ApiResult<VoteResultVO> getVoteResult(
            @RequestParam Long voteId,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 2) {
            return ApiResult.error(403, "仅教师可查看投票结果");
        }

        VoteResultVO response = voteService.getVoteResult(voteId, userId);
        return ApiResult.success(response);
    }

    @GetMapping("/list")
    public ApiResult<List<VoteListVO>> getActiveVoteList(
            @RequestParam Long classId,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 1) {
            return ApiResult.error(403, "仅学生可查看");
        }

        List<VoteListVO> response = voteService.getVoteList(classId, userId);
        return ApiResult.success(response);
    }
}