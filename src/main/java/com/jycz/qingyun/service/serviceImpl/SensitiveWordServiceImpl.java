package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jycz.qingyun.model.entity.SensitiveWord;
import com.jycz.qingyun.model.vo.SensitiveWordVO;
import com.jycz.qingyun.mapper.SensitiveWordMapper;
import com.jycz.qingyun.service.SensitiveWordService;
import com.jycz.qingyun.utils.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensitiveWordServiceImpl implements SensitiveWordService {

    private final SensitiveWordMapper sensitiveWordMapper;

    @Override
    @Transactional
    public SensitiveWordVO addSensitiveWord(String word) {
        // 检查是否已存在
        LambdaQueryWrapper<SensitiveWord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SensitiveWord::getWord, word);
        if (sensitiveWordMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(409, "敏感词已存在");
        }

        SensitiveWord entity = new SensitiveWord();
        entity.setWord(word);
        sensitiveWordMapper.insert(entity);

        log.info("敏感词添加成功: id={}, word={}", entity.getId(), word);

        return SensitiveWordVO.builder()
                .id(entity.getId())
                .word(entity.getWord())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public void deleteSensitiveWord(Long id) {
        SensitiveWord entity = sensitiveWordMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(404, "敏感词不存在");
        }

        sensitiveWordMapper.deleteById(id);
        log.info("敏感词删除成功: id={}, word={}", id, entity.getWord());
    }

    @Override
    public List<SensitiveWordVO> listSensitiveWords() {
        List<SensitiveWord> list = sensitiveWordMapper.selectList(
                new LambdaQueryWrapper<SensitiveWord>().orderByAsc(SensitiveWord::getWord)
        );

        return list.stream().map(entity ->
                SensitiveWordVO.builder()
                        .id(entity.getId())
                        .word(entity.getWord())
                        .createdAt(entity.getCreatedAt())
                        .build()
        ).collect(Collectors.toList());
    }
}