package com.jycz.qingyun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jycz.qingyun.model.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {

    @Select("SELECT COUNT(*) FROM notice WHERE user_id = #{userId} AND notice_status = 0")
    long countUnread(Long userId);

    @Select("SELECT COUNT(*) FROM notice WHERE user_id = #{userId} AND notice_type = 3")
    long countPendingGrade(Long userId);
}
