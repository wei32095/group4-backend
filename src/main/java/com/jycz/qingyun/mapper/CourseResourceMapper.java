package com.jycz.qingyun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jycz.qingyun.model.entity.CourseResource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CourseResourceMapper extends BaseMapper<CourseResource> {

    @Update("UPDATE course_resource SET download_count = download_count + 1 WHERE id = #{resourceId}")
    void incrementDownloadCount(Long resourceId);
}