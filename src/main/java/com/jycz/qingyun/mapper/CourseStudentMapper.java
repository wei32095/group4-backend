package com.jycz.qingyun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jycz.qingyun.model.entity.CourseStudent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CourseStudentMapper extends BaseMapper<CourseStudent> {

    @Select("SELECT COUNT(*) FROM course_student WHERE course_id = #{courseId}")
    int countByCourseId(@Param("courseId") Long courseId);//
}