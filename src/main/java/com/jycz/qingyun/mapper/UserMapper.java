package com.jycz.qingyun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jycz.qingyun.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 原子增减积分（正数加分，负数扣分）
     */
    @Update("UPDATE user SET points = points + #{delta} WHERE id = #{userId}")
    void updatePoints(@Param("userId") Long userId, @Param("delta") int delta);

    /**
     * 扣分时校验余额充足，返回受影响行数（0 表示余额不足）
     */
    @Update("UPDATE user SET points = points - #{points} WHERE id = #{userId} AND points >= #{points}")
    int deductPointsIfSufficient(@Param("userId") Long userId, @Param("points") int points);

    /**
     * 查询用户当前积分
     */
    @Select("SELECT points FROM user WHERE id = #{userId}")
    Integer getPoints(@Param("userId") Long userId);
}