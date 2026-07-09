package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jycz.qingyun.model.dto.VoteCreateRequest;
import com.jycz.qingyun.model.dto.VoteSubmitRequest;
import com.jycz.qingyun.model.entity.Class;
import com.jycz.qingyun.model.entity.ClassVote;
import com.jycz.qingyun.model.entity.CourseStudent;
import com.jycz.qingyun.model.entity.VoteRecord;
import com.jycz.qingyun.model.vo.VoteListVO;
import com.jycz.qingyun.model.vo.VoteCreateVO;
import com.jycz.qingyun.model.vo.VoteResultVO;
import com.jycz.qingyun.model.vo.VoteSubmitVO;
import com.jycz.qingyun.mapper.ClassMapper;
import com.jycz.qingyun.mapper.ClassVoteMapper;
import com.jycz.qingyun.mapper.CourseStudentMapper;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final ClassVoteMapper classVoteMapper;
    private final ClassMapper classMapper;
    private final VoteRecordMapper voteRecordMapper;
    private final CourseStudentMapper courseStudentMapper;
    private final ObjectMapper objectMapper;
    private final PointsRecordService pointsRecordService;

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
        ClassVote vote = classVoteMapper.selectById(request.getVoteId());
        if (vote == null) {
            throw new BusinessException(404, "投票不存在");
        }

        if (vote.getEndedAt() != null && LocalDateTime.now().isAfter(vote.getEndedAt())) {
            throw new BusinessException(400, "投票已结束");
        }

        LambdaQueryWrapper<VoteRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VoteRecord::getVoteId, request.getVoteId())
                .eq(VoteRecord::getUserId, studentId);
        if (voteRecordMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(409, "您已投过票");
        }

        Integer isCorrect = 0;
        if (vote.getCorrectOption() != null &&
                vote.getCorrectOption().equals(request.getSelectedOption())) {
            isCorrect = 1;
        }

        VoteRecord record = new VoteRecord();
        record.setVoteId(request.getVoteId());
        record.setUserId(studentId);
        record.setSelectedOption(request.getSelectedOption());
        record.setIsCorrect(isCorrect);
        record.setSubmittedAt(LocalDateTime.now());
        voteRecordMapper.insert(record);

        if (isCorrect == 1) {
            pointsRecordService.handleVoteCorrectPoints(studentId);
        }

        log.info("投票提交成功: voteId={}, studentId={}, option={}, isCorrect={}",
                request.getVoteId(), studentId, request.getSelectedOption(), isCorrect);

        return VoteSubmitVO.builder()
                .voteId(request.getVoteId())
                .selectedOption(request.getSelectedOption())
                .isCorrect(isCorrect)
                .correctOption(vote.getCorrectOption())
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

    /**
     * 获取投票列表（学生和老师通用）
     */
    @Override
    public List<VoteListVO> getVoteList(Long classId, Long userId, Integer role) {
        // 1. 查询课堂是否存在
        Class clazz = classMapper.selectById(classId);
        if (clazz == null) {
            throw new BusinessException(404, "课堂不存在");
        }

        // 2. 权限校验：学生必须已加入课程，老师必须是该课堂的老师
        if (role == 1) {
            // 学生：校验是否已加入课程
            LambdaQueryWrapper<CourseStudent> csWrapper = new LambdaQueryWrapper<>();
            csWrapper.eq(CourseStudent::getCourseId, clazz.getCourseId())
                    .eq(CourseStudent::getUserId, userId);
            if (courseStudentMapper.selectCount(csWrapper) == 0) {
                throw new BusinessException(403, "您未加入该课程");
            }
        } else if (role == 2) {
            // 老师：校验是否是该课堂的老师
            if (!clazz.getUserId().equals(userId)) {
                throw new BusinessException(403, "您不是该课堂的老师");
            }
        } else {
            throw new BusinessException(403, "无权查看");
        }

        // 3. 查询该课堂所有投票
        LambdaQueryWrapper<ClassVote> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassVote::getClassId, classId)
                .orderByDesc(ClassVote::getCreatedAt);
        List<ClassVote> votes = classVoteMapper.selectList(wrapper);

        if (votes.isEmpty()) {
            return new ArrayList<>();
        }

        // 4. 获取该学生的投票记录（老师不需要）
        Set<Long> votedVoteIds = new HashSet<>();
        Map<Long, VoteRecord> userVoteMap = new HashMap<>();

        if (role == 1) {
            // 学生：查询已投票记录
            List<Long> voteIds = votes.stream().map(ClassVote::getId).collect(Collectors.toList());
            LambdaQueryWrapper<VoteRecord> recordWrapper = new LambdaQueryWrapper<>();
            recordWrapper.in(VoteRecord::getVoteId, voteIds)
                    .eq(VoteRecord::getUserId, userId);
            List<VoteRecord> records = voteRecordMapper.selectList(recordWrapper);

            for (VoteRecord record : records) {
                votedVoteIds.add(record.getVoteId());
            }
            userVoteMap = records.stream()
                    .collect(Collectors.toMap(
                            VoteRecord::getVoteId,
                            r -> r,
                            (existing, replacement) -> replacement
                    ));
        }

        // 5. 组装结果
        List<VoteListVO> result = new ArrayList<>();
        for (ClassVote vote : votes) {
            List<String> options;
            try {
                options = objectMapper.readValue(vote.getOptions(), new TypeReference<List<String>>() {});
            } catch (Exception e) {
                continue;
            }

            // 计算实际状态
            String status = vote.getStatus();
            if (vote.getEndedAt() != null && LocalDateTime.now().isAfter(vote.getEndedAt())) {
                status = "ended";
            }

            VoteListVO.VoteListVOBuilder builder = VoteListVO.builder()
                    .voteId(vote.getId())
                    .heading(vote.getHeading())
                    .options(options)
                    .status(status)
                    .endedAt(vote.getEndedAt())
                    .correctOption(vote.getCorrectOption());

            // 学生才需要返回投票状态，老师不需要
            if (role == 1) {
                VoteRecord userVote = userVoteMap.get(vote.getId());
                if (userVote != null) {
                    builder.hasVoted(true)
                            .selectedOption(userVote.getSelectedOption());
                } else {
                    builder.hasVoted(false)
                            .selectedOption(null);
                }
            } else {
                // 老师：不需要这些字段
                builder.hasVoted(null)
                        .selectedOption(null);
            }

            result.add(builder.build());
        }

        return result;
    }
}