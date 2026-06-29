package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jycz.qingyun.mapper.CourseMapper;
import com.jycz.qingyun.mapper.CourseReviewMapper;
import com.jycz.qingyun.mapper.CourseStudentMapper;
import com.jycz.qingyun.mapper.UserMapper;
import com.jycz.qingyun.model.dto.CourseReviewSubmitRequest;
import com.jycz.qingyun.model.entity.Course;
import com.jycz.qingyun.model.entity.CourseReview;
import com.jycz.qingyun.model.entity.User;
import com.jycz.qingyun.model.vo.CourseReviewSummaryVO;
import com.jycz.qingyun.model.vo.CourseReviewVO;
import com.jycz.qingyun.service.CourseReviewService;
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
@RequiredArgsConstructor//
public class CourseReviewServiceImpl implements CourseReviewService {

    private final CourseReviewMapper courseReviewMapper;
    private final CourseMapper courseMapper;
    private final CourseStudentMapper courseStudentMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public CourseReviewVO submitReview(CourseReviewSubmitRequest request, Long userId) {
        // 1. 校验课程是否存在
        Course course = courseMapper.selectById(request.getCourseId());
        if (course == null) {
            throw new BusinessException(404, "课程不存在");
        }

        // 2. 校验学生是否已加入该课程
        LambdaQueryWrapper<com.jycz.qingyun.model.entity.CourseStudent> csWrapper = new LambdaQueryWrapper<>();
        csWrapper.eq(com.jycz.qingyun.model.entity.CourseStudent::getCourseId, request.getCourseId())
                .eq(com.jycz.qingyun.model.entity.CourseStudent::getUserId, userId);
        if (courseStudentMapper.selectCount(csWrapper) == 0) {
            throw new BusinessException(403, "您未加入该课程，无权评价");
        }

        // 3. 检查是否已评价
        CourseReview existingReview = courseReviewMapper.selectOne(
                new LambdaQueryWrapper<CourseReview>()
                        .eq(CourseReview::getCourseId, request.getCourseId())
                        .eq(CourseReview::getUserId, userId)
        );

        CourseReview review;
        boolean isUpdate = false;

        if (existingReview != null) {
            // 更新已有评价
            existingReview.setStar(request.getStar());
            existingReview.setReviewContent(request.getReviewContent());
            courseReviewMapper.updateById(existingReview);
            review = existingReview;
            isUpdate = true;
            log.info("更新课程评价: reviewId={}, userId={}", review.getId(), userId);
        } else {
            // 新建评价
            review = new CourseReview();
            review.setCourseId(request.getCourseId());
            review.setUserId(userId);
            review.setStar(request.getStar());
            review.setReviewContent(request.getReviewContent());
            review.setLikecount(0);
            review.setReviewCreateTime(LocalDateTime.now());
            courseReviewMapper.insert(review);
            log.info("创建课程评价: reviewId={}, userId={}", review.getId(), userId);
        }

        // 4. 获取用户信息
        User user = userMapper.selectById(userId);

        return CourseReviewVO.builder()
                .id(review.getId())
                .userId(review.getUserId())
                .userName(user != null ? user.getName() : "未知用户")
                .userAvatar(user != null ? user.getAvatar() : null)
                .star(review.getStar())
                .reviewContent(review.getReviewContent())
                .likecount(review.getLikecount())
                .reviewCreateTime(review.getReviewCreateTime())
                .build();
    }

    @Override
    public List<CourseReviewVO> getReviewList(Long courseId, Integer star, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;

        LambdaQueryWrapper<CourseReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseReview::getCourseId, courseId);
        if (star != null) {
            wrapper.eq(CourseReview::getStar, star);
        }
        wrapper.orderByDesc(CourseReview::getReviewCreateTime);
        wrapper.last("LIMIT " + offset + "," + pageSize);

        List<CourseReview> reviews = courseReviewMapper.selectList(wrapper);

        if (reviews.isEmpty()) {
            return new ArrayList<>();
        }

        // 批量查询用户信息
        List<Long> userIds = reviews.stream()
                .map(CourseReview::getUserId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, User> userMap;
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));
        } else {
            userMap = new HashMap<>();
        }

        // 组装 VO
        return reviews.stream().map(review -> {
            User user = userMap.get(review.getUserId());
            return CourseReviewVO.builder()
                    .id(review.getId())
                    .userId(review.getUserId())
                    .userName(user != null ? user.getName() : "未知用户")
                    .userAvatar(user != null ? user.getAvatar() : null)
                    .star(review.getStar())
                    .reviewContent(review.getReviewContent())
                    .likecount(review.getLikecount())
                    .reviewCreateTime(review.getReviewCreateTime())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public Long countReviews(Long courseId, Integer star) {
        LambdaQueryWrapper<CourseReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseReview::getCourseId, courseId);
        if (star != null) {
            wrapper.eq(CourseReview::getStar, star);
        }
        return courseReviewMapper.selectCount(wrapper);
    }

    @Override
    public CourseReviewSummaryVO getReviewSummary(Long courseId) {
        // 1. 总评价数
        Long total = countReviews(courseId, null);

        // 2. 平均分
        Double avgStar = courseReviewMapper.selectAvgStar(courseId);

        // 3. 星级统计
        List<Map<String, Object>> stats = courseReviewMapper.selectStarStats(courseId);
        Map<Integer, Integer> starStats = new HashMap<>();
        for (Map<String, Object> stat : stats) {
            Integer star = ((Number) stat.get("star")).intValue();
            Integer count = ((Number) stat.get("count")).intValue();
            starStats.put(star, count);
        }

        return CourseReviewSummaryVO.builder()
                .courseId(courseId)
                .totalReviews(total)
                .avgStar(avgStar != null ? avgStar : 0.0)
                .starStats(starStats)
                .build();
    }
}