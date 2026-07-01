package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.AddSeedRequest;
import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.dto.UpdateSeedRequest;
import com.jycz.qingyun.model.vo.SeedVO;
import com.jycz.qingyun.service.SeedService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/qingyun/flowers/admin/seeds")
@RequiredArgsConstructor
public class SeedAdminController {

    private final SeedService seedService;

    /**
     * 管理员新增种子
     * POST /qingyun/flowers/admin/seeds
     */
    @PostMapping
    public ApiResult<SeedVO> addSeed(
            @Valid @RequestBody AddSeedRequest request,
            HttpServletRequest httpRequest) {

        Integer role = (Integer) httpRequest.getAttribute("role");
        if (role == null || role != 3) {
            return ApiResult.error(403, "仅管理员可操作");
        }

        try {
            SeedVO vo = seedService.addSeed(request);
            return ApiResult.success("新增成功", vo);
        } catch (RuntimeException e) {
            return ApiResult.error(400, e.getMessage());
        }
    }

    /**
     * 管理员编辑种子（部分更新）
     * PUT /qingyun/flowers/admin/seeds/{id}
     */
    @PutMapping("/{id}")
    public ApiResult<SeedVO> updateSeed(
            @PathVariable Long id,
            @RequestBody UpdateSeedRequest request,
            HttpServletRequest httpRequest) {

        Integer role = (Integer) httpRequest.getAttribute("role");
        if (role == null || role != 3) {
            return ApiResult.error(403, "仅管理员可操作");
        }

        SeedVO vo = seedService.updateSeed(id, request);
        if (vo == null) {
            return ApiResult.error(404, "种子不存在");
        }
        return ApiResult.success("更新成功", vo);
    }

    /**
     * 管理员删除种子（逻辑删除）
     * DELETE /qingyun/flowers/admin/seeds/{id}
     */
    @DeleteMapping("/{id}")
    public ApiResult<Void> deleteSeed(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        Integer role = (Integer) httpRequest.getAttribute("role");
        if (role == null || role != 3) {
            return ApiResult.error(403, "仅管理员可操作");
        }

        boolean deleted = seedService.deleteSeed(id);
        if (!deleted) {
            return ApiResult.error(404, "种子不存在");
        }
        return ApiResult.success("删除成功", null);
    }

    /**
     * 管理员查看全部种子（含已删除）
     * GET /qingyun/flowers/admin/seeds
     */
    @GetMapping
    public ApiResult<List<SeedVO>> listAllSeeds(HttpServletRequest httpRequest) {

        Integer role = (Integer) httpRequest.getAttribute("role");
        if (role == null || role != 3) {
            return ApiResult.error(403, "仅管理员可操作");
        }

        List<SeedVO> list = seedService.listAllSeeds();
        return ApiResult.success(list);
    }

    /**
     * 管理员查看单个种子详情
     * GET /qingyun/flowers/admin/seeds/{id}
     */
    @GetMapping("/{id}")
    public ApiResult<SeedVO> getSeedById(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        Integer role = (Integer) httpRequest.getAttribute("role");
        if (role == null || role != 3) {
            return ApiResult.error(403, "仅管理员可操作");
        }

        SeedVO vo = seedService.getSeedById(id);
        if (vo == null) {
            return ApiResult.error(404, "种子不存在");
        }
        return ApiResult.success(vo);
    }

    /**
     * 管理员恢复已删除种子
     * PUT /qingyun/flowers/admin/seeds/{id}/restore
     */
    @PutMapping("/{id}/restore")
    public ApiResult<SeedVO> restoreSeed(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        Integer role = (Integer) httpRequest.getAttribute("role");
        if (role == null || role != 3) {
            return ApiResult.error(403, "仅管理员可操作");
        }

        SeedVO vo = seedService.restoreSeed(id);
        if (vo == null) {
            return ApiResult.error(404, "种子不存在");
        }
        return ApiResult.success("恢复成功", vo);
    }
}
