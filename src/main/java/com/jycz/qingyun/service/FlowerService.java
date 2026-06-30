package com.jycz.qingyun.service;

import com.jycz.qingyun.model.vo.FlowerListVO;
import com.jycz.qingyun.model.vo.FlowerVO;

public interface FlowerService {
    FlowerListVO getMyFlowers(Long userId, int page, int size);

    FlowerVO plant(Long userId, Long seedId);
}
