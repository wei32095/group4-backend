package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jycz.qingyun.mapper.UserMapper;
import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.utils.JwtUtil;
import com.jycz.qingyun.model.dto.LoginRequest;
import com.jycz.qingyun.model.entity.User;
import com.jycz.qingyun.service.UserService;
import com.jycz.qingyun.model.vo.Login;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public ApiResult<Login> login(LoginRequest request) {
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
        if (user.getStatus() == 2) {
            return ApiResult.error(403, "账号已被禁用");
        }

        // 5. 生成 JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getRole());

        // 6. 构建 LoginVO
        Login login = new Login();
        login.setToken(token);
        login.setTokenType("Bearer");

        // 7. 根据角色设置 userType 和 userInfo
        String userType;
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("name", user.getName());
        userInfo.put("phone", user.getPhone());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("role", user.getRole());
        userInfo.put("status", user.getStatus());

        switch (user.getRole()) {
            case 1:
                userType = "STUDENT";
                break;
            case 2:
                userType = "TEACHER";
                break;
            case 3:
                userType = "ADMIN";
                break;
            default:
                userType = "UNKNOWN";
        }

        login.setUserType(userType);
        login.setUserInfo(userInfo);

        return ApiResult.success(login);
    }
}
