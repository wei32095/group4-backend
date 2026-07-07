package com.jycz.qingyun.service;

import com.jycz.qingyun.model.vo.WeakPointVO;

import java.util.List;

public interface WeakPointService {

    List<WeakPointVO> getWeakPointsList(Long userId, Long courseId, Long assignmentId);
}