package com.jycz.qingyun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jycz.qingyun.model.entity.StudyRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface StudyRoomMapper extends BaseMapper<StudyRoom> {

    @Select("SELECT COALESCE(SUM(total_time), 0) FROM study_room " +
            "WHERE user_id = #{userId} AND end_time IS NOT NULL " +
            "AND start_time >= #{since}")
    Integer sumTotalTimeByUserIdSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    @Select("SELECT COUNT(*) FROM study_room " +
            "WHERE user_id = #{userId} AND end_time IS NOT NULL " +
            "AND start_time >= #{since}")
    Integer countValidByUserIdSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    @Select("SELECT COUNT(DISTINCT user_id) FROM study_room WHERE start_time >= #{since}")
    long countDistinctUsersSince(@Param("since") LocalDateTime since);

    @Select("SELECT COUNT(DISTINCT user_id) FROM (" +
            "SELECT user_id FROM study_room WHERE start_time >= #{since} " +
            "UNION " +
            "SELECT user_id FROM class_check WHERE checkin_time >= #{since}" +
            ") combined")
    long countActiveUsersSince(@Param("since") LocalDateTime since);
}
