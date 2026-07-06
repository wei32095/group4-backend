package com.jycz.qingyun.service;

import com.jycz.qingyun.model.dto.CourseProblemRequest;
import com.jycz.qingyun.model.dto.ProblemReplyRequest;
import com.jycz.qingyun.model.vo.CourseProblemVO;
import com.jycz.qingyun.model.vo.ProblemDetailVO;

import java.util.List;

public interface CourseProblemService {

    CourseProblemVO createProblem(CourseProblemRequest request, Long userId);

    List<CourseProblemVO> getProblemList(Long courseId, Long userId, Integer pageNum, Integer pageSize);

    ProblemDetailVO getProblemDetail(Long problemId, Long userId);

    CourseProblemVO replyProblem(ProblemReplyRequest request, Long userId);

    void deleteProblem(Long problemId, Long userId);

    void deleteReply(Long replyId, Long userId);
}