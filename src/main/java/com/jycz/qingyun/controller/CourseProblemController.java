package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.dto.CourseProblemRequest;
import com.jycz.qingyun.model.dto.ProblemReplyRequest;
import com.jycz.qingyun.model.vo.CourseProblemVO;
import com.jycz.qingyun.model.vo.ProblemDetailVO;
import com.jycz.qingyun.service.CourseProblemService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/qingyun/course/problem")
@RequiredArgsConstructor
public class CourseProblemController {

    private final CourseProblemService courseProblemService;

    /**
     * 学生发布问题
     * POST /qingyun/course/problem/create
     */
    @PostMapping("/create")
    public ApiResult<CourseProblemVO> createProblem(
            @Valid @RequestBody CourseProblemRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 1) {
            return ApiResult.error(403, "仅学生可发布问题");
        }

        CourseProblemVO response = courseProblemService.createProblem(request, userId);
        return ApiResult.success("问题发布成功", response);
    }

    /**
     * 问题列表（学生/教师）
     * GET /qingyun/course/problem/list?courseId=1&pageNum=1&pageSize=10
     */
    @GetMapping("/list")
    public ApiResult<List<CourseProblemVO>> getProblemList(
            @RequestParam Long courseId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");

        List<CourseProblemVO> response = courseProblemService.getProblemList(courseId, userId, pageNum, pageSize);
        return ApiResult.success(response);
    }

    /**
     * 问题详情（学生/教师）
     * GET /qingyun/course/problem/{problemId}
     */
    @GetMapping("/{problemId}")
    public ApiResult<ProblemDetailVO> getProblemDetail(
            @PathVariable Long problemId,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");

        ProblemDetailVO response = courseProblemService.getProblemDetail(problemId, userId);
        return ApiResult.success(response);
    }

    /**
     * 回复问题（学生/教师）
     * POST /qingyun/course/problem/reply
     */
    @PostMapping("/reply")
    public ApiResult<CourseProblemVO> replyProblem(
            @Valid @RequestBody ProblemReplyRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");

        CourseProblemVO response = courseProblemService.replyProblem(request, userId);
        return ApiResult.success("回复成功", response);
    }

    /**
     * 删除自己的问题
     * DELETE /qingyun/course/problem/{problemId}
     */
    @DeleteMapping("/{problemId}")
    public ApiResult<Void> deleteProblem(
            @PathVariable Long problemId,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");

        courseProblemService.deleteProblem(problemId, userId);
        return ApiResult.success();
    }

    /**
     * 删除自己的回复
     * DELETE /qingyun/course/problem/reply/{replyId}
     */
    @DeleteMapping("/reply/{replyId}")
    public ApiResult<Void> deleteReply(
            @PathVariable Long replyId,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");

        courseProblemService.deleteReply(replyId, userId);
        return ApiResult.success();
    }
}