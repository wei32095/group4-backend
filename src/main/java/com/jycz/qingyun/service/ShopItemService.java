package com.jycz.qingyun.service;

import com.jycz.qingyun.model.vo.FlowerVO;
import com.jycz.qingyun.model.vo.ShopItemVO;

import java.util.List;

public interface ShopItemService {
    List<ShopItemVO> getShopList();
    FlowerVO exchangeItem(Long userId, Long itemId);
}
