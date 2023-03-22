package cn.edu.zjut.schoolToolService.controller;

import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.model.Result;
import cn.edu.zjut.schoolToolService.dto.WejhLoginRequest;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author bert
 * @date 2023/3/1 21:14
 */
@RestController
@Api(tags = "微精弘")
@RequestMapping("/wejh")
public class WejhUserController {

    @PostMapping("/login")
    @ApiOperation("登录")
    public Result<String> wejhLogin(@RequestBody WejhLoginRequest request) {
        String url = "https://wejh.zjutjh.com/api/user/login";
        HttpResponse response = HttpRequest.post(url).body(JSON.toJSONString(request)).execute();
        String responseBody = response.body();
        JSONObject jsonObject = JSON.parseObject(responseBody);
        if (jsonObject.getInteger("code") != 1) {
            throw new BusinessException(CodeEnum.FAIL,jsonObject.getString("msg"));
        }
        String cookie = response.header("set-cookie");
        return Result.ok(cookie);
    }

}
