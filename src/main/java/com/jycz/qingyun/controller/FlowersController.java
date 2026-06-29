package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.dto.ExchangeRequest;
import com.jycz.qingyun.model.dto.UseItemRequest;
import com.jycz.qingyun.model.vo.FlowerListVO;
import com.jycz.qingyun.model.vo.PointsRecordListVO;
import com.jycz.qingyun.model.vo.ShopItemVO;
import com.jycz.qingyun.model.vo.UseItemVO;
import com.jycz.qingyun.model.vo.UserItemVO;
import com.jycz.qingyun.service.FlowerService;
import com.jycz.qingyun.service.PointsRecordService;
import com.jycz.qingyun.service.ShopItemService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/qingyun/flowers")
public class FlowersController {

    @Autowired
    private PointsRecordService pointsRecordService;

    @Autowired
    private ShopItemService shopItemService;

    @Autowired
    private FlowerService flowerService;

    @GetMapping("/records")
    public ApiResult<PointsRecordListVO> getPointsRecords(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        PointsRecordListVO vo = pointsRecordService.getRecords(userId, page, size);
        return ApiResult.success(vo);
    }

    @GetMapping("/my")
    public ApiResult<FlowerListVO> getMyFlowers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        FlowerListVO vo = flowerService.getMyFlowers(userId, page, size);
        return ApiResult.success(vo);
    }

    @GetMapping("/shop")
    public ApiResult<List<ShopItemVO>> getShop() {
        List<ShopItemVO> list = shopItemService.getShopList();
        return ApiResult.success(list);
    }

    @GetMapping("/bag")
    public ApiResult<List<UserItemVO>> getBag(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        List<UserItemVO> list = shopItemService.getBag(userId);
        return ApiResult.success(list);
    }

    @PostMapping("/exchange")
    public ApiResult<Boolean> exchangeItem(
            @RequestBody ExchangeRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");

        try {
            shopItemService.exchangeItem(userId, request.getItemId());
            return ApiResult.success("兑换成功", true);
        } catch (RuntimeException e) {
            return ApiResult.error(400, e.getMessage());
        }
    }

    @PostMapping("/use")
    public ApiResult<UseItemVO> useItem(
            @RequestBody UseItemRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");

        try {
            UseItemVO vo = shopItemService.useItem(userId, request.getFlowerId(), request.getItemId());
            return ApiResult.success("使用成功", vo);
        } catch (RuntimeException e) {
            return ApiResult.error(400, e.getMessage());
        }
    }
}
