package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jycz.qingyun.mapper.FlowerMapper;
import com.jycz.qingyun.mapper.PointsRecordMapper;
import com.jycz.qingyun.mapper.ShopItemMapper;
import com.jycz.qingyun.mapper.UserItemMapper;
import com.jycz.qingyun.model.entity.Flower;
import com.jycz.qingyun.model.entity.PointsRecord;
import com.jycz.qingyun.model.entity.ShopItem;
import com.jycz.qingyun.model.entity.UserItem;
import com.jycz.qingyun.model.vo.ShopItemVO;
import com.jycz.qingyun.model.vo.UseItemVO;
import com.jycz.qingyun.model.vo.UserItemVO;
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
    private UserItemMapper userItemMapper;

    @Override
    public List<ShopItemVO> getShopList() {
        List<ShopItem> items = shopItemMapper.selectList(null);
        return items.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<UserItemVO> getBag(Long userId) {
        return userItemMapper.selectUserBag(userId);
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
        record.setChangeType(2);          // 消耗
        record.setChangePoints(item.getPrice());
        record.setLeftPoints(currentPoints - item.getPrice());
        record.setSourceType(5);           // 道具兑换
        record.setChangeTime(LocalDateTime.now());
        pointsRecordMapper.insert(record);

        // 5. 放入背包
        LambdaQueryWrapper<UserItem> wrapper = new LambdaQueryWrapper<UserItem>()
                .eq(UserItem::getUserId, userId)
                .eq(UserItem::getItemId, itemId);
        UserItem userItem = userItemMapper.selectOne(wrapper);

        if (userItem == null) {
            userItem = new UserItem();
            userItem.setUserId(userId);
            userItem.setItemId(itemId);
            userItem.setQuantity(1);
            userItem.setCreatedAt(LocalDateTime.now());
            userItem.setUpdatedAt(LocalDateTime.now());
            userItemMapper.insert(userItem);
        } else {
            userItem.setQuantity(userItem.getQuantity() + 1);
            userItem.setUpdatedAt(LocalDateTime.now());
            userItemMapper.updateById(userItem);
        }
    }

    @Override
    @Transactional
    public UseItemVO useItem(Long userId, Long flowerId, Long itemId) {
        // 1. 查背包
        LambdaQueryWrapper<UserItem> bagWrapper = new LambdaQueryWrapper<UserItem>()
                .eq(UserItem::getUserId, userId)
                .eq(UserItem::getItemId, itemId);
        UserItem userItem = userItemMapper.selectOne(bagWrapper);
        if (userItem == null || userItem.getQuantity() <= 0) {
            throw new RuntimeException("道具数量不足");
        }

        // 2. 查道具信息
        ShopItem item = shopItemMapper.selectById(itemId);
        if (item == null) {
            throw new RuntimeException("道具不存在");
        }

        // 3. 查花卉
        Flower flower = flowerMapper.selectById(flowerId);
        if (flower == null) {
            throw new RuntimeException("花卉不存在");
        }
        if (!flower.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该花卉");
        }

        // 4. 计算生长值（超过 100 的部分丢弃）
        int newGrowth = Math.min(100, flower.getGrowthGrowthValue() + item.getGrowthValue());

        // 5. 更新花卉
        flower.setGrowthGrowthValue(newGrowth);
        flower.setUpdatedAt(LocalDateTime.now());
        flowerMapper.updateById(flower);

        // 6. 扣减背包数量
        if (userItem.getQuantity() <= 1) {
            userItemMapper.deleteById(userItem.getId());
        } else {
            userItem.setQuantity(userItem.getQuantity() - 1);
            userItem.setUpdatedAt(LocalDateTime.now());
            userItemMapper.updateById(userItem);
        }

        // 7. 返回 VO
        UseItemVO vo = new UseItemVO();
        vo.setItemName(item.getItemName());
        vo.setGrowthValue(item.getGrowthValue());
        vo.setCurrentGrowth(newGrowth);
        return vo;
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
