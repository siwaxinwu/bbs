package cn.edu.zjut.common.aspect;

import cn.edu.zjut.common.annotation.Prevent;
import cn.edu.zjut.common.annotation.PreventStrategy;
import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.redis.RedisConstants;
import cn.edu.zjut.common.utils.HttpUtils;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

/**
 * 防刷切面实现类
 * @author bert
 */
@Aspect
@Component
@Slf4j
@Order(11)
public class PreventAop {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    private final String preKey = "prevent";

    /**
     * 切入点
     */
    @Pointcut("@annotation(cn.edu.zjut.common.annotation.Prevent)")
    public void pointcut() {
    }

    /**
     * 处理前
     */
    @Before("pointcut()")
    public void joinPoint(JoinPoint joinPoint) throws Exception {
        Object[] args = joinPoint.getArgs();
        String requestStr;
        if (args.length == 0) {
            requestStr = "0";
        } else {
            requestStr = JSON.toJSONString(args[0]);
        }

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = joinPoint.getTarget().getClass().getMethod(methodSignature.getName(),
                methodSignature.getParameterTypes());

        Prevent preventAnnotation = method.getAnnotation(Prevent.class);
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodFullName = className + ":" + method.getName();

        entrance(preventAnnotation, requestStr, methodFullName);
    }


    /**
     * 入口
     */
    private void entrance(Prevent prevent, String requestStr, String methodFullName) throws Exception {
        PreventStrategy strategy = prevent.strategy();
        switch (strategy) {
            case DEFAULT:
                defaultHandle(requestStr, prevent,methodFullName);
                break;
            case IPADDR:
                ipAddrHandle(requestStr,prevent, methodFullName);
                break;
            default:
                throw new RuntimeException("无效的策略");
        }
    }


    /**
     * 默认处理方式
     */
    private void defaultHandle(String requestStr, Prevent prevent,String methodFullName) throws Exception {
        String base64Str = toBase64String(requestStr);
        String key = preKey + RedisConstants.SPLIT + methodFullName + base64Str;
        process(key, prevent);
    }

    /**
     * ip限制处理方式
     */
    private void ipAddrHandle(String requestStr, Prevent prevent,String methodFullName) throws Exception {
        String base64Str = toBase64String(requestStr);
        String ipAddress = HttpUtils.getIpAddress();
        String key = preKey + RedisConstants.SPLIT + ipAddress + RedisConstants.SPLIT + methodFullName + base64Str;
        process(key, prevent);
    }

    private void process(String key, Prevent prevent) {
        long expire = prevent.value();
        String resp = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isEmpty(resp)) {
            stringRedisTemplate.opsForValue().set(key, "0");
            stringRedisTemplate.expire(key, Duration.ofMillis(expire));
        } else {
            String message = !StrUtil.isEmpty(prevent.message()) ? prevent.message() : "请求过快";
            throw new BusinessException(CodeEnum.FAIL,message);
        }
    }


    /**
     * 对象转换为base64字符串
     *
     * @param obj 对象值
     * @return base64字符串
     */
    private String toBase64String(String obj) throws Exception {
        if (StrUtil.isEmpty(obj)) {
            return null;
        }
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] bytes = obj.getBytes(StandardCharsets.UTF_8);
        return encoder.encodeToString(bytes);
    }


}