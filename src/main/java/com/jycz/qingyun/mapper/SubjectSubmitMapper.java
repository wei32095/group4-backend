package com.jycz.qingyun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jycz.qingyun.model.entity.SubjectSubmit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SubjectSubmitMapper extends BaseMapper<SubjectSubmit> {

    @Select("SELECT * FROM subject_submit WHERE assignment_id = #{assignmentId} AND user_id = #{userId}")
    List<SubjectSubmit> selectByAssignmentAndUser(@Param("assignmentId") Long assignmentId, @Param("userId") Long userId);
    @Update("UPDATE subject_submit " +
            "SET subject_score = #{score}, teacher_comment = #{comment}, grading_status = 2, grading_time = NOW() " +
            "WHERE assignment_id = #{assignmentId} AND user_id = #{userId} AND question_id = #{questionId}")
    int updateSubjectScore(@Param("assignmentId") Long assignmentId,
                           @Param("userId") Long userId,
                           @Param("questionId") Long questionId,
                           @Param("score") Integer score,
                           @Param("comment") String comment);
}