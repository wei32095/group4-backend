package com.jycz.qingyun.service;

import com.jycz.qingyun.model.dto.AssignmentCreateRequest;
import com.jycz.qingyun.model.dto.AssignmentGradeRequest;
import com.jycz.qingyun.model.dto.AssignmentSubmitRequest;
import com.jycz.qingyun.model.vo.*;

import java.util.List;

public interface AssignmentService {
//
    AssignmentCreateVO createAssignment(AssignmentCreateRequest request, Long teacherId);

    List<AssignmentStudentListVO> getStudentAssignmentList(Long studentId);

    AssignmentDetailVO getAssignmentDetail(Long assignmentId, Long userId);

    AssignmentSubmitVO submitAssignment(AssignmentSubmitRequest request, Long studentId);

    AssignmentGradeVO gradeAssignment(AssignmentGradeRequest request, Long teacherId);

    List<AssignmentTeacherListVO> getTeacherAssignmentList(Long courseId, Long teacherId);

    // ========== 新增 ==========
    AssignmentStudentGradeVO getStudentGrades(Long assignmentId, Long teacherId);

    List<PendingAssignmentVO> getPendingAssignments(Long courseId, Long studentId, Long teacherId);
}