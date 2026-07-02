package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")  // 对应数据库表名
public class User {

    @TableId(type = IdType.AUTO)  // 自增主键
    private Long id;

    private String openid;

    private String name;

    private String password;

    private String phone;

    private String bio;

    private String avatar;

    private Integer role;   // 1-学员 2-讲师 3-管理员

    private Integer status; // 1-正常 0-禁用

    private LocalDateTime banExpireTime;

    private String banReason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}