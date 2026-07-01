package com.jycz.qingyun.service;

import com.jycz.qingyun.model.dto.CourseResourceUploadRequest;
import com.jycz.qingyun.model.vo.CourseResourceVO;

import java.util.List;

public interface CourseResourceService {

    CourseResourceVO uploadResource(CourseResourceUploadRequest request, Long userId);

    List<CourseResourceVO> getResourceList(Long courseId, Long userId);

    void deleteResource(Long resourceId, Long userId);

    CourseResourceVO downloadResource(Long resourceId, Long userId);


}