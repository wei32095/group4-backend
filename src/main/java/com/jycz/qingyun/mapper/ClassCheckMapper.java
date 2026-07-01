package com.jycz.qingyun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jycz.qingyun.model.entity.ClassCheck;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface ClassCheckMapper extends BaseMapper<ClassCheck> {

    @Select("SELECT COUNT(*) FROM class_check WHERE checkin_time >= #{since}")
    long countSince(@Param("since") LocalDateTime since);
}