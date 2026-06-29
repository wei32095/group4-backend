package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jycz.qingyun.mapper.FlowerMapper;
import com.jycz.qingyun.model.entity.Flower;
import com.jycz.qingyun.model.vo.FlowerListVO;
import com.jycz.qingyun.model.vo.FlowerVO;
import com.jycz.qingyun.service.FlowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlowerServiceImpl implements FlowerService {

    @Autowired
    private FlowerMapper flowerMapper;

    @Override
    public FlowerListVO getMyFlowers(Long userId, int page, int size) {
        // 1. 分页查询用户的花卉，按创建时间倒序
        Page<Flower> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Flower> wrapper = new LambdaQueryWrapper<Flower>()
                .eq(Flower::getUserId, userId)
                .orderByDesc(Flower::getCreatedAt);

        Page<Flower> result = flowerMapper.selectPage(pageObj, wrapper);

        // 2. 转换 VO
        List<FlowerVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        // 3. 组装
        FlowerListVO listVO = new FlowerListVO();
        listVO.setLocation(voList);
        listVO.setTotal(result.getTotal());
        listVO.setPageNum((int) result.getCurrent());
        listVO.setPageSize((int) result.getSize());
        listVO.setPages((int) result.getPages());
        return listVO;
    }

    private FlowerVO toVO(Flower flower) {
        FlowerVO vo = new FlowerVO();
        vo.setId(flower.getId());
        vo.setVariety(flower.getVariety());
        vo.setStage(flower.getStage());
        vo.setCurrentValue(flower.getGrowthGrowthValue());
        vo.setIsUnlocked(flower.getIsUnlocked());
        vo.setCreatedAt(flower.getCreatedAt());
        return vo;
    }
}
