package cn.edu.zjut.common.utils;

import cn.edu.zjut.common.constants.CommonConstants;
import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.dto.UserDto;
import cn.edu.zjut.common.redis.RedisConstants;
import cn.edu.zjut.common.redis.RedisManager;
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

    public static UserDto getCurUserDto() {
        UserDto curUser = null;
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null != requestAttributes) {
            HttpServletRequest request = requestAttributes.getRequest();
            String header = request.getHeader(CommonConstants.TOKEN);
            if (StrUtil.isNotEmpty(header)) {
                StringRedisTemplate stringRedisTemplate = RedisManager.getStringRedisTemplate();
                String key = String.format("%s:%s", RedisConstants.CACHE_USER_KEY, header);
                String userJson = stringRedisTemplate.opsForValue().get(key);
                curUser = JSON.parseObject(userJson, UserDto.class);
            }
        }
        return curUser;
    }

    public static UserDto getCurUserDtoThrow() {
        UserDto curUserDto = getCurUserDto();
        if (curUserDto == null) {
            throw new BusinessException(CodeEnum.NOT_LOGIN);
        }
        return curUserDto;
    }

}
