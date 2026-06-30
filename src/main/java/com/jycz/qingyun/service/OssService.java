package com.jycz.qingyun.service;

import org.springframework.web.multipart.MultipartFile;

public interface OssService {

    /**
     * 上传文件到 OSS，返回签名 URL
     * @param file     上传的文件
     * @param directory 目录前缀（如 avatar/、images/）
     * @return 带签名的文件访问 URL
     */
    String uploadFile(MultipartFile file, String directory);

    /**
     * 生成 OSS 文件的签名 URL（用于查看已有文件）
     * @param fileName OSS 上的文件路径
     * @return 带签名的文件访问 URL
     */
    String getSignedUrl(String fileName);
}
