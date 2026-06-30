package com.jycz.qingyun.service.serviceImpl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.jycz.qingyun.config.AliyunOssConfig;
import com.jycz.qingyun.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class OssServiceImpl implements OssService {

    /**
     * 常见文件后缀 → Content-Type 映射
     */
    private static final Map<String, String> CONTENT_TYPE_MAP = Map.ofEntries(
            Map.entry(".jpg", "image/jpeg"),
            Map.entry(".jpeg", "image/jpeg"),
            Map.entry(".png", "image/png"),
            Map.entry(".gif", "image/gif"),
            Map.entry(".webp", "image/webp"),
            Map.entry(".bmp", "image/bmp"),
            Map.entry(".svg", "image/svg+xml"),
            Map.entry(".mp4", "video/mp4"),
            Map.entry(".pdf", "application/pdf"),
            Map.entry(".doc", "application/msword"),
            Map.entry(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
            Map.entry(".xls", "application/vnd.ms-excel"),
            Map.entry(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
            Map.entry(".mp3", "audio/mpeg"),
            Map.entry(".zip", "application/zip")
    );

    @Autowired
    private OSS ossClient;

    @Autowired
    private AliyunOssConfig aliyunOssConfig;

    @Override
    public String uploadFile(MultipartFile file, String directory) {
        // 1. 校验文件
        if (file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        // 2. 生成唯一文件名（防止覆盖）
        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }
        String fileName = directory + "/" + UUID.randomUUID().toString().replace("-", "") + suffix;

        // 3. 上传（设定 Content-Type，浏览器直接预览而非下载）
        try (InputStream inputStream = file.getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            String contentType = file.getContentType();
            if (contentType == null || contentType.equals("application/octet-stream")) {
                contentType = CONTENT_TYPE_MAP.getOrDefault(suffix, "application/octet-stream");
            }
            metadata.setContentType(contentType);
            metadata.setContentDisposition("inline");

            PutObjectRequest putRequest = new PutObjectRequest(
                    aliyunOssConfig.getBucket(), fileName, inputStream, metadata);
            ossClient.putObject(putRequest);
        } catch (IOException e) {
            log.error("OSS 上传失败", e);
            throw new RuntimeException("文件上传失败");
        }

        // 4. 返回签名 URL（有效期 30 天）
        return getSignedUrl(fileName);
    }

    @Override
    public String getSignedUrl(String fileName) {
        // 签名有效期 30 天
        Date expiration = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
        URL signedUrl = ossClient.generatePresignedUrl(
                aliyunOssConfig.getBucket(), fileName, expiration,
                com.aliyun.oss.HttpMethod.GET
        );
        return signedUrl.toString();
    }
}
