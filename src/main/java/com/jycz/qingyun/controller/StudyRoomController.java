package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.dto.CreateStudyRoomRequest;
import com.jycz.qingyun.model.dto.EndStudyRoomRequest;
import com.jycz.qingyun.model.vo.StudyRoomEndVO;
import com.jycz.qingyun.model.vo.StudyRoomRecordListVO;
import com.jycz.qingyun.model.vo.StudyRoomStatisticVO;
import com.jycz.qingyun.model.vo.StudyRoomVO;
import com.jycz.qingyun.service.StudyRoomService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/qingyun/studyroom")
public class StudyRoomController {

    @Autowired
    private StudyRoomService studyRoomService;

    @PostMapping("/create")
    public ApiResult<StudyRoomVO> createStudyRoom(
            @RequestBody CreateStudyRoomRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");

        try {
            StudyRoomVO vo = studyRoomService.createStudyRoom(userId, request);
            return ApiResult.success("开始学习", vo);
        } catch (RuntimeException e) {
            return ApiResult.error(400, e.getMessage());
        }
    }

    @PostMapping("/end")
    public ApiResult<StudyRoomEndVO> endStudyRoom(
            @RequestBody EndStudyRoomRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");

        try {
            StudyRoomEndVO vo = studyRoomService.endStudyRoom(userId, request);
            return ApiResult.success("学习结束", vo);
        } catch (RuntimeException e) {
            return ApiResult.error(400, e.getMessage());
        }
    }

    @GetMapping("/current")
    public ApiResult<StudyRoomVO> getCurrentStudy(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        StudyRoomVO vo = studyRoomService.getCurrentStudy(userId);
        return ApiResult.success(vo);
    }

    @GetMapping("/statistic")
    public ApiResult<StudyRoomStatisticVO> getStatistic(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        StudyRoomStatisticVO vo = studyRoomService.getStudyStatistic(userId);
        return ApiResult.success(vo);
    }

    @GetMapping("/records")
    public ApiResult<StudyRoomRecordListVO> getRecords(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        StudyRoomRecordListVO vo = studyRoomService.getRecords(userId, page, size);
        return ApiResult.success(vo);
    }
}
