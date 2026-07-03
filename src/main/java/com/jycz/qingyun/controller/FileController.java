package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.service.OssService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@RestController
@RequestMapping("/qingyun/file")
public class FileController {

    private static final Map<String, String> CONTENT_TYPE_MAP = Map.ofEntries(
            Map.entry("jpg", "image/jpeg"),
            Map.entry("jpeg", "image/jpeg"),
            Map.entry("png", "image/png"),
            Map.entry("gif", "image/gif"),
            Map.entry("webp", "image/webp"),
            Map.entry("bmp", "image/bmp"),
            Map.entry("svg", "image/svg+xml"),
            Map.entry("mp4", "video/mp4"),
            Map.entry("pdf", "application/pdf"),
            Map.entry("doc", "application/msword"),
            Map.entry("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
            Map.entry("xls", "application/vnd.ms-excel"),
            Map.entry("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
            Map.entry("mp3", "audio/mpeg"),
            Map.entry("zip", "application/zip")
    );

    @Autowired
    private OssService ossService;

    @PostMapping("/upload")
    public ApiResult<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "dir", defaultValue = "images") String dir) {

        try {
            String url = ossService.uploadFile(file, dir);
            return ApiResult.success("上传成功", url);
        } catch (RuntimeException e) {
            return ApiResult.error(400, e.getMessage());
        }
    }

    /**
     * OSS 代理：后端请求 OSS → 流式返回，浏览器视为同源请求
     * GET /qingyun/file/get?path=xxx      → 浏览器直接显示（图片/PDF/视频）
     * GET /qingyun/file/get?path=xxx&dl=1 → 浏览器弹出下载
     */
    @GetMapping("/get")
    public void proxyFile(
            @RequestParam String path,
            @RequestParam(required = false, defaultValue = "0") int dl,
            HttpServletResponse response) {

        try {
            String signedUrl = ossService.getSignedUrl(path);
            URL url = new URL(signedUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);
            conn.connect();

            int status = conn.getResponseCode();
            if (status != 200) {
                response.setStatus(502);
                return;
            }

            String contentType = detectContentType(path);
            response.setContentType(contentType);
            response.setHeader("Cache-Control", "public, max-age=86400");

            // dl=1 → 下载模式（弹出下载框）
            if (dl == 1) {
                String fileName = path.contains("/") ? path.substring(path.lastIndexOf('/') + 1) : path;
                String encodedFileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");
            }

            try (InputStream in = conn.getInputStream();
                 OutputStream out = response.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.flush();
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            response.setStatus(500);
        }
    }

    private String detectContentType(String path) {
        if (path == null) return "application/octet-stream";
        int dot = path.lastIndexOf('.');
        if (dot < 0) return "application/octet-stream";
        String ext = path.substring(dot + 1).toLowerCase();
        return CONTENT_TYPE_MAP.getOrDefault(ext, "application/octet-stream");
    }
}
