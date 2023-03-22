package cn.edu.zjut.common.aspect;

import cn.edu.zjut.common.utils.HttpUtils;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.StringJoiner;

/**
 * @author bert
 */
@Aspect
@Component
@Order(10)
@Slf4j
public class LogAspect {
    @Pointcut("@annotation(cn.edu.zjut.common.annotation.Log)")
    public void logPointCut() {
    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = point.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        //保存日志
        recordLog(point, result ,time);
        return result;
    }

    private void recordLog(ProceedingJoinPoint joinPoint, Object result, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        log.info("=====================log start================================");
        //请求的类名、方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        log.info("request {}",className + "." + methodName + "()");
        //请求的参数
        Object[] args = joinPoint.getArgs();
        StringJoiner params = new StringJoiner(":");
        Arrays.stream(args).forEach(c->{
            params.add(c.toString());
        });
        log.info("params:{}", params);
        //获取request 设置IP地址
        String ipAddress = HttpUtils.getIpAddress();
        log.info("ip:{}", ipAddress);
        log.info("result:{}", JSON.toJSONString(result));
        log.info("excute time : {} ms",time);
        log.info("=====================log end================================");
        log.info("\n");
    }

}