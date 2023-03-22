package cn.edu.zjut.common.enums;

/**
 * 错误码枚举
 *
 * @author liyupi
 */
public enum LoginTypeEnum {

    /**
     * 账号密码登录
     */
    PASSWORD(1),
    /**
     * 超星登录
     */
    CHAO_XING(2),
    /**
     * 微精弘登录
     */
    WE_JH(3),
    ;

    private final int code;

    LoginTypeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}