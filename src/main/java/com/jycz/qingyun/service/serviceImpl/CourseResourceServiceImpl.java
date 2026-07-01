package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jycz.qingyun.model.dto.CourseResourceUploadRequest;
import com.jycz.qingyun.model.entity.Course;
import com.jycz.qingyun.model.entity.CourseResource;
import com.jycz.qingyun.model.entity.CourseStudent;
import com.jycz.qingyun.model.entity.User;
import com.jycz.qingyun.model.vo.CourseResourceVO;
import com.jycz.qingyun.mapper.CourseMapper;
import com.jycz.qingyun.mapper.CourseResourceMapper;
import com.jycz.qingyun.mapper.CourseStudentMapper;
import com.jycz.qingyun.mapper.UserMapper;
import com.jycz.qingyun.service.CourseResourceService;
import com.jycz.qingyun.utils.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseResourceServiceImpl implements CourseResourceService {

    private final CourseResourceMapper courseResourceMapper;
    private final CourseMapper courseMapper;
    private final CourseStudentMapper courseStudentMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public CourseResourceVO uploadResource(CourseResourceUploadRequest request, Long userId) {
        // 1. 查询课程
        Course course = courseMapper.selectById(request.getCourseId());
        if (course == null) {
            throw new BusinessException(404, "课程不存在");
        }

        // 2. 校验教师权限
        if (!course.getUserId().equals(userId)) {
            throw new BusinessException(403, "您不是该课程的教师，无权上传资源");
        }

        // 3. 校验课程状态
        if (!"active".equals(course.getStatus())) {
            throw new BusinessException(400, "课程当前不可用，无法上传资源");
        }

        // 4. 保存记录（fileUrl 来自同事的 OSS 上传接口）
        CourseResource resource = new CourseResource();
        resource.setCourseId(request.getCourseId());
        resource.setUserId(userId);
        resource.setFileName(request.getFileName());
        resource.setFileUrl(request.getFileUrl());

        resource.setDescription(request.getDescription());
        resource.setDownloadCount(0);
        courseResourceMapper.insert(resource);

        log.info("资源上传成功: resourceId={}, fileName={}, courseId={}",
                resource.getId(), request.getFileName(), request.getCourseId());

        // 5. 返回结果
        User teacher = userMapper.selectById(userId);
        return buildVO(resource, teacher);
    }

    @Override
    public List<CourseResourceVO> getResourceList(Long courseId, Long userId) {
        // 1. 校验课程是否存在
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException(404, "课程不存在");
        }

        // 2. 校验用户是否已加入该课程（学生）或是否是该课程教师
        boolean isTeacher = course.getUserId().equals(userId);
        if (!isTeacher) {
            LambdaQueryWrapper<CourseStudent> csWrapper = new LambdaQueryWrapper<>();
            csWrapper.eq(CourseStudent::getCourseId, courseId)
                    .eq(CourseStudent::getUserId, userId);
            if (courseStudentMapper.selectCount(csWrapper) == 0) {
                throw new BusinessException(403, "您未加入该课程，无法查看资源");
            }
        }

        // 3. 查询资源列表
        LambdaQueryWrapper<CourseResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseResource::getCourseId, courseId)
                .orderByDesc(CourseResource::getCreatedAt);
        List<CourseResource> resources = courseResourceMapper.selectList(wrapper);

        if (resources.isEmpty()) {
            return new ArrayList<>();
        }

        // 4. 批量查询教师信息
        List<Long> teacherIds = resources.stream()
                .map(CourseResource::getUserId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, User> teacherMap = userMapper.selectBatchIds(teacherIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        // 5. 组装结果
        List<CourseResourceVO> list = new ArrayList<>();
        for (CourseResource resource : resources) {
            User teacher = teacherMap.get(resource.getUserId());
            list.add(buildVO(resource, teacher));
        }

        return list;
    }



    @Override
    @Transactional
    public void deleteResource(Long resourceId, Long userId) {
        // 1. 查询资源
        CourseResource resource = courseResourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new BusinessException(404, "资源不存在");
        }

        // 2. 校验教师权限
        Course course = courseMapper.selectById(resource.getCourseId());
        if (course == null || !course.getUserId().equals(userId)) {
            throw new BusinessException(403, "您不是该课程的教师，无权删除");
        }

        // 3. 删除记录
        courseResourceMapper.deleteById(resourceId);
        log.info("资源删除成功: resourceId={}, userId={}", resourceId, userId);
    }

    @Override
    @Transactional
    public CourseResourceVO downloadResource(Long resourceId, Long userId) {
        // 1. 查询资源
        CourseResource resource = courseResourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new BusinessException(404, "资源不存在");
        }

        // 2. 查询课程
        Course course = courseMapper.selectById(resource.getCourseId());
        if (course == null) {
            throw new BusinessException(404, "课程不存在");
        }

        // 3. 校验权限
        boolean isTeacher = course.getUserId().equals(userId);
        if (!isTeacher) {
            LambdaQueryWrapper<CourseStudent> csWrapper = new LambdaQueryWrapper<>();
            csWrapper.eq(CourseStudent::getCourseId, resource.getCourseId())
                    .eq(CourseStudent::getUserId, userId);
            if (courseStudentMapper.selectCount(csWrapper) == 0) {
                throw new BusinessException(403, "您未加入该课程，无法下载");
            }
        }

        // 4. 下载次数 +1
        courseResourceMapper.incrementDownloadCount(resourceId);

        // 5. 获取教师信息
        User teacher = userMapper.selectById(resource.getUserId());

        return buildVO(resource, teacher);
    }

    // ========== 工具方法 ==========

    private CourseResourceVO buildVO(CourseResource resource, User teacher) {
        return CourseResourceVO.builder()
                .resourceId(resource.getId())
                .courseId(resource.getCourseId())
                .userId(resource.getUserId())
                .teacherName(teacher != null ? teacher.getName() : "未知老师")
                .fileName(resource.getFileName())
                .fileUrl(resource.getFileUrl())

                .description(resource.getDescription())
                .downloadCount(resource.getDownloadCount())
                .createdAt(resource.getCreatedAt())
                .build();
    }
}