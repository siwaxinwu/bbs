package cn.edu.zjut.userService.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author bert
 */
@Data
public class LoginDto {

    @ApiModelProperty(value = "登录类型 1-密码登录  2-验证码登录 3-扫码登录 ")
    @NotNull
    private Integer loginType;
    @ApiModelProperty(value = "用户名")
    private String userName;
    @ApiModelProperty(value = "手机号")
    private String phone;
    @ApiModelProperty(value = "密码")
    private String password;
    @ApiModelProperty(value = "校验码")
    private String captcha;
    @ApiModelProperty(value = "验证码id")
    private String captchaId;
    @ApiModelProperty(value = "预扫码回调之后返回的code")
    private String code;

}
