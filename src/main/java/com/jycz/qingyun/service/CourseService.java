package com.jycz.qingyun.service;

import com.jycz.qingyun.model.dto.CourseAuditRequest;
import com.jycz.qingyun.model.dto.CourseCreateRequest;
import com.jycz.qingyun.model.dto.CourseJoinRequest;
import com.jycz.qingyun.model.vo.*;

import java.util.List;

public interface CourseService {

    CourseCreateVO createCourse(CourseCreateRequest request, Long teacherId);

    CourseJoinVO joinCourse(CourseJoinRequest request, Long studentId);

    CourseDetailVO getCourseDetail(Long courseId, Long userId, Integer role);

    List<CourseListStudentVO> getStudentCourseList(Long studentId, Integer pageNum, Integer pageSize);

    List<CourseListTeacherVO> getTeacherCourseList(Long teacherId, Integer pageNum, Integer pageSize);

    CourseAuditVO auditCourse(CourseAuditRequest request, Long adminId);

    void endCourse(Long courseId, Long teacherId);

    List<AdminUserCourseVO> getAdminUserCourses(Long userId, Integer pageNum, Integer pageSize);

    List<CoursePendingVO> getPendingCourseList();
}
