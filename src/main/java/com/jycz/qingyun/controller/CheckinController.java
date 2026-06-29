package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.dto.CheckinSubmitRequest;
import com.jycz.qingyun.model.vo.CheckinResultVO;
import com.jycz.qingyun.model.vo.CheckinSubmitVO;
import com.jycz.qingyun.service.CheckinService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/qingyun/checkin")
@RequiredArgsConstructor
public class CheckinController {

    private final CheckinService checkinService;

    @PostMapping("/submit")
    public ApiResult<CheckinSubmitVO> submitCheckin(
            @Valid @RequestBody CheckinSubmitRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 1) {
            return ApiResult.error(403, "仅学生可签到");
        }

        CheckinSubmitVO response = checkinService.submitCheckin(request, userId);
        return ApiResult.success("签到成功", response);
    }

    @GetMapping("/result")
    public ApiResult<CheckinResultVO> getCheckinResult(
            @RequestParam Long classId,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 2) {
            return ApiResult.error(403, "仅教师可查看签到结果");
        }

        CheckinResultVO response = checkinService.getCheckinResult(classId, userId);
        return ApiResult.success(response);
    }
}