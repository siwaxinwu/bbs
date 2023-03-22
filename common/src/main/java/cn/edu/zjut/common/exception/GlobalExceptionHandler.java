package cn.edu.zjut.common.exception;

import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * @author bert
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     *
     * @param e 异常
     * @return 异常结果
     */
    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException(BusinessException e) {
        log.info("BusinessException, {}", e.getMessage());
        return new Result<>(e.getCode(), e.getMessage());
    }

    /**
     * 运行时异常
     *
     * @param e 异常
     * @return 异常结果
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException e) {
        log.error("RuntimeException", e);
        return new Result<>(CodeEnum.SYSTEM_ERROR);
    }

    /**
     * 异常
     *
     * @param e 异常
     * @return 异常结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException", e);
        return new Result<>(CodeEnum.PARAMS_ERROR, e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

}