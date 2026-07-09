package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.dto.CourseResourceUploadRequest;
import com.jycz.qingyun.model.vo.CourseResourceVO;
import com.jycz.qingyun.service.CourseResourceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
@Slf4j
@RestController
@RequestMapping("/qingyun/course/resource")
@RequiredArgsConstructor
public class CourseResourceController {

    private final CourseResourceService courseResourceService;

    /**
     * 教师上传资源（先调同事的OSS接口拿fileUrl，再调这个接口）
     * POST /qingyun/course/resource/upload
     */
    @PostMapping("/upload")
    public ApiResult<CourseResourceVO> uploadResource(
            @Valid @RequestBody CourseResourceUploadRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 2) {
            return ApiResult.error(403, "仅教师可上传资源");
        }

        CourseResourceVO response = courseResourceService.uploadResource(request, userId);
        return ApiResult.success("上传成功", response);
    }

    /**
     * 资源列表（学生/教师）
     * GET /qingyun/course/resource/list?courseId=1
     */
    @GetMapping("/list")
    public ApiResult<List<CourseResourceVO>> getResourceList(
            @RequestParam Long courseId,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");

        List<CourseResourceVO> response = courseResourceService.getResourceList(courseId, userId);
        return ApiResult.success(response);
    }

    /**
     * 删除资源（教师）
     * DELETE /qingyun/course/resource/{resourceId}
     */
    @DeleteMapping("/{resourceId}")
    public ApiResult<Void> deleteResource(
            @PathVariable Long resourceId,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");

        if (role == null || role != 2) {
            return ApiResult.error(403, "仅教师可删除资源");
        }

        courseResourceService.deleteResource(resourceId, userId);
        return ApiResult.success("删除成功", null);
    }

    /**
     * 下载资源（直接返回文件流）
     * POST /qingyun/course/resource/download/{resourceId}
     */
    @PostMapping("/download/{resourceId}")
    public void downloadResource(
            @PathVariable Long resourceId,
            HttpServletRequest httpRequest,
            HttpServletResponse response) throws IOException {

        Long userId = (Long) httpRequest.getAttribute("userId");

        // 1. 获取文件信息（同时更新下载次数）
        CourseResourceVO resource = courseResourceService.downloadResource(resourceId, userId);

        // 2. 从 OSS 获取文件流
        String fileUrl = resource.getFileUrl();
        String fileName = resource.getFileName();

        // 3. 设置响应头
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" +
                new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + "\"");

        // 4. 从 OSS 读取文件并写入响应
        URL url = new URL(fileUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        try (InputStream inputStream = conn.getInputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, bytesRead);
            }
            response.getOutputStream().flush();
        } finally {
            conn.disconnect();
        }
    }
}