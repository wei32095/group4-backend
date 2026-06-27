package com.jycz.qingyun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jycz.qingyun.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 继承 BaseMapper 后，默认就有了 CRUD 方法（增删改查）
    // 这里暂时不需要写任何 SQL，MyBatis-Plus 会自动帮我们生成
}
