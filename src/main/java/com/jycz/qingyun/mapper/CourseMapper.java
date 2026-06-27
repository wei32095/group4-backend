package com.jycz.qingyun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jycz.qingyun.model.entity.Course;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CourseMapper{

    @Insert("INSERT INTO course(name, description, cover_image, teacher_id, teacher_name, course_code, status, created_at) " +
            "VALUES(#{name}, #{description}, #{coverImage}, #{teacherId}, #{teacherName}, #{courseCode}, #{status}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Course course);

    @Select("SELECT * FROM course WHERE id = #{id}")
    Course findById(Long id);

    @Select("SELECT * FROM course WHERE teacher_id = #{teacherId} ORDER BY created_at DESC")
    List<Course> findByTeacherId(Long teacherId);

    @Select("SELECT * FROM course WHERE course_code = #{courseCode}")
    Course findByCourseCode(String courseCode);
}