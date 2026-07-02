package com.jycz.qingyun.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 管理员查看用户列表 - 分页包装
 */
@Data
public class AdminUserListVO {
    private List<AdminUserVO> records;
    private long total;
    private int pageNum;
    private int pageSize;
    private int pages;
}
