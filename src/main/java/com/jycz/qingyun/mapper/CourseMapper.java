package com.jycz.qingyun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jycz.qingyun.model.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {

    @Select("SELECT COUNT(*) FROM course WHERE course_code = #{courseCode}")
    int countByCourseCode(String courseCode);
}