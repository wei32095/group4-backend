package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jycz.qingyun.mapper.FlowerMapper;
import com.jycz.qingyun.mapper.SeedMapper;
import com.jycz.qingyun.mapper.ShopItemMapper;
import com.jycz.qingyun.model.entity.Flower;
import com.jycz.qingyun.model.entity.Seed;
import com.jycz.qingyun.model.entity.ShopItem;
import com.jycz.qingyun.model.vo.FlowerVO;
import com.jycz.qingyun.model.vo.ShopItemVO;
import com.jycz.qingyun.service.PointsRecordService;
import com.jycz.qingyun.service.ShopItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShopItemServiceImpl implements ShopItemService {

    @Autowired
    private ShopItemMapper shopItemMapper;

    @Autowired
    private PointsRecordService pointsRecordService;

    @Autowired
    private FlowerMapper flowerMapper;

    @Autowired
    private SeedMapper seedMapper;

    @Override
    public List<ShopItemVO> getShopList() {
        List<ShopItem> items = shopItemMapper.selectList(null);
        return items.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FlowerVO exchangeItem(Long userId, Long itemId) {
        // 1. 查询道具
        ShopItem item = shopItemMapper.selectById(itemId);
        if (item == null) {
            throw new RuntimeException("道具不存在");
        }

        // 2. 扣积分
        pointsRecordService.deductPoints(userId, item.getPrice(), 5);

        // 3. 查找用户当前正在培育的花卉（最新一株）
        LambdaQueryWrapper<Flower> wrapper = new LambdaQueryWrapper<Flower>()
                .eq(Flower::getUserId, userId)
                .orderByDesc(Flower::getCreatedAt)
                .last("LIMIT 1");
        List<Flower> flowerList = flowerMapper.selectList(wrapper);
        Flower flower = flowerList.isEmpty() ? null : flowerList.get(0);

        if (flower == null) {
            throw new RuntimeException("请先在种子商店购买种子");
        }

        // 4. 查询品种信息获取满值
        Seed seed = seedMapper.selectById(flower.getSeedId());
        if (seed == null) {
            throw new RuntimeException("花卉品种不存在");
        }

        int attributeType = item.getAttributeType();
        int boost = item.getBoostValue() != null ? item.getBoostValue() : 10;
        boolean updated = false;

        // 5. 根据属性类型增加对应值（不超过满值）
        if (attributeType == 1) {
            // 阳光
            int max = seed.getSunlightMax() != null ? seed.getSunlightMax() : 100;
            int current = flower.getSunlight() != null ? flower.getSunlight() : 0;
            if (current >= max) {
                throw new RuntimeException("阳光值已满，无需使用此道具");
            }
            flower.setSunlight(Math.min(max, current + boost));
            updated = true;
        } else if (attributeType == 2) {
            // 水分
            int max = seed.getWaterMax() != null ? seed.getWaterMax() : 100;
            int current = flower.getWater() != null ? flower.getWater() : 0;
            if (current >= max) {
                throw new RuntimeException("水分值已满，无需使用此道具");
            }
            flower.setWater(Math.min(max, current + boost));
            updated = true;
        } else if (attributeType == 3) {
            // 养份
            int max = seed.getNutrientMax() != null ? seed.getNutrientMax() : 100;
            int current = flower.getNutrient() != null ? flower.getNutrient() : 0;
            if (current >= max) {
                throw new RuntimeException("养份值已满，无需使用此道具");
            }
            flower.setNutrient(Math.min(max, current + boost));
            updated = true;
        }

        if (!updated) {
            throw new RuntimeException("未知的道具属性类型");
        }

        // 6. 更新 growth_value（冗余）和 stage
        int totalMax = (seed.getSunlightMax() != null ? seed.getSunlightMax() : 100)
                + (seed.getWaterMax() != null ? seed.getWaterMax() : 100)
                + (seed.getNutrientMax() != null ? seed.getNutrientMax() : 100);
        int totalCur = (flower.getSunlight() != null ? flower.getSunlight() : 0)
                + (flower.getWater() != null ? flower.getWater() : 0)
                + (flower.getNutrient() != null ? flower.getNutrient() : 0);
        int percent = totalMax > 0 ? totalCur * 100 / totalMax : 0;

        flower.setGrowthValue(totalCur);
        if (percent < 25) {
            flower.setStage(0);
        } else if (percent < 50) {
            flower.setStage(1);
        } else if (percent < 75) {
            flower.setStage(2);
        } else {
            flower.setStage(3);
        }
        flower.setUpdatedAt(LocalDateTime.now());
        flowerMapper.updateById(flower);

        // 8. 返回更新后的花卉信息
        return toFlowerVO(flower, seed);
    }

    private FlowerVO toFlowerVO(Flower flower, Seed seed) {
        FlowerVO vo = new FlowerVO();
        vo.setId(flower.getId());
        vo.setSeedId(flower.getSeedId());
        vo.setVariety(seed != null ? seed.getVariety() : "未知");
        vo.setSunlight(flower.getSunlight() != null ? flower.getSunlight() : 0);
        vo.setWater(flower.getWater() != null ? flower.getWater() : 0);
        vo.setNutrient(flower.getNutrient() != null ? flower.getNutrient() : 0);

        int sunMax = seed != null && seed.getSunlightMax() != null ? seed.getSunlightMax() : 100;
        int waterMax = seed != null && seed.getWaterMax() != null ? seed.getWaterMax() : 100;
        int nutMax = seed != null && seed.getNutrientMax() != null ? seed.getNutrientMax() : 100;
        int totalMax = sunMax + waterMax + nutMax;
        int totalCur = vo.getSunlight() + vo.getWater() + vo.getNutrient();
        int percent = totalMax > 0 ? totalCur * 100 / totalMax : 0;
        vo.setGrowthPercent(percent);

        if (percent < 25) vo.setStage(0);
        else if (percent < 50) vo.setStage(1);
        else if (percent < 75) vo.setStage(2);
        else vo.setStage(3);

        vo.setIsUnlocked(flower.getIsUnlocked());
        vo.setCreatedAt(flower.getCreatedAt());
        return vo;
    }

    private ShopItemVO toVO(ShopItem item) {
        ShopItemVO vo = new ShopItemVO();
        vo.setId(item.getId());
        vo.setItemName(item.getItemName());
        vo.setIcon(item.getIcon());
        vo.setPrice(item.getPrice());
        vo.setAttributeType(item.getAttributeType());
        vo.setBoostValue(item.getBoostValue());
        return vo;
    }
}
