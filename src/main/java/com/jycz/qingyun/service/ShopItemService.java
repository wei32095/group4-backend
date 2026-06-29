package com.jycz.qingyun.service;

import com.jycz.qingyun.model.vo.ShopItemVO;
import com.jycz.qingyun.model.vo.UseItemVO;
import com.jycz.qingyun.model.vo.UserItemVO;

import java.util.List;

public interface ShopItemService {
    List<ShopItemVO> getShopList();
    List<UserItemVO> getBag(Long userId);
    void exchangeItem(Long userId, Long itemId);
    UseItemVO useItem(Long userId, Long flowerId, Long itemId);
}
