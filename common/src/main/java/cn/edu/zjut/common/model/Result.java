package cn.edu.zjut.common.model;

import cn.edu.zjut.common.enums.CodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * 通用返回类
 * @author lcg
 */
@Data
@AllArgsConstructor
@ApiModel(description = "响应信息主体")
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("消息码")
    private Integer code;

    @ApiModelProperty("消息内容")
    private String msg;

    @ApiModelProperty("数据")
    private T data;



    public Result(Integer code, String msg) {
        this(code,msg,null);
    }

    public Result(CodeEnum codeEnum) {
        this(codeEnum.getCode(), codeEnum.getMessage(),null);
    }

    public Result(CodeEnum codeEnum, String msg) {
        this(codeEnum.getCode(), msg,null);
    }

    public Result(CodeEnum codeEnum, T data) {
        this(codeEnum.getCode(), codeEnum.getMessage(),data);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(CodeEnum.SUCCESS, data);
    }

    public static <T> Result<T> ok() {
        return Result.ok(null);
    }

    public static <T> Result<T> fail(T data) {
        return new Result<>(CodeEnum.FAIL,data);
    }

    public static <T> Result<T> fail() {
        return Result.fail(null);
    }

    public static <T> Result<T> bool(boolean flag) {
        if (flag) {
            return new Result<>(CodeEnum.SUCCESS);
        }
        return new Result<>(CodeEnum.FAIL);
    }
}