package cn.edu.zjut.userService.controller.common;

import cn.edu.zjut.common.model.Result;
import cn.edu.zjut.common.redis.RedisConstants;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.util.IdUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 验证码操作处理
 *
 * @author bert
 */
@RestController
@Api(tags = "验证码")
public class CaptchaController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 生成验证码
     */
    @GetMapping("/captchaImage")
    @ApiOperation("生成验证码")
    public Result<Map<String, String>> getCode() {
        //定义图形验证码的长、宽、验证码字符数、干扰元素个数
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(200, 100, 4, 40);
        String code = captcha.getCode();
        String captchaId = IdUtil.fastUUID();
        // 保存验证码信息
        String verifyKey = String.format("%s:%s", RedisConstants.CAPTCHA, captchaId);
        stringRedisTemplate.opsForValue().set(verifyKey, code, RedisConstants.CAPTCHA_EXPIRE_MIN, TimeUnit.MINUTES);
        // 封装返回
        String imageBase64 = captcha.getImageBase64();
        Map<String, String> map = new HashMap<>(2);
        map.put("captchaId", captchaId);
        map.put("img", imageBase64);
        return Result.ok(map);
    }
}


