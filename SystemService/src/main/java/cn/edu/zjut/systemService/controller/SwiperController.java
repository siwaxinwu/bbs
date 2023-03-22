package cn.edu.zjut.systemService.controller;

import cn.edu.zjut.common.annotation.RequireAdmin;
import cn.edu.zjut.common.model.Result;
import cn.edu.zjut.common.redis.RedisConstants;
import cn.edu.zjut.systemService.model.Swiper;
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
 * @date 2023/3/5 16:47
 */
@RestController
@RequestMapping("/system/swiper")
@Api(tags = "系统")
public class SwiperController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @PutMapping
    @ApiOperation("修改轮播图列表")
    @RequireAdmin
    public Result<Boolean> updateSwiper(@RequestBody List<Swiper> swiperList) {
        stringRedisTemplate.opsForValue().set(RedisConstants.SWIPER, JSON.toJSONString(swiperList));
        return Result.ok();
    }

    @GetMapping
    @ApiOperation("获取轮播图列表")
    public Result<List<Swiper>> getSwiperList() {
        String dataJson = stringRedisTemplate.opsForValue().get(RedisConstants.SWIPER);
        if (dataJson == null) {
            return Result.ok(Collections.emptyList());
        }
        List<Swiper> swiperList = JSON.parseArray(dataJson, Swiper.class);
        return Result.ok(swiperList);
    }

}
