package com.jycz.qingyun.service;

import com.jycz.qingyun.model.vo.SensitiveWordVO;

import java.util.List;

public interface SensitiveWordService {

    SensitiveWordVO addSensitiveWord(String word);

    void deleteSensitiveWord(Long id);

    List<SensitiveWordVO> listSensitiveWords();
}