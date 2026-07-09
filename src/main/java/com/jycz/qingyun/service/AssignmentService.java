package com.jycz.qingyun.service;

import com.jycz.qingyun.model.dto.AssignmentCreateRequest;
import com.jycz.qingyun.model.dto.AssignmentGradeRequest;
import com.jycz.qingyun.model.dto.AssignmentSubmitRequest;
import com.jycz.qingyun.model.vo.*;

import java.util.List;

public interface AssignmentService {

    AssignmentCreateVO createAssignment(AssignmentCreateRequest request, Long teacherId);

    List<AssignmentStudentListVO> getStudentAssignmentList(Long studentId);

    AssignmentDetailVO getAssignmentDetail(Long assignmentId, Long userId);

    AssignmentSubmitVO submitAssignment(AssignmentSubmitRequest request, Long studentId);

    AssignmentGradeVO gradeAssignment(AssignmentGradeRequest request, Long teacherId);

    List<AssignmentTeacherListVO> getTeacherAssignmentList(Long courseId, Long teacherId);

    AssignmentStudentGradeVO getStudentGrades(Long assignmentId, Long teacherId);

    /**
     * 获取待批改作业列表
     * @param courseId 课程ID（可为 null）
     * @param assignmentId 作业ID（可为 null）
     * @param teacherId 教师ID
     * @return 待批改作业列表
     */
    List<PendingAssignmentVO> getPendingAssignments(Long courseId, Long assignmentId, Long teacherId);
}