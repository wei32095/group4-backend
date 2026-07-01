package com.jycz.qingyun.controller;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.dto.ExchangeRequest;
import com.jycz.qingyun.model.dto.PlantRequest;
import com.jycz.qingyun.model.vo.FlowerListVO;
import com.jycz.qingyun.model.vo.FlowerVO;
import com.jycz.qingyun.model.vo.PointsRecordListVO;
import com.jycz.qingyun.model.vo.SeedVO;
import com.jycz.qingyun.model.vo.ShopItemVO;
import com.jycz.qingyun.service.FlowerService;
import com.jycz.qingyun.service.PointsRecordService;
import com.jycz.qingyun.service.SeedService;
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

    @Autowired
    private SeedService seedService;

    @GetMapping("/records")
    public ApiResult<PointsRecordListVO> getPointsRecords(
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        PointsRecordListVO vo = pointsRecordService.getRecords(userId);
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

    @GetMapping("/seeds")
    public ApiResult<List<SeedVO>> getSeeds() {
        List<SeedVO> list = seedService.getSeedList();
        return ApiResult.success(list);
    }

    @PostMapping("/plant")
    public ApiResult<FlowerVO> plant(
            @RequestBody PlantRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");

        try {
            FlowerVO vo = flowerService.plant(userId, request.getSeedId());
            return ApiResult.success("播种成功", vo);
        } catch (RuntimeException e) {
            return ApiResult.error(400, e.getMessage());
        }
    }

    @GetMapping("/shop")
    public ApiResult<List<ShopItemVO>> getShop() {
        List<ShopItemVO> list = shopItemService.getShopList();
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
}
