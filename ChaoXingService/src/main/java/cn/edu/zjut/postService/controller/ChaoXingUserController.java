package cn.edu.zjut.postService.controller;

import cn.edu.zjut.common.model.Result;
import cn.edu.zjut.postService.dto.LoginRequest;
import cn.edu.zjut.postService.utils.ParseRequestUtils;
import cn.edu.zjut.common.model.ChaoXingUser;
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

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author bert
 * @date 2023/1/9 14:48
 */
@RestController
@RequestMapping("/chaoxing/user")
@Api(tags = "超星用户")
public class ChaoXingUserController {


    @PostMapping("/login")
    @ApiOperation("登录")
    public Result<String> login(@RequestBody LoginRequest loginRequest) {
        String url = String.format("https://passport2-api.chaoxing.com/v11/loginregister" +
                        "?code=%s&cx_xxt_passport=json&uname=%s&loginType=1&roleSelect=true",
                loginRequest.getPassword(), loginRequest.getPhone());
        HttpResponse response = HttpRequest.get(url).execute();
        String responseBody = response.body();
        JSONObject jsonObject = JSON.parseObject(responseBody);
        StringBuilder cookies = new StringBuilder();
        if (jsonObject.getBoolean("status")) {
            List<String> list = response.headerList("Set-Cookie");
            String uid = null;
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    String temp = list.get(i).split(";")[0];
                    if (temp.startsWith("UID")) {
                        uid = temp.substring(4);
                    }
                    if (temp.startsWith("JSESSIONID")) {
                        continue;
                    }
                    cookies.append(temp).append(";");
                }
            } else {
                System.out.println("Cookies获取失败");
            }
        }
        else {
            return Result.fail(jsonObject.getString("mes"));
        }
        return Result.ok(cookies.toString());
    }

    @PostMapping("/info")
    @ApiOperation("获取用户信息")
    public Result<ChaoXingUser> userInfo(HttpServletRequest request) {
        ChaoXingUser user = ParseRequestUtils.getCxUser(request);
        return Result.ok(user);
    }


}
