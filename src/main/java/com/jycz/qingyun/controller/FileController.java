package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/qingyun/file")
public class FileController {

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
}
