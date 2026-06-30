package com.jycz.qingyun.service;

import com.jycz.qingyun.model.dto.CreateStudyRoomRequest;
import com.jycz.qingyun.model.dto.EndStudyRoomRequest;
import com.jycz.qingyun.model.vo.StudyRoomEndVO;
import com.jycz.qingyun.model.vo.StudyRoomRecordListVO;
import com.jycz.qingyun.model.vo.StudyRoomStatisticVO;
import com.jycz.qingyun.model.vo.StudyRoomVO;

public interface StudyRoomService {
    StudyRoomVO createStudyRoom(Long userId, CreateStudyRoomRequest request);
    StudyRoomEndVO endStudyRoom(Long userId, EndStudyRoomRequest request);
    StudyRoomRecordListVO getRecords(Long userId, int page, int size);
    StudyRoomStatisticVO getStudyStatistic(Long userId);
}
