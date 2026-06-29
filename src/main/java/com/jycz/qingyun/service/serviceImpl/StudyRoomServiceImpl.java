package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jycz.qingyun.mapper.StudyRoomMapper;
import com.jycz.qingyun.model.dto.CreateStudyRoomRequest;
import com.jycz.qingyun.model.dto.EndStudyRoomRequest;
import com.jycz.qingyun.model.entity.StudyRoom;
import com.jycz.qingyun.model.vo.StudyRoomEndVO;
import com.jycz.qingyun.model.vo.StudyRoomRecordListVO;
import com.jycz.qingyun.model.vo.StudyRoomRecordVO;
import com.jycz.qingyun.model.vo.StudyRoomVO;
import com.jycz.qingyun.service.StudyRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudyRoomServiceImpl implements StudyRoomService {

    @Autowired
    private StudyRoomMapper studyRoomMapper;

    @Override
    public StudyRoomVO createStudyRoom(Long userId, CreateStudyRoomRequest request) {
        // 1. 参数校验
        if (request.getMode() == null) {
            throw new RuntimeException("学习模式不能为空");
        }
        if (request.getMode() != 1 && request.getMode() != 2) {
            throw new RuntimeException("学习模式无效");
        }
        if (request.getMode() == 2) {
            if (request.getPlanTime() == null || request.getPlanTime() <= 0) {
                throw new RuntimeException("倒计时模式必须设置计划时长");
            }
        }

        // 2. 检查是否有进行中的自习
        LambdaQueryWrapper<StudyRoom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudyRoom::getUserId, userId)
                .isNull(StudyRoom::getEndTime);
        Long count = studyRoomMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("您已有进行中的自习，请先结束");
        }

        // 3. 创建记录
        StudyRoom studyRoom = new StudyRoom();
        studyRoom.setUserId(userId);
        studyRoom.setMode(request.getMode());
        studyRoom.setPlanTime(request.getMode() == 2 ? request.getPlanTime() : null);
        studyRoom.setStartTime(LocalDateTime.now());
        studyRoom.setScreenSwitchCount(0);
        studyRoom.setIsValid(1);
        studyRoom.setCreatedAt(LocalDateTime.now());
        studyRoomMapper.insert(studyRoom);

        // 4. 返回 VO
        StudyRoomVO vo = new StudyRoomVO();
        vo.setId(studyRoom.getId());
        vo.setMode(studyRoom.getMode());
        vo.setStartTime(studyRoom.getStartTime());
        vo.setPlanTime(studyRoom.getPlanTime());
        return vo;
    }

    @Override
    public StudyRoomEndVO endStudyRoom(Long userId, EndStudyRoomRequest request) {
        // 1. 参数校验
        if (request.getId() == null) {
            throw new RuntimeException("自习记录ID不能为空");
        }
        if (request.getScreenSwitchCount() == null) {
            throw new RuntimeException("切屏次数不能为空");
        }
        if (request.getScreenSwitchCount() < 0) {
            throw new RuntimeException("切屏次数无效");
        }

        // 2. 查询记录
        StudyRoom studyRoom = studyRoomMapper.selectById(request.getId());
        if (studyRoom == null) {
            throw new RuntimeException("自习记录不存在");
        }

        // 3. 校验归属
        if (!studyRoom.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该自习记录");
        }

        // 4. 校验是否已结束
        if (studyRoom.getEndTime() != null) {
            throw new RuntimeException("该自习已结束");
        }

        // 5. 计算时长
        LocalDateTime now = LocalDateTime.now();
        long totalTime = ChronoUnit.SECONDS.between(studyRoom.getStartTime(), now);
        if (totalTime < 0) {
            totalTime = 0;
        }

        // 6. 判断有效性：切屏超过3次无效
        int isValid = request.getScreenSwitchCount() > 3 ? 0 : 1;

        // 7. 更新记录
        studyRoom.setEndTime(now);
        studyRoom.setTotalTime((int) totalTime);
        studyRoom.setScreenSwitchCount(request.getScreenSwitchCount());
        studyRoom.setIsValid(isValid);
        studyRoomMapper.updateById(studyRoom);

        // 8. 返回 VO
        StudyRoomEndVO vo = new StudyRoomEndVO();
        vo.setId(studyRoom.getId());
        vo.setEndTime(now);
        vo.setTotalTime((int) totalTime);
        vo.setIsValid(isValid);
        return vo;
    }

    @Override
    public StudyRoomRecordListVO getRecords(Long userId, int page, int size) {
        // 1. 分页查询，按开始时间倒序
        Page<StudyRoom> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<StudyRoom> wrapper = new LambdaQueryWrapper<StudyRoom>()
                .eq(StudyRoom::getUserId, userId)
                .orderByDesc(StudyRoom::getStartTime);

        Page<StudyRoom> result = studyRoomMapper.selectPage(pageObj, wrapper);

        // 2. 转换 VO
        List<StudyRoomRecordVO> voList = result.getRecords().stream()
                .map(this::toRecordVO)
                .collect(Collectors.toList());

        // 3. 组装
        StudyRoomRecordListVO listVO = new StudyRoomRecordListVO();
        listVO.setLocation(voList);
        listVO.setTotal(result.getTotal());
        listVO.setPageNum((int) result.getCurrent());
        listVO.setPageSize((int) result.getSize());
        listVO.setPages((int) result.getPages());
        return listVO;
    }

    private StudyRoomRecordVO toRecordVO(StudyRoom studyRoom) {
        StudyRoomRecordVO vo = new StudyRoomRecordVO();
        vo.setId(studyRoom.getId());
        vo.setMode(studyRoom.getMode());
        vo.setStartTime(studyRoom.getStartTime());
        vo.setEndTime(studyRoom.getEndTime());
        vo.setTotalTime(studyRoom.getTotalTime());
        vo.setScreenSwitchCount(studyRoom.getScreenSwitchCount());
        vo.setPlanTime(studyRoom.getPlanTime());
        vo.setIsValid(studyRoom.getIsValid());
        return vo;
    }
}
