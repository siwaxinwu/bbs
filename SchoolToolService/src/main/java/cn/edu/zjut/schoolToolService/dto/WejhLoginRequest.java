package cn.edu.zjut.schoolToolService.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author bert
 * @date 2023/3/1 21:14
 */
@ApiModel("微精弘登录参数")
@Data
public class WejhLoginRequest {
    private String username;
    private String password;
}
