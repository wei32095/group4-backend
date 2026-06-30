package com.jycz.qingyun.service.serviceImpl;

import com.jycz.qingyun.service.VerifyCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class VerifyCodeServiceImpl implements VerifyCodeService {

    private static final String CODE_PREFIX = "verify:code:";
    private static final long CODE_TTL_SECONDS = 300; // 5分钟
    private static final long SEND_INTERVAL_SECONDS = 60; // 60秒内不能重复发送
    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String sendCode(String phone) {
        String codeKey = CODE_PREFIX + phone;
        String intervalKey = CODE_PREFIX + phone + ":interval";

        // 1. 检查发送间隔
        String intervalFlag = stringRedisTemplate.opsForValue().get(intervalKey);
        if (intervalFlag != null) {
            long remaining = stringRedisTemplate.getExpire(intervalKey, TimeUnit.SECONDS);
            throw new RuntimeException("操作太频繁，请" + remaining + "秒后再试");
        }

        // 2. 生成6位随机验证码
        String code = String.format("%06d", RANDOM.nextInt(1_000_000));

        // 3. 存入 Redis（覆盖旧的）
        stringRedisTemplate.opsForValue().set(codeKey, code, CODE_TTL_SECONDS, TimeUnit.SECONDS);
        // 4. 发送间隔标记
        stringRedisTemplate.opsForValue().set(intervalKey, "1", SEND_INTERVAL_SECONDS, TimeUnit.SECONDS);

        // 5. 模拟发送（TODO: 对接真实短信通道后去掉返回验证码）
        log.info("===== 验证码 [{}] 发送到手机 {}", code, phone);
        return code;
    }

    @Override
    public boolean verifyCode(String phone, String code) {
        String codeKey = CODE_PREFIX + phone;
        String storedCode = stringRedisTemplate.opsForValue().get(codeKey);

        if (storedCode == null) {
            return false; // 验证码不存在或已过期
        }

        // 校验通过后立即删除（一次性验证码）
        if (storedCode.equals(code)) {
            stringRedisTemplate.delete(codeKey);
            return true;
        }

        return false;
    }
}
