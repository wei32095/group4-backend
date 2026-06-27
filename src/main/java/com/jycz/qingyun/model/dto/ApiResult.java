package com.jycz.qingyun.model.dto;

import lombok.Data;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * 统一 API 响应结果封装
 * @param <T> 业务数据类型
 */
@Data
public class ApiResult<T> {

    /**
     * 业务状态码（200 表示成功）
     */
    private int code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 业务数据（泛型）
     */
    private T data;

    /**
     * 响应时间戳（ISO 8601 格式）
     */
    private String timestamp;

    // ========== 私有构造方法 ==========
    private ApiResult() {}

    private ApiResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now());
    }

    // ========== 成功响应（200） ==========
    public static <T> ApiResult<T> success() {
        return new ApiResult<>(200, "操作成功", null);
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(200, "操作成功", data);
    }

    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>(200, message, data);
    }

    // ========== 失败响应（自定义状态码） ==========
    public static <T> ApiResult<T> error(int code, String message) {
        return new ApiResult<>(code, message, null);
    }

    public static <T> ApiResult<T> error(String message) {
        return new ApiResult<>(400, message, null);
    }

    public static <T> ApiResult<T> error(ErrorCode errorCode) {
        return new ApiResult<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    // ========== 内部错误码枚举 ==========
    public enum ErrorCode {
        SUCCESS(200, "操作成功"),
        BAD_REQUEST(400, "请求参数错误"),
        UNAUTHORIZED(401, "未登录或登录已过期"),
        FORBIDDEN(403, "权限不足"),
        NOT_FOUND(404, "资源不存在"),
        CONFLICT(409, "数据冲突"),
        INTERNAL_ERROR(500, "服务器内部错误");

        private final int code;
        private final String message;

        ErrorCode(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() { return code; }
        public String getMessage() { return message; }
    }

    // ========== 链式设置（可选） ==========
    // 如果需要链式调用，可以手动添加 setter 返回 this（但 Lombok @Data 生成的 setter 返回 void）
    // 此处为了简洁，不提供链式写法，保持标准 JavaBean 风格
}