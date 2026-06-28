package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("course")  // 对应数据库表名
public class Course {
    private Long id;
    private String name;
    private String description;
    private String coverImage;
    private Long teacherId;
    private String teacherName;
    private String courseCode;
    private String status; // active, archived
    private LocalDateTime createdAt;
}