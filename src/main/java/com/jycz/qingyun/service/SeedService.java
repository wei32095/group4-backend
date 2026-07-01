package com.jycz.qingyun.service;

import com.jycz.qingyun.model.dto.AddSeedRequest;
import com.jycz.qingyun.model.dto.UpdateSeedRequest;
import com.jycz.qingyun.model.vo.SeedVO;

import java.util.List;

public interface SeedService {
    List<SeedVO> getSeedList();

    SeedVO addSeed(AddSeedRequest request);

    SeedVO updateSeed(Long id, UpdateSeedRequest request);

    boolean deleteSeed(Long id);

    List<SeedVO> listAllSeeds();

    SeedVO getSeedById(Long id);

    SeedVO restoreSeed(Long id);
}
