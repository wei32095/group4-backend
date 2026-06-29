package com.jycz.qingyun.service;

import com.jycz.qingyun.model.dto.VoteCreateRequest;
import com.jycz.qingyun.model.dto.VoteSubmitRequest;
import com.jycz.qingyun.model.vo.VoteCreateVO;
import com.jycz.qingyun.model.vo.VoteResultVO;
import com.jycz.qingyun.model.vo.VoteSubmitVO;
//
public interface VoteService {

    VoteCreateVO createVote(VoteCreateRequest request, Long teacherId);

    VoteSubmitVO submitVote(VoteSubmitRequest request, Long studentId);

    VoteResultVO getVoteResult(Long voteId, Long teacherId);
}