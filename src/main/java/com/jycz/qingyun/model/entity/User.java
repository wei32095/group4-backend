package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user")  // 对应数据库表名
public class User {

    @TableId(type = IdType.AUTO)  // 自增主键
    private Long id;

    private String name;

    private String password;

    private String phone;

    private String avatar;

    private Integer role;   // 1-学员 2-讲师 3-管理员

    private Integer status; // 1-正常 2-禁用

    // 积分在单独表中，但 User 实体里暂时不加 points，后面根据业务需要再建积分实体
}