package cn.edu.zjut.common.enums;

/**
 * 错误码枚举
 *
 * @author liyupi
 */
public enum CodeEnum {

    /**
     * 成功
     */
    SUCCESS(2000, "成功"),

    /**
     * 失败
     */
    NOT_ALLOW(3000, "权限不足"),

    /**
     * 失败
     */
    FAIL(4000, "失败"),

    /**
     * 失败
     */
    NOT_LOGIN(4001, "未登录"),

    /**
     * 用户错误
     */
    PARAMS_ERROR(4010, "参数错误"),

    WEJH_EXPIRE(4100,"请重新登录微精弘账号"),
    CHAO_XING_EXPIRE(4100,"请重新登录超星账号"),

    /**
     * 系统错误
     */
    SYSTEM_ERROR(5000, "系统错误"),

    /**
     * 文件上传错误
     */
    FILE_UPLOAD_ERROR(5010, "文件上传错误"),

    OPERATION_ERROR(6000,"操作错误"),

    /**
     * 未上线
     */
    UN_KNOW_ERROR(99999, "未知异常");

    private final int code;

    private final String message;

    CodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}