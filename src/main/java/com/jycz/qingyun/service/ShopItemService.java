package com.jycz.qingyun.service;

import com.jycz.qingyun.model.vo.ShopItemVO;

import java.util.List;

public interface ShopItemService {
    List<ShopItemVO> getShopList();
    void exchangeItem(Long userId, Long itemId);
}
