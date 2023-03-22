package cn.edu.zjut.postService.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bert
 * @date 2023/1/20 21:49
 */
@ApiModel("超星登录参数")
@Data
public class LoginRequest {

    @ApiModelProperty("电话号码")
    private String phone;
    @ApiModelProperty("登录密码")
    private String password;
}
