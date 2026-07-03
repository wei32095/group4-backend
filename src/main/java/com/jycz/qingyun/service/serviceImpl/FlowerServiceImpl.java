package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jycz.qingyun.config.AliyunOssConfig;
import com.jycz.qingyun.mapper.FlowerMapper;
import com.jycz.qingyun.mapper.SeedMapper;
import com.jycz.qingyun.model.entity.Flower;
import com.jycz.qingyun.model.entity.Seed;
import com.jycz.qingyun.model.vo.FlowerListVO;
import com.jycz.qingyun.model.vo.FlowerVO;
import com.jycz.qingyun.service.FlowerService;
import com.jycz.qingyun.service.PointsRecordService;
import com.jycz.qingyun.utils.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FlowerServiceImpl implements FlowerService {

    @Autowired
    private FlowerMapper flowerMapper;

    @Autowired
    private SeedMapper seedMapper;

    @Autowired
    private PointsRecordService pointsRecordService;

    @Autowired
    private AliyunOssConfig aliyunOssConfig;

    @Override
    public FlowerListVO getMyFlowers(Long userId) {
        // 1. 查询用户所有花卉
        LambdaQueryWrapper<Flower> wrapper = new LambdaQueryWrapper<Flower>()
                .eq(Flower::getUserId, userId)
                .orderByDesc(Flower::getCreatedAt);

        List<Flower> flowers = flowerMapper.selectList(wrapper);

        if (flowers.isEmpty()) {
            FlowerListVO empty = new FlowerListVO();
            empty.setRecords(List.of());
            return empty;
        }

        // 2. 批量查询品种信息
        List<Long> seedIds = flowers.stream()
                .map(Flower::getSeedId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Seed> seedMap = seedMapper.selectList(
                        new LambdaQueryWrapper<Seed>().in(Seed::getId, seedIds))
                .stream().collect(Collectors.toMap(Seed::getId, s -> s));

        // 3. 转换 VO
        List<FlowerVO> voList = flowers.stream()
                .map(flower -> toVO(flower, seedMap.get(flower.getSeedId())))
                .collect(Collectors.toList());

        // 4. 组装
        FlowerListVO listVO = new FlowerListVO();
        listVO.setRecords(voList);
        return listVO;
    }

    @Override
    @Transactional
    public FlowerVO plant(Long userId, Long seedId) {
        // 1. 查找用户最新的花卉
        LambdaQueryWrapper<Flower> wrapper = new LambdaQueryWrapper<Flower>()
                .eq(Flower::getUserId, userId)
                .orderByDesc(Flower::getCreatedAt)
                .last("LIMIT 1");
        List<Flower> flowerList = flowerMapper.selectList(wrapper);
        Flower currentFlower = flowerList.isEmpty() ? null : flowerList.get(0);

        // 2. 已有花卉 → 检查是否已培育完成
        if (currentFlower != null) {
            Seed currentSeed = seedMapper.selectById(currentFlower.getSeedId());
            int sunMax = currentSeed != null && currentSeed.getSunlightMax() != null ? currentSeed.getSunlightMax() : 100;
            int waterMax = currentSeed != null && currentSeed.getWaterMax() != null ? currentSeed.getWaterMax() : 100;
            int nutMax = currentSeed != null && currentSeed.getNutrientMax() != null ? currentSeed.getNutrientMax() : 100;
            int totalMax = sunMax + waterMax + nutMax;
            int totalCur = (currentFlower.getSunlight() != null ? currentFlower.getSunlight() : 0)
                         + (currentFlower.getWater() != null ? currentFlower.getWater() : 0)
                         + (currentFlower.getNutrient() != null ? currentFlower.getNutrient() : 0);
            int percent = totalMax > 0 ? totalCur * 100 / totalMax : 0;
            if (percent < 100) {
                throw new BusinessException(400, "当前花卉尚未培育完成，无法购买新种子");
            }
            // 满值 → 自动解锁图鉴
            if (currentFlower.getIsUnlocked() == null || currentFlower.getIsUnlocked() == 0) {
                currentFlower.setIsUnlocked(1);
                flowerMapper.updateById(currentFlower);
            }
        }

        // 3. 验证种子
        Seed targetSeed = seedMapper.selectById(seedId);
        if (targetSeed == null) {
            throw new BusinessException(404, "种子不存在");
        }

        // 4. 扣积分（price=0 不扣）
        if (targetSeed.getPrice() > 0) {
            pointsRecordService.deductPoints(userId, targetSeed.getPrice(), 5);
        }

        // 5. 创建新花卉
        Flower newFlower = new Flower();
        newFlower.setUserId(userId);
        newFlower.setSeedId(targetSeed.getId());
        newFlower.setSunlight(0);
        newFlower.setWater(0);
        newFlower.setNutrient(0);
        newFlower.setGrowthValue(0);
        newFlower.setStage(0);
        newFlower.setIsUnlocked(0);
        newFlower.setCreatedAt(LocalDateTime.now());
        newFlower.setUpdatedAt(LocalDateTime.now());
        flowerMapper.insert(newFlower);

        return toVO(newFlower, targetSeed);
    }

    private FlowerVO toVO(Flower flower, Seed seed) {
        FlowerVO vo = new FlowerVO();
        vo.setId(flower.getId());
        vo.setSeedId(flower.getSeedId());
        vo.setVariety(seed != null ? seed.getVariety() : "未知");
        vo.setSunlight(flower.getSunlight() != null ? flower.getSunlight() : 0);
        vo.setWater(flower.getWater() != null ? flower.getWater() : 0);
        vo.setNutrient(flower.getNutrient() != null ? flower.getNutrient() : 0);

        // 计算生长进度百分比
        int sunMax = seed != null && seed.getSunlightMax() != null ? seed.getSunlightMax() : 100;
        int waterMax = seed != null && seed.getWaterMax() != null ? seed.getWaterMax() : 100;
        int nutMax = seed != null && seed.getNutrientMax() != null ? seed.getNutrientMax() : 100;
        int totalMax = sunMax + waterMax + nutMax;
        int totalCur = vo.getSunlight() + vo.getWater() + vo.getNutrient();
        int percent = totalMax > 0 ? totalCur * 100 / totalMax : 0;
        vo.setGrowthPercent(percent);

        // 阶段判定
        int stage;
        if (percent < 25) {
            stage = 0;
        } else if (percent < 50) {
            stage = 1;
        } else if (percent < 75) {
            stage = 2;
        } else {
            stage = 3;
        }
        vo.setStage(stage);

        vo.setIsUnlocked(flower.getIsUnlocked());
        vo.setCreatedAt(flower.getCreatedAt());

        // 拷贝品种图片（解析 OSS key）
        if (seed != null) {
            vo.setImage(resolveOssKey(seed.getImage()));
            vo.setStage0Image(resolveOssKey(seed.getStage0Image()));
            vo.setStage1Image(resolveOssKey(seed.getStage1Image()));
            vo.setStage2Image(resolveOssKey(seed.getStage2Image()));
            vo.setStage3Image(resolveOssKey(seed.getStage3Image()));
        }

        return vo;
    }

    /**
     * 从 OSS 签名 URL 中提取文件 key，非 OSS URL 原样返回
     */
    private String resolveOssKey(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return null;
        if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            return imageUrl;
        }
        try {
            URL url = new URL(imageUrl);
            String host = url.getHost();
            if (host != null && host.contains(aliyunOssConfig.getBucket() + ".")) {
                String path = url.getPath();
                return path.startsWith("/") ? path.substring(1) : path;
            }
        } catch (Exception e) {
            // 非 OSS URL，原样返回
        }
        return imageUrl;
    }
}
