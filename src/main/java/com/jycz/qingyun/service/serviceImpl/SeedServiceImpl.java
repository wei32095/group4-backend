package com.jycz.qingyun.service.serviceImpl;

import com.jycz.qingyun.mapper.SeedMapper;
import com.jycz.qingyun.model.entity.Seed;
import com.jycz.qingyun.model.vo.SeedVO;
import com.jycz.qingyun.service.SeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeedServiceImpl implements SeedService {

    @Autowired
    private SeedMapper seedMapper;

    @Override
    public List<SeedVO> getSeedList() {
        List<Seed> seeds = seedMapper.selectList(null);
        return seeds.stream().map(this::toVO).collect(Collectors.toList());
    }

    private SeedVO toVO(Seed seed) {
        SeedVO vo = new SeedVO();
        vo.setId(seed.getId());
        vo.setVariety(seed.getVariety());
        vo.setDescription(seed.getDescription());
        vo.setMaxGrowth(seed.getMaxGrowth());
        vo.setImage(seed.getImage());
        vo.setStage0Image(seed.getStage0Image());
        vo.setStage1Image(seed.getStage1Image());
        vo.setStage2Image(seed.getStage2Image());
        vo.setStage3Image(seed.getStage3Image());
        vo.setStage4Image(seed.getStage4Image());
        vo.setPrice(seed.getPrice());
        return vo;
    }
}
