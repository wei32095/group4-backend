package com.jycz.qingyun.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PointsRecordMapper {

    /**
     * 获取用户最新积分余额
     */
    @Select("SELECT left_points FROM points_record WHERE user_id = #{userId} ORDER BY change_time DESC LIMIT 1")
    Integer getLatestPoints(@Param("userId") Long userId);
}
