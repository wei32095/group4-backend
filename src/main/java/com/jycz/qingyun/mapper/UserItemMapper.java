package com.jycz.qingyun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jycz.qingyun.model.entity.UserItem;
import com.jycz.qingyun.model.vo.UserItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserItemMapper extends BaseMapper<UserItem> {

    @Select("SELECT ui.item_id AS id, si.item_name, si.icon, si.price, si.growth_value, ui.quantity " +
            "FROM user_item ui " +
            "LEFT JOIN shop_item si ON ui.item_id = si.id " +
            "WHERE ui.user_id = #{userId} AND ui.quantity > 0 " +
            "ORDER BY ui.created_at DESC")
    List<UserItemVO> selectUserBag(@Param("userId") Long userId);
}
