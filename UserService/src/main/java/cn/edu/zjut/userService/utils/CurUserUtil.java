package cn.edu.zjut.userService.utils;

import cn.edu.zjut.common.constants.CommonConstants;
import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.redis.RedisConstants;
import cn.edu.zjut.common.redis.RedisManager;
import cn.edu.zjut.userService.model.entity.User;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 请求用户信息工具类
 * @author bert
 */
public class CurUserUtil {

    private CurUserUtil() {
    }

    public static User getCurUser() {
        User curUser = null;
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null != requestAttributes) {
            HttpServletRequest request = requestAttributes.getRequest();
            String header = request.getHeader(CommonConstants.TOKEN);
            if (StrUtil.isNotEmpty(header)) {
                StringRedisTemplate stringRedisTemplate = RedisManager.getStringRedisTemplate();
                String key = String.format("%s:%s", RedisConstants.CACHE_USER_KEY, header);
                String userJson = stringRedisTemplate.opsForValue().get(key);
                curUser = JSON.parseObject(userJson, User.class);
            }
        }
        return curUser;
    }

    public static User getCurUserThrow() {
        User curUser = getCurUser();
        if (curUser == null) {
            throw new BusinessException(CodeEnum.NOT_LOGIN);
        }
        return curUser;
    }


    /**
     * 获取当前请求中的token
     * 如果带上指定标识，则会判断token是否包含指定标识
     *
     * @return java.lang.String
     */
    private static String getCurrentRequestToken() {
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == requestAttributes) {
            throw new BusinessException(CodeEnum.FAIL,"当前无法获取到请求");
        }
        String token = requestAttributes.getRequest().getHeader(CommonConstants.TOKEN);
        if (StrUtil.isBlank(token)) {
            throw new BusinessException(CodeEnum.FAIL,"非有效请求");
        }
        return token;
    }
}
