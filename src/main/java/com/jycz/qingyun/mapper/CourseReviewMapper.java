package com.jycz.qingyun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jycz.qingyun.model.entity.CourseReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface CourseReviewMapper extends BaseMapper<CourseReview> {

    @Select("SELECT COUNT(*) FROM course_review WHERE course_id = #{courseId} AND user_id = #{userId}")
    int countByCourseIdAndUserId(@Param("courseId") Long courseId, @Param("userId") Long userId);

    @Select("SELECT COALESCE(ROUND(AVG(star), 1), 0) FROM course_review WHERE course_id = #{courseId}")
    Double selectAvgStar(@Param("courseId") Long courseId);

    @Select("SELECT star, COUNT(*) AS count FROM course_review WHERE course_id = #{courseId} GROUP BY star")
    List<Map<String, Object>> selectStarStats(@Param("courseId") Long courseId);//
}