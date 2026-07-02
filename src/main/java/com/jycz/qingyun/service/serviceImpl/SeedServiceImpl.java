package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jycz.qingyun.mapper.SeedMapper;
import com.jycz.qingyun.model.dto.AddSeedRequest;
import com.jycz.qingyun.model.dto.UpdateSeedRequest;
import com.jycz.qingyun.model.entity.Seed;
import com.jycz.qingyun.model.vo.SeedVO;
import com.jycz.qingyun.service.SeedService;
import com.jycz.qingyun.utils.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeedServiceImpl implements SeedService {

    @Autowired
    private SeedMapper seedMapper;

    @Override
    public List<SeedVO> getSeedList() {
        // 公开列表仅返回未删除的种子
        LambdaQueryWrapper<Seed> wrapper = new LambdaQueryWrapper<Seed>()
                .eq(Seed::getIsDeleted, 0);
        List<Seed> seeds = seedMapper.selectList(wrapper);
        return seeds.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public SeedVO addSeed(AddSeedRequest request) {
        // 校验品种名称是否已存在（不含已删除的）
        Long count = seedMapper.selectCount(
                new LambdaQueryWrapper<Seed>()
                        .eq(Seed::getVariety, request.getVariety())
                        .eq(Seed::getIsDeleted, 0));
        if (count > 0) {
            throw new RuntimeException("品种 '" + request.getVariety() + "' 已存在");
        }

        Seed seed = new Seed();
        seed.setVariety(request.getVariety());
        seed.setDescription(request.getDescription());
        seed.setSunlightMax(request.getSunlightMax());
        seed.setWaterMax(request.getWaterMax());
        seed.setNutrientMax(request.getNutrientMax());
        seed.setImage(request.getImage());
        seed.setStage0Image(request.getStage0Image());
        seed.setStage1Image(request.getStage1Image());
        seed.setStage2Image(request.getStage2Image());
        seed.setStage3Image(request.getStage3Image());
        seed.setPrice(request.getPrice());
        seed.setIsDeleted(0);
        seed.setCreatedAt(LocalDateTime.now());

        seedMapper.insert(seed);
        return toVO(seed);
    }

    @Override
    public SeedVO updateSeed(Long id, UpdateSeedRequest request) {
        Seed existing = seedMapper.selectById(id);
        if (existing == null) {
            return null;
        }

        // 部分更新：只拷贝非 null 字段
        copyNonNullProperties(request, existing);

        seedMapper.updateById(existing);
        return toVO(existing);
    }

    @Override
    public boolean deleteSeed(Long id) {
        Seed existing = seedMapper.selectById(id);
        if (existing == null) {
            return false;
        }

        if (existing.getIsDeleted() == 1) {
            throw new BusinessException(400, "该种子已被禁用，无需重复操作");
        }

        existing.setIsDeleted(1);
        seedMapper.updateById(existing);
        return true;
    }

    @Override
    public List<SeedVO> listAllSeeds() {
        // 管理端列表返回全部种子（含已删除）
        List<Seed> seeds = seedMapper.selectList(null);
        return seeds.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public SeedVO getSeedById(Long id) {
        Seed seed = seedMapper.selectById(id);
        return seed != null ? toVO(seed) : null;
    }

    @Override
    public SeedVO restoreSeed(Long id) {
        Seed existing = seedMapper.selectById(id);
        if (existing == null) {
            return null;
        }

        if (existing.getIsDeleted() == 0) {
            throw new BusinessException(400, "该种子未被禁用，无需恢复");
        }

        existing.setIsDeleted(0);
        seedMapper.updateById(existing);
        return toVO(existing);
    }

    private SeedVO toVO(Seed seed) {
        SeedVO vo = new SeedVO();
        vo.setId(seed.getId());
        vo.setVariety(seed.getVariety());
        vo.setDescription(seed.getDescription());
        vo.setSunlightMax(seed.getSunlightMax());
        vo.setWaterMax(seed.getWaterMax());
        vo.setNutrientMax(seed.getNutrientMax());
        vo.setImage(seed.getImage());
        vo.setStage0Image(seed.getStage0Image());
        vo.setStage1Image(seed.getStage1Image());
        vo.setStage2Image(seed.getStage2Image());
        vo.setStage3Image(seed.getStage3Image());
        vo.setPrice(seed.getPrice());
        vo.setIsDeleted(seed.getIsDeleted());
        return vo;
    }

    /**
     * 将 source 中非 null 的属性复制到 target 中（支持 String / Integer 字段）
     */
    private void copyNonNullProperties(Object source, Object target) {
        for (Field field : source.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(source);
                if (value != null) {
                    Field targetField = target.getClass().getDeclaredField(field.getName());
                    targetField.setAccessible(true);
                    targetField.set(target, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // 字段不存在或无法访问，跳过
            }
        }
    }
}
