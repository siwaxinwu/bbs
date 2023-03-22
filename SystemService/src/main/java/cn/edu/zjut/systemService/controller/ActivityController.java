package cn.edu.zjut.systemService.controller;

import cn.edu.zjut.common.annotation.Cache;
import cn.edu.zjut.common.annotation.CacheStrategy;
import cn.edu.zjut.common.annotation.RequireAdmin;
import cn.edu.zjut.common.model.Result;
import cn.edu.zjut.common.redis.RedisConstants;
import cn.edu.zjut.systemService.model.Activity;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author bert
 * @date 2023/3/5 16:46
 */
@RestController
@RequestMapping("/system/hotActivity")
@Api(tags = "系统")
public class ActivityController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @PutMapping
    @ApiOperation("修改热门活动列表")
    @RequireAdmin
    public Result<Boolean> updateActivity(@RequestBody List<Activity> list) {
        stringRedisTemplate.opsForValue().set(RedisConstants.HOT_ACTIVITY, JSON.toJSONString(list));
        return Result.ok();
    }

    @GetMapping
    @ApiOperation("获取热门活动列表")
    public Result<List<Activity>> getActivityList() {
        String dataJson = stringRedisTemplate.opsForValue().get(RedisConstants.HOT_ACTIVITY);
        if (dataJson == null) {
            return Result.ok(Collections.emptyList());
        }
        List<Activity> list = JSON.parseArray(dataJson, Activity.class);
        return Result.ok(list);
    }
}
