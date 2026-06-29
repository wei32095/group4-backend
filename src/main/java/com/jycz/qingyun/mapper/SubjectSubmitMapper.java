package com.jycz.qingyun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jycz.qingyun.model.entity.SubjectSubmit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SubjectSubmitMapper extends BaseMapper<SubjectSubmit> {

    @Select("SELECT * FROM subject_submit WHERE assignment_id = #{assignmentId} AND user_id = #{userId}")
    List<SubjectSubmit> selectByAssignmentAndUser(@Param("assignmentId") Long assignmentId,
                                                  @Param("userId") Long userId);
}