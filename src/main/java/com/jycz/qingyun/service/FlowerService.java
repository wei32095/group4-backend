package com.jycz.qingyun.service;

import com.jycz.qingyun.model.vo.FlowerListVO;

public interface FlowerService {
    FlowerListVO getMyFlowers(Long userId, int page, int size);
}
