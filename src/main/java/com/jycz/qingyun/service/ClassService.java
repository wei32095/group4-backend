package com.jycz.qingyun.service;

import com.jycz.qingyun.model.dto.ClassCreateRequest;
import com.jycz.qingyun.model.vo.ClassCreateVO;
import com.jycz.qingyun.model.vo.ClassStudentVO;

import java.util.List;

public interface ClassService {

    ClassCreateVO createClass(ClassCreateRequest request, Long teacherId);

    List<ClassStudentVO> getStudentClassList(Long courseId, Long studentId);

    void endClass(Long classId, Long teacherId);
}