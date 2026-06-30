package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jycz.qingyun.mapper.FlowerMapper;
import com.jycz.qingyun.mapper.PointsRecordMapper;
import com.jycz.qingyun.mapper.SeedMapper;
import com.jycz.qingyun.mapper.ShopItemMapper;
import com.jycz.qingyun.model.entity.Flower;
import com.jycz.qingyun.model.entity.PointsRecord;
import com.jycz.qingyun.model.entity.Seed;
import com.jycz.qingyun.model.entity.ShopItem;
import com.jycz.qingyun.model.vo.ShopItemVO;
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
    private PointsRecordMapper pointsRecordMapper;

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
    public void exchangeItem(Long userId, Long itemId) {
        // 1. 查询道具
        ShopItem item = shopItemMapper.selectById(itemId);
        if (item == null) {
            throw new RuntimeException("道具不存在");
        }

        // 2. 查询当前积分
        Integer currentPoints = pointsRecordMapper.getLatestPoints(userId);
        if (currentPoints == null) {
            currentPoints = 0;
        }

        // 3. 校验积分是否足够
        if (currentPoints < item.getPrice()) {
            throw new RuntimeException("积分不足");
        }

        // 4. 扣除积分并记录流水
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setChangeType(2);
        record.setChangePoints(item.getPrice());
        record.setLeftPoints(currentPoints - item.getPrice());
        record.setSourceType(5);
        record.setChangeTime(LocalDateTime.now());
        pointsRecordMapper.insert(record);

        // 5. 查找用户当前正在培育的花卉（最新一株）
        LambdaQueryWrapper<Flower> wrapper = new LambdaQueryWrapper<Flower>()
                .eq(Flower::getUserId, userId)
                .orderByDesc(Flower::getCreatedAt)
                .last("LIMIT 1");
        List<Flower> flowerList = flowerMapper.selectList(wrapper);
        Flower flower = flowerList.isEmpty() ? null : flowerList.get(0);

        if (flower == null) {
            throw new RuntimeException("请先在种子商店购买种子");
        }

        // 6. 检查当前花卉是否已培育完成
        Seed seed = seedMapper.selectById(flower.getSeedId());
        int maxGrowth = (seed != null) ? seed.getMaxGrowth() : 100;
        if (flower.getGrowthValue() >= maxGrowth) {
            throw new RuntimeException("当前花卉已培育完成，无需使用道具");
        }

        // 7. 增加生长值，上限为该品种 max_growth
        int newGrowth = Math.min(maxGrowth, flower.getGrowthValue() + item.getGrowthValue());
        flower.setGrowthValue(newGrowth);
        flower.setUpdatedAt(LocalDateTime.now());
        flowerMapper.updateById(flower);
    }

    private ShopItemVO toVO(ShopItem item) {
        ShopItemVO vo = new ShopItemVO();
        vo.setId(item.getId());
        vo.setItemName(item.getItemName());
        vo.setIcon(item.getIcon());
        vo.setPrice(item.getPrice());
        vo.setGrowthValue(item.getGrowthValue());
        return vo;
    }
}
