package com.jycz.qingyun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jycz.qingyun.model.entity.CourseProblemReply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CourseProblemReplyMapper extends BaseMapper<CourseProblemReply> {

    @Select("SELECT COUNT(*) FROM course_problem_reply WHERE problem_id = #{problemId}")
    int countByProblemId(@Param("problemId") Long problemId);

    @Select("SELECT * FROM course_problem_reply WHERE problem_id = #{problemId} ORDER BY created_at ASC")
    List<CourseProblemReply> selectByProblemId(@Param("problemId") Long problemId);
}