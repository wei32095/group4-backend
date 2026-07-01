package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jycz.qingyun.mapper.PointsRecordMapper;
import com.jycz.qingyun.mapper.UserMapper;
import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.dto.BindPhoneRequest;
import com.jycz.qingyun.model.dto.InfoUpdateRequest;
import com.jycz.qingyun.model.dto.LoginRequest;
import com.jycz.qingyun.model.dto.MpLoginRequest;
import com.jycz.qingyun.model.dto.PasswordUpdateRequest;
import com.jycz.qingyun.model.dto.RegisterRequest;
import com.jycz.qingyun.model.dto.VerifyCodeLoginRequest;
import com.jycz.qingyun.model.vo.AdminUserListVO;
import com.jycz.qingyun.model.vo.AdminUserVO;
import com.jycz.qingyun.service.VerifyCodeService;
import com.jycz.qingyun.utils.BusinessException;
import com.jycz.qingyun.utils.JwtUtil;
import com.jycz.qingyun.utils.WxUtil;
import com.jycz.qingyun.model.entity.User;
import com.jycz.qingyun.model.vo.StudentInfoVO;
import com.jycz.qingyun.service.UserService;
import com.jycz.qingyun.model.vo.LoginVO;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.jycz.qingyun.model.dto.BanUserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PointsRecordMapper pointsRecordMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private WxUtil wxUtil;

    @Autowired
    private VerifyCodeService verifyCodeService;

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
    public ApiResult<LoginVO> loginByVerifyCode(VerifyCodeLoginRequest request) {
        // 1. 校验验证码
        boolean valid = verifyCodeService.verifyCode(request.getPhone(), request.getCode());
        if (!valid) {
            return ApiResult.error(400, "验证码错误或已过期");
        }

        // 2. 查用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, request.getPhone());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            return ApiResult.error(401, "账号不存在");
        }

        // 3. 校验用户状态
        if (user.getStatus() == 0) {
            return ApiResult.error(403, "账号已被禁用");
        }

        // 4. 生成 JWT
        String token = jwtUtil.generateToken(user.getId(), user.getRole());
        return ApiResult.success(buildLoginVO(token, user));
    }

    @Override
    public ApiResult<LoginVO> register(RegisterRequest request) {
        // 1. 校验验证码
        boolean valid = verifyCodeService.verifyCode(request.getPhone(), request.getCode());
        if (!valid) {
            return ApiResult.error(400, "验证码错误或已过期");
        }

        // 2. 校验手机号是否已被注册
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, request.getPhone());
        if (userMapper.selectOne(wrapper) != null) {
            return ApiResult.error(409, "该手机号已被注册");
        }

        // 3. 创建用户
        User user = new User();
        user.setPhone(request.getPhone());
        user.setPassword(request.getPassword());
        user.setName(request.getName().trim());
        user.setRole(request.getRole());
        user.setStatus(1);
        userMapper.insert(user);

        // 3. 生成 JWT
        String token = jwtUtil.generateToken(user.getId(), user.getRole());

        // 4. 构建 LoginVO
        LoginVO login = new LoginVO();
        login.setToken(token);
        login.setTokenType("Bearer");

        String userType = switch (user.getRole()) {
            case 1 -> "STUDENT";
            case 2 -> "TEACHER";
            default -> "UNKNOWN";
        };

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("name", user.getName());
        userInfo.put("phone", user.getPhone());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("bio", user.getBio());
        userInfo.put("role", user.getRole());
        userInfo.put("status", user.getStatus());
        userInfo.put("points", 0);

        login.setUserType(userType);
        login.setUserInfo(userInfo);

        return ApiResult.success("注册成功", login);
    }

    @Override
    public ApiResult<LoginVO> loginByMpCode(MpLoginRequest request) {
        // 1. code → openid
        String openid = wxUtil.codeToOpenid(request.getCode());

        // 2. 查用户是否存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOpenid, openid);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            // 3. 首次使用，自动注册
            user = new User();
            user.setOpenid(openid);
            user.setName("微信用户");
            user.setPassword("");
            user.setRole(1);
            user.setStatus(1);
            userMapper.insert(user);
        }

        // 4. 校验状态
        if (user.getStatus() == 0) {
            return ApiResult.error(403, "账号已被禁用");
        }

        // 5. 生成 JWT
        String token = jwtUtil.generateToken(user.getId(), user.getRole());

        return ApiResult.success(buildLoginVO(token, user));
    }

    @Override
    public ApiResult<Boolean> bindPhone(Long userId, BindPhoneRequest request) {
        // 1. 校验验证码
        boolean valid = verifyCodeService.verifyCode(request.getPhone(), request.getCode());
        if (!valid) {
            return ApiResult.error(400, "验证码错误或已过期");
        }

        // 2. 查手机号是否已被其他账号绑定
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, request.getPhone());
        User existUser = userMapper.selectOne(wrapper);

        if (existUser != null && !existUser.getId().equals(userId)) {
            // 手机号已被其他账号绑定
            User currentUser = userMapper.selectById(userId);
            if (currentUser == null) {
                return ApiResult.error(401, "用户不存在");
            }

            // 如果已有账号也绑了微信 → 不能合并，报错
            if (existUser.getOpenid() != null) {
                return ApiResult.error(409, "该手机号已被其他账号绑定，且该账号已绑定微信");
            }

            // 合并：把当前账号的 openid 转移到已有账号
            existUser.setOpenid(currentUser.getOpenid());
            userMapper.updateById(existUser);
            userMapper.deleteById(userId);
            return ApiResult.success(true);
        }

        // 3. 直接绑定到当前账号
        User user = userMapper.selectById(userId);
        if (user == null) {
            return ApiResult.error(401, "用户不存在");
        }
        user.setPhone(request.getPhone());
        userMapper.updateById(user);
        return ApiResult.success(true);
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
        if (request.getName() == null
                && request.getAvatar() == null && request.getBio() == null) {
            return ApiResult.error(400, "至少提供一个修改字段");
        }

        if (request.getName() != null) {
            user.setName(request.getName());
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

    @Override
    public ApiResult<Boolean> updatePassword(Long userId, PasswordUpdateRequest request) {
        User user = userMapper.selectById(userId);

        if (user == null) {
            return ApiResult.error(401, "用户不存在");
        }

        if (user.getStatus() == 0) {
            return ApiResult.error(403, "账号已被禁用");
        }

        // 校验旧密码
        if (!request.getOldPassword().equals(user.getPassword())) {
            return ApiResult.error(401, "旧密码错误");
        }

        // 更新密码
        user.setPassword(request.getNewPassword());
        int rows = userMapper.updateById(user);
        return ApiResult.success(rows > 0);
    }

    /**
     * 构建统一的登录响应
     */
    private LoginVO buildLoginVO(String token, User user) {
        LoginVO login = new LoginVO();
        login.setToken(token);
        login.setTokenType("Bearer");

        String userType = switch (user.getRole()) {
            case 1 -> "STUDENT";
            case 2 -> "TEACHER";
            case 3 -> "ADMIN";
            default -> "UNKNOWN";
        };

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("name", user.getName());
        userInfo.put("phone", user.getPhone());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("bio", user.getBio());
        userInfo.put("role", user.getRole());
        userInfo.put("status", user.getStatus());
        userInfo.put("points", 0);

        login.setUserType(userType);
        login.setUserInfo(userInfo);

        return login;
    }

    @Override
    @Transactional
    public void banUser(BanUserRequest request, Long adminId) {
        // 1. 查询目标用户
        User targetUser = userMapper.selectById(request.getUserId());
        if (targetUser == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 2. 不能封禁管理员
        if (targetUser.getRole() == 3) {
            throw new BusinessException(403, "不能封禁管理员账号");
        }

        // 3. 不能封禁自己
        if (targetUser.getId().equals(adminId)) {
            throw new BusinessException(403, "不能封禁自己的账号");
        }

        // 4. 封禁操作
        if (request.getStatus() == 0) {
            // 封禁
            if (request.getBanExpireTime() == null) {
                throw new BusinessException(400, "封禁时请指定封禁到期时间");
            }
            if (request.getBanExpireTime().isBefore(LocalDateTime.now())) {
                throw new BusinessException(400, "封禁到期时间不能早于当前时间");
            }
            targetUser.setStatus(0);
            targetUser.setBanExpireTime(request.getBanExpireTime());
            targetUser.setBanReason(request.getBanReason());
            log.info("用户被封禁: userId={}, 操作人={}", targetUser.getId(), adminId);
        } else if (request.getStatus() == 1) {
            // 解封
            targetUser.setStatus(1);
            targetUser.setBanExpireTime(null);
            targetUser.setBanReason(null);
            log.info("用户被解封: userId={}, 操作人={}", targetUser.getId(), adminId);
        } else {
            throw new BusinessException(400, "状态值错误，0-封禁，1-解封");
        }

        // 5. 更新数据库
        userMapper.updateById(targetUser);
    }

    @Override
    public AdminUserListVO getAdminUserList(Integer pageNum, Integer pageSize) {
        // 1. 分页查询用户
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .orderByDesc(User::getCreatedAt);
        IPage<User> userPage = userMapper.selectPage(page, wrapper);

        // 2. 转换 VO
        List<AdminUserVO> records = new ArrayList<>();
        for (User user : userPage.getRecords()) {
            AdminUserVO vo = new AdminUserVO();
            vo.setId(user.getId());
            vo.setName(user.getName());
            vo.setPhone(user.getPhone());
            vo.setAvatar(user.getAvatar());
            vo.setBio(user.getBio());
            vo.setRole(user.getRole());
            vo.setStatus(user.getStatus());
            vo.setBanExpireTime(user.getBanExpireTime());
            vo.setBanReason(user.getBanReason());
            vo.setCreatedAt(user.getCreatedAt());
            vo.setUpdatedAt(user.getUpdatedAt());

            // 查询积分
            Integer points = pointsRecordMapper.getLatestPoints(user.getId());
            vo.setPoints(points != null ? points : 0);

            records.add(vo);
        }

        // 3. 组装分页结果
        AdminUserListVO result = new AdminUserListVO();
        result.setRecords(records);
        result.setTotal(userPage.getTotal());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setPages((int) userPage.getPages());
        return result;
    }

}
