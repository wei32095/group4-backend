package com.jycz.qingyun.service;

import com.jycz.qingyun.model.dto.VoteCreateRequest;
import com.jycz.qingyun.model.dto.VoteSubmitRequest;
import com.jycz.qingyun.model.vo.VoteListVO;
import com.jycz.qingyun.model.vo.VoteCreateVO;
import com.jycz.qingyun.model.vo.VoteResultVO;
import com.jycz.qingyun.model.vo.VoteSubmitVO;

import java.util.List;

public interface VoteService {

    VoteCreateVO createVote(VoteCreateRequest request, Long teacherId);

    VoteSubmitVO submitVote(VoteSubmitRequest request, Long studentId);

    VoteResultVO getVoteResult(Long voteId, Long teacherId);

    /**
     * 获取投票列表（学生和老师通用）
     * @param classId 课堂ID
     * @param userId 当前用户ID
     * @param role 当前用户角色（1-学生，2-教师）
     */
    List<VoteListVO> getVoteList(Long classId, Long userId, Integer role);
}