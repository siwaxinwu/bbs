package cn.edu.zjut.schoolToolService.controller;

import cn.edu.zjut.common.annotation.Cache;
import cn.edu.zjut.common.model.Result;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author bert
 * @date 2023/3/1 21:14
 */
@RestController
@Api(tags = "微精弘")
@RequestMapping("/wejh")
public class TermInfoController {

    @PostMapping("/terminfo")
    @ApiOperation("学期信息")
    @Cache(expire = 1000*60*60*24*10L)
    public Result<Object> terminfo() {
        String url = "https://wejh.zjutjh.com/api/info";
        HttpResponse response = HttpRequest.post(url).execute();
        String responseBody = response.body();
        JSONObject jsonObject = JSON.parseObject(responseBody);
        JSONObject data = jsonObject.getJSONObject("data");
        return Result.ok(data);
    }

}
