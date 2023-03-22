package cn.edu.zjut.schoolToolService.controller;

import cn.edu.zjut.common.annotation.Cache;
import cn.edu.zjut.common.annotation.CacheStrategy;
import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.model.Result;
import cn.edu.zjut.schoolToolService.dto.ZfRequest;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Currency;

/**
 * @author bert
 * @date 2023/3/1 21:14
 */
@RestController
@Api(tags = "微精弘")
@RequestMapping("/wejh")
@Slf4j
public class ClassTableController {

    @PostMapping("/classtable")
    @ApiOperation("课程表")
    @Cache(expire = 1000*60*5,strategy = CacheStrategy.IP_ADDRESS)
    public Result<Object[]> classTable(HttpServletRequest request, @RequestBody ZfRequest zfRequest) {
        String cookie = request.getHeader("wejh-cookie");
        String url = "https://wejh.zjutjh.com/api/func/zf/classtable";
        String body = JSON.toJSONString(zfRequest);
        HttpResponse response = HttpRequest.post(url).body(body).cookie(cookie).execute();
        String responseBody = response.body();
        JSONObject jsonObject = JSON.parseObject(responseBody);
        Integer code = jsonObject.getInteger("code");
        if (code != 1) {
            String errorMsg = jsonObject.getString("msg");
            log.error("微精弘错误: {}", errorMsg);
            throw new BusinessException(CodeEnum.WEJH_EXPIRE);
        }
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("lessonsTable");
        if (jsonArray == null) {
            return Result.ok(Collections.emptyList().toArray());
        }
        Object[] objects = jsonArray.toArray();
        return Result.ok(objects);
    }

}
