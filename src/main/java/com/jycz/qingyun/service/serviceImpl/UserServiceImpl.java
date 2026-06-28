package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jycz.qingyun.mapper.PointsRecordMapper;
import com.jycz.qingyun.mapper.UserMapper;
import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.dto.InfoUpdateRequest;
import com.jycz.qingyun.utils.JwtUtil;
import com.jycz.qingyun.model.dto.LoginRequest;
import com.jycz.qingyun.model.entity.User;
import com.jycz.qingyun.model.vo.StudentInfoVO;
import com.jycz.qingyun.service.UserService;
import com.jycz.qingyun.model.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PointsRecordMapper pointsRecordMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public ApiResult<LoginVO> login(LoginRequest request) {
        // 1. 根据手机号查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, request.getPhone());
        User user = userMapper.selectOne(wrapper);

        // 2. 校验用户是否存在
        if (user == null) {
            return ApiResult.error(401, "账号不存在");
        }

        // 3. 校验密码（当前明文比对，后续改为加密）
        if (!request.getPassword().equals(user.getPassword())) {
            return ApiResult.error(401, "密码错误");
        }

        // 4. 校验用户状态
        if (user.getStatus() == 0) {
            return ApiResult.error(403, "账号已被禁用");
        }

        // 5. 生成 JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getRole());

        // 6. 构建 LoginVO
        LoginVO login = new LoginVO();
        login.setToken(token);
        login.setTokenType("Bearer");

        // 7. 根据角色设置 userType
        String userType = switch (user.getRole()) {
            case 1 -> "STUDENT";
            case 2 -> "TEACHER";
            case 3 -> "ADMIN";
            default -> "UNKNOWN";
        };

        // 8. 查询积分
        Integer points = pointsRecordMapper.getLatestPoints(user.getId());

        // 9. 组装 userInfo（统一返回所有字段）
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("name", user.getName());
        userInfo.put("phone", user.getPhone());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("bio", user.getBio());
        userInfo.put("role", user.getRole());
        userInfo.put("status", user.getStatus());
        userInfo.put("points", points != null ? points : 0);

        login.setUserType(userType);
        login.setUserInfo(userInfo);

        return ApiResult.success(login);
    }

    @Override
    public ApiResult<StudentInfoVO> getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);

        if (user == null) {
            return ApiResult.error(401, "用户不存在");
        }

        if (user.getStatus() == 0) {
            return ApiResult.error(403, "账号已被禁用");
        }

        StudentInfoVO vo = new StudentInfoVO();
        vo.setId(user.getId());
        vo.setName(user.getName());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setBio(user.getBio());
        vo.setRole(user.getRole());
        vo.setStatus(user.getStatus());

        // 查询积分
        Integer points = pointsRecordMapper.getLatestPoints(user.getId());
        vo.setPoints(points != null ? points : 0);

        return ApiResult.success(vo);
    }

    @Override
    public ApiResult<Boolean> updateUserInfo(Long userId, InfoUpdateRequest request) {
        User user = userMapper.selectById(userId);

        if (user == null) {
            return ApiResult.error(401, "用户不存在");
        }

        if (user.getStatus() == 0) {
            return ApiResult.error(403, "账号已被禁用");
        }

        // 至少提供一个修改字段
        if (request.getName() == null && request.getPhone() == null
                && request.getAvatar() == null && request.getBio() == null) {
            return ApiResult.error(400, "至少提供一个修改字段");
        }

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        int rows = userMapper.updateById(user);
        return ApiResult.success(rows > 0);
    }
}
