package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.dto.SensitiveWordAddRequest;
import com.jycz.qingyun.model.dto.SensitiveWordDeleteRequest;
import com.jycz.qingyun.model.vo.SensitiveWordVO;
import com.jycz.qingyun.service.SensitiveWordService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/qingyun/sensitive")
@RequiredArgsConstructor
public class SensitiveWordController {

    private final SensitiveWordService sensitiveWordService;

    /**
     * 管理员增加敏感词
     * POST /qingyun/sensitive/add
     */
    @PostMapping("/add")
    public ApiResult<SensitiveWordVO> addSensitiveWord(
            @Valid @RequestBody SensitiveWordAddRequest request,
            HttpServletRequest httpRequest) {

        Integer role = (Integer) httpRequest.getAttribute("role");
        if (role == null || role != 3) {
            return ApiResult.error(403, "仅管理员可操作");
        }

        SensitiveWordVO response = sensitiveWordService.addSensitiveWord(request.getWord());
        return ApiResult.success("敏感词添加成功", response);
    }

    /**
     * 管理员删除敏感词
     * DELETE /qingyun/sensitive/delete
     */
    @DeleteMapping("/delete")
    public ApiResult<Void> deleteSensitiveWord(
            @Valid @RequestBody SensitiveWordDeleteRequest request,
            HttpServletRequest httpRequest) {

        Integer role = (Integer) httpRequest.getAttribute("role");
        if (role == null || role != 3) {
            return ApiResult.error(403, "仅管理员可操作");
        }

        sensitiveWordService.deleteSensitiveWord(request.getId());
        return ApiResult.success("敏感词删除成功", null);
    }

    /**
     * 查询敏感词列表
     * GET /qingyun/sensitive/list
     */
    @GetMapping("/list")
    public ApiResult<List<SensitiveWordVO>> listSensitiveWords(
            HttpServletRequest httpRequest) {

        Integer role = (Integer) httpRequest.getAttribute("role");
        if (role == null || role != 3) {
            return ApiResult.error(403, "仅管理员可操作");
        }

        List<SensitiveWordVO> response = sensitiveWordService.listSensitiveWords();
        return ApiResult.success(response);
    }
}