package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jycz.qingyun.mapper.FlowerMapper;
import com.jycz.qingyun.mapper.SeedMapper;
import com.jycz.qingyun.model.entity.Flower;
import com.jycz.qingyun.model.entity.Seed;
import com.jycz.qingyun.model.vo.FlowerListVO;
import com.jycz.qingyun.model.vo.FlowerVO;
import com.jycz.qingyun.service.FlowerService;
import com.jycz.qingyun.service.PointsRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public FlowerListVO getMyFlowers(Long userId) {
        // 1. 查询用户所有花卉
        LambdaQueryWrapper<Flower> wrapper = new LambdaQueryWrapper<Flower>()
                .eq(Flower::getUserId, userId)
                .orderByDesc(Flower::getCreatedAt);

        List<Flower> flowers = flowerMapper.selectList(wrapper);

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
        // 1. 查找用户最新的花卉（按创建时间倒序）
        LambdaQueryWrapper<Flower> wrapper = new LambdaQueryWrapper<Flower>()
                .eq(Flower::getUserId, userId)
                .orderByDesc(Flower::getCreatedAt)
                .last("LIMIT 1");
        List<Flower> flowerList = flowerMapper.selectList(wrapper);
        Flower currentFlower = flowerList.isEmpty() ? null : flowerList.get(0);

        // 2. 首次使用 → 免费送向日葵
        if (currentFlower == null) {
            Seed sunflower = seedMapper.selectById(1L);
            if (sunflower == null) {
                throw new RuntimeException("暂无可用品种");
            }

            Flower flower = new Flower();
            flower.setUserId(userId);
            flower.setSeedId(sunflower.getId());
            flower.setSunlight(0);
            flower.setWater(0);
            flower.setNutrient(0);
            flower.setGrowthValue(0);
            flower.setStage(0);
            flower.setIsUnlocked(0);
            flower.setCreatedAt(LocalDateTime.now());
            flower.setUpdatedAt(LocalDateTime.now());
            flowerMapper.insert(flower);

            return toVO(flower, sunflower);
        }

        // 3. 已有花卉 → 检查是否已开花（stage=3 → growthPercent >= 75）
        Seed currentSeed = seedMapper.selectById(currentFlower.getSeedId());
        if (currentSeed != null) {
            int totalMax = currentSeed.getSunlightMax() + currentSeed.getWaterMax() + currentSeed.getNutrientMax();
            int totalCur = currentFlower.getSunlight() + currentFlower.getWater() + currentFlower.getNutrient();
            int percent = totalMax > 0 ? totalCur * 100 / totalMax : 0;
            if (percent < 75) {
                throw new RuntimeException("当前花卉尚未培育完成，无法购买新种子");
            }
        } else {
            if (currentFlower.getGrowthValue() < 100) {
                throw new RuntimeException("当前花卉尚未培育完成，无法购买新种子");
            }
        }

        // 4. 验证要购买的种子
        Seed targetSeed = seedMapper.selectById(seedId);
        if (targetSeed == null) {
            throw new RuntimeException("种子不存在");
        }

        // 5. 扣积分
        pointsRecordService.deductPoints(userId, targetSeed.getPrice(), 5);

        // 6. 创建新花卉
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

        // 拷贝品种图片
        if (seed != null) {
            vo.setImage(seed.getImage());
            vo.setStage0Image(seed.getStage0Image());
            vo.setStage1Image(seed.getStage1Image());
            vo.setStage2Image(seed.getStage2Image());
            vo.setStage3Image(seed.getStage3Image());
        }

        return vo;
    }
}
