package cn.edu.zjut.common.exception;

import cn.edu.zjut.common.enums.CodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义业务异常类
 *
 * @author liyupi
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class BusinessException extends RuntimeException {

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;


    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(CodeEnum codeEnum) {
        this(codeEnum.getCode(), codeEnum.getMessage());
    }

    public BusinessException(CodeEnum codeEnum, String message) {
        this(codeEnum.getCode(), message);
    }
}