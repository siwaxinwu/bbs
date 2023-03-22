package cn.edu.zjut.interceptor;

import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.redis.RedisConstants;
import cn.edu.zjut.common.utils.HttpUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * 全局接口防刷
 */
@Component
public class PreventInterceptor implements HandlerInterceptor {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String method = request.getMethod();
        String requestUrl = request.getRequestURI();
        String ipAddress = HttpUtils.getIpAddress(request);
        String keyPre = "prevent";
        String key = keyPre + RedisConstants.SPLIT + method +  requestUrl + ipAddress;
        String value = stringRedisTemplate.opsForValue().get(key);
        if (value != null) {
            throw new BusinessException(CodeEnum.FAIL,"请求太快了~");
        }
        stringRedisTemplate.opsForValue().set(key, "0", 100, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    }
}
