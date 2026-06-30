package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    public FlowerListVO getMyFlowers(Long userId, int page, int size) {
        // 1. 分页查询用户的花卉
        Page<Flower> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Flower> wrapper = new LambdaQueryWrapper<Flower>()
                .eq(Flower::getUserId, userId)
                .orderByDesc(Flower::getCreatedAt);

        Page<Flower> result = flowerMapper.selectPage(pageObj, wrapper);

        // 2. 收集所有 seedId，批量查询品种信息
        List<Long> seedIds = result.getRecords().stream()
                .map(Flower::getSeedId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Seed> seedMap = seedMapper.selectList(
                        new LambdaQueryWrapper<Seed>().in(Seed::getId, seedIds))
                .stream().collect(Collectors.toMap(Seed::getId, s -> s));

        // 3. 转换 VO
        List<FlowerVO> voList = result.getRecords().stream()
                .map(flower -> toVO(flower, seedMap.get(flower.getSeedId())))
                .collect(Collectors.toList());

        // 4. 组装
        FlowerListVO listVO = new FlowerListVO();
        listVO.setLocation(voList);
        listVO.setTotal(result.getTotal());
        listVO.setPageNum((int) result.getCurrent());
        listVO.setPageSize((int) result.getSize());
        listVO.setPages((int) result.getPages());
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
            flower.setGrowthValue(0);
            flower.setStage(0);
            flower.setIsUnlocked(0);
            flower.setCreatedAt(LocalDateTime.now());
            flower.setUpdatedAt(LocalDateTime.now());
            flowerMapper.insert(flower);

            return toVO(flower, sunflower);
        }

        // 3. 已有花卉 → 检查是否培育完成
        Seed currentSeed = seedMapper.selectById(currentFlower.getSeedId());
        int maxGrowth = currentSeed != null ? currentSeed.getMaxGrowth() : 100;

        if (currentFlower.getGrowthValue() < maxGrowth) {
            throw new RuntimeException("当前花卉尚未培育完成，无法购买新种子");
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
        vo.setVariety(seed != null ? seed.getVariety() : "未知");
        vo.setStage(flower.getStage());
        vo.setCurrentValue(flower.getGrowthValue());
        vo.setMaxGrowth(seed != null ? seed.getMaxGrowth() : 100);
        vo.setIsUnlocked(flower.getIsUnlocked());
        vo.setCreatedAt(flower.getCreatedAt());
        return vo;
    }
}
