package cn.edu.zjut.systemService.controller;

import cn.edu.zjut.common.model.Result;
import cn.edu.zjut.common.redis.RedisConstants;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author bert
 * @date 2023/3/6 15:32
 */
@RestController
@RequestMapping("/system")
@Api(tags = "系统")
public class AppController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/updateApp")
    @ApiOperation("获取最新app")
    public Result<JSONObject> updateApp() {
        String key = RedisConstants.getAppUpdateKey();
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json == null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("version","1.0.0");
            return Result.ok(jsonObject);
        }
        JSONObject jsonObject = JSON.parseObject(json);

        return Result.ok(jsonObject);
    }


}
