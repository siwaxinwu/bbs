package cn.edu.zjut.common.aspect;

import cn.edu.zjut.common.annotation.Cache;
import cn.edu.zjut.common.annotation.CacheStrategy;
import cn.edu.zjut.common.annotation.Prevent;
import cn.edu.zjut.common.dto.UserDto;
import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.model.Result;
import cn.edu.zjut.common.redis.RedisConstants;
import cn.edu.zjut.common.utils.CurUserUtil;
import cn.edu.zjut.common.utils.HttpUtils;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;

/**
 * @author bert
 */
@Aspect
@Component
@Slf4j
@Order(12)
public class CacheAspect {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Pointcut("@annotation(cn.edu.zjut.common.annotation.Cache)")
    public void pt(){}

    @Around("pt()")
    public Object joinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String requestStr = "0";
        if (args.length != 0) {
            requestStr = Arrays.toString(args);
        }

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = joinPoint.getTarget().getClass().getMethod(methodSignature.getName(),
                methodSignature.getParameterTypes());
        String className = joinPoint.getTarget().getClass().getSimpleName();

        Cache cache = method.getAnnotation(Cache.class);
        long expire = cache.expire();
        CacheStrategy strategy = cache.strategy();
        String key;
        String suffix = className + RedisConstants.SPLIT + method.getName() + requestStr.hashCode();
        switch (strategy) {
            case DEFAULT:
                key = RedisConstants.CACHE_ANNOTATION + RedisConstants.SPLIT + suffix;
                break;
            case CUR_USER:
                UserDto curUser = CurUserUtil.getCurUserDto();
                if (curUser == null) {
                    key = RedisConstants.CACHE_ANNOTATION + RedisConstants.SPLIT + suffix;
                } else {
                    key = RedisConstants.CACHE_ANNOTATION + RedisConstants.SPLIT + curUser.getUserId() + RedisConstants.SPLIT + suffix;
                }
                break;
            case IP_ADDRESS:
                String ipAddress = StrUtil.replace(HttpUtils.getIpAddress(),0,":",".",false);
                key = RedisConstants.CACHE_ANNOTATION + RedisConstants.SPLIT + ipAddress + RedisConstants.SPLIT + suffix;
                break;
            default:
                throw new RuntimeException("错误的缓存策略");
        }
        String redisValue = stringRedisTemplate.opsForValue().get(key);
        // 走缓存
        if (StrUtil.isNotEmpty(redisValue)){
            Class<?> returnType = method.getReturnType();
            return JSON.parseObject(redisValue, returnType);
        }
        // 存缓存
        Object proceed;
        proceed = joinPoint.proceed();
        stringRedisTemplate.opsForValue().set(key,JSON.toJSONString(proceed), Duration.ofMillis(expire));
        return proceed;
    }


}