package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jycz.qingyun.model.dto.VoteCreateRequest;
import com.jycz.qingyun.model.dto.VoteSubmitRequest;
import com.jycz.qingyun.model.entity.Class;
import com.jycz.qingyun.model.entity.ClassVote;
import com.jycz.qingyun.model.entity.VoteRecord;
import com.jycz.qingyun.model.vo.VoteCreateVO;
import com.jycz.qingyun.model.vo.VoteResultVO;
import com.jycz.qingyun.model.vo.VoteSubmitVO;
import com.jycz.qingyun.mapper.ClassMapper;
import com.jycz.qingyun.mapper.ClassVoteMapper;
import com.jycz.qingyun.mapper.VoteRecordMapper;
import com.jycz.qingyun.service.PointsRecordService;
import com.jycz.qingyun.service.VoteService;
import com.jycz.qingyun.utils.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service//
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final ClassVoteMapper classVoteMapper;
    private final ClassMapper classMapper;
    private final VoteRecordMapper voteRecordMapper;
    private final ObjectMapper objectMapper;
    private final PointsRecordService pointsRecordService;  // ← 新增
    @Override
    @Transactional
    public VoteCreateVO createVote(VoteCreateRequest request, Long teacherId) {
        Class clazz = classMapper.selectById(request.getClassId());
        if (clazz == null) {
            throw new BusinessException(404, "课堂不存在");
        }

        if (!clazz.getUserId().equals(teacherId)) {
            throw new BusinessException(403, "您不是该课堂的老师");
        }

        if ("ended".equals(clazz.getStatus())) {
            throw new BusinessException(400, "课堂已结束，无法发起投票");
        }

        String optionsJson;
        try {
            optionsJson = objectMapper.writeValueAsString(request.getOptions());
        } catch (Exception e) {
            throw new BusinessException(500, "选项格式错误");
        }

        int duration = request.getDuration() != null ? request.getDuration() : 120;

        ClassVote vote = new ClassVote();
        vote.setClassId(request.getClassId());
        vote.setHeading(request.getHeading());
        vote.setOptions(optionsJson);
        vote.setCorrectOption(request.getCorrectOption());
        vote.setDuration(duration);
        vote.setStatus("active");
        vote.setCreatedAt(LocalDateTime.now());
        vote.setEndedAt(LocalDateTime.now().plusSeconds(duration));

        classVoteMapper.insert(vote);
        log.info("投票创建成功: voteId={}, classId={}", vote.getId(), request.getClassId());

        return VoteCreateVO.builder()
                .id(vote.getId())
                .classId(vote.getClassId())
                .heading(vote.getHeading())
                .options(request.getOptions())
                .correctOption(vote.getCorrectOption())
                .duration(vote.getDuration())
                .status(vote.getStatus())
                .createdAt(vote.getCreatedAt())
                .endedAt(vote.getEndedAt())
                .build();
    }

    @Override
    @Transactional
    public VoteSubmitVO submitVote(VoteSubmitRequest request, Long studentId) {
        // 1. 查询投票
        ClassVote vote = classVoteMapper.selectById(request.getVoteId());
        if (vote == null) {
            throw new BusinessException(404, "投票不存在");
        }

        // 2. ✅ 校验投票是否已结束（直接比较时间）
        if (LocalDateTime.now().isAfter(vote.getEndedAt())) {
            throw new BusinessException(400, "投票已结束");
        }

        // 3. 检查是否已投过票
        LambdaQueryWrapper<VoteRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VoteRecord::getVoteId, request.getVoteId())
                .eq(VoteRecord::getUserId, studentId);
        if (voteRecordMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(409, "您已投过票");
        }

        // 4. 判断是否正确
        Integer isCorrect = 0;
        if (vote.getCorrectOption() != null &&
                vote.getCorrectOption().equals(request.getSelectedOption())) {
            isCorrect = 1;
        }

        // 5. 保存投票记录
        VoteRecord record = new VoteRecord();
        record.setVoteId(request.getVoteId());
        record.setUserId(studentId);
        record.setSelectedOption(request.getSelectedOption());
        record.setIsCorrect(isCorrect);
        record.setSubmittedAt(LocalDateTime.now());
        voteRecordMapper.insert(record);

        // 6. 投票正确积分处理
        if (isCorrect == 1) {
            pointsRecordService.handleVoteCorrectPoints(studentId);
        }

        log.info("投票提交成功: voteId={}, studentId={}, option={}, isCorrect={}",
                request.getVoteId(), studentId, request.getSelectedOption(), isCorrect);

        return VoteSubmitVO.builder()
                .voteId(request.getVoteId())
                .selectedOption(request.getSelectedOption())
                .isCorrect(isCorrect)
                .build();
    }

    @Override
    public VoteResultVO getVoteResult(Long voteId, Long teacherId) {
        ClassVote vote = classVoteMapper.selectById(voteId);
        if (vote == null) {
            throw new BusinessException(404, "投票不存在");
        }

        Class clazz = classMapper.selectById(vote.getClassId());
        if (!clazz.getUserId().equals(teacherId)) {
            throw new BusinessException(403, "您不是该课堂的老师");
        }

        List<String> options;
        try {
            options = objectMapper.readValue(vote.getOptions(), new TypeReference<List<String>>() {});
        } catch (Exception e) {
            throw new BusinessException(500, "选项解析失败");
        }

        LambdaQueryWrapper<VoteRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VoteRecord::getVoteId, voteId);
        List<VoteRecord> records = voteRecordMapper.selectList(wrapper);
        int totalVoters = records.size();

        Map<String, Integer> optionCountMap = new HashMap<>();
        for (String opt : options) {
            optionCountMap.put(opt, 0);
        }
        for (VoteRecord record : records) {
            String selected = record.getSelectedOption();
            optionCountMap.put(selected, optionCountMap.getOrDefault(selected, 0) + 1);
        }

        int correctCount = 0;
        for (VoteRecord record : records) {
            if (record.getIsCorrect() == 1) {
                correctCount++;
            }
        }
        double correctRate = totalVoters > 0 ?
                Math.round((double) correctCount / totalVoters * 100 * 100) / 100.0 : 0.0;

        List<VoteResultVO.OptionStatVO> statistics = new ArrayList<>();
        for (String opt : options) {
            Integer count = optionCountMap.getOrDefault(opt, 0);
            double percentage = totalVoters > 0 ?
                    Math.round((double) count / totalVoters * 100 * 100) / 100.0 : 0.0;
            statistics.add(VoteResultVO.OptionStatVO.builder()
                    .option(opt)
                    .count(count)
                    .percentage(percentage)
                    .build());
        }

        return VoteResultVO.builder()
                .id(vote.getId())
                .heading(vote.getHeading())
                .options(options)
                .totalVoters(totalVoters)
                .correctOption(vote.getCorrectOption())
                .statistics(statistics)
                .correctRate(correctRate)
                .build();
    }
}