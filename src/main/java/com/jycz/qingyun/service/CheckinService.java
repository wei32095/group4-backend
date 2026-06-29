package com.jycz.qingyun.service;

import com.jycz.qingyun.model.dto.CheckinSubmitRequest;
import com.jycz.qingyun.model.vo.CheckinResultVO;
import com.jycz.qingyun.model.vo.CheckinSubmitVO;

public interface CheckinService {

    CheckinSubmitVO submitCheckin(CheckinSubmitRequest request, Long studentId);

    CheckinResultVO getCheckinResult(Long classId, Long teacherId);
}