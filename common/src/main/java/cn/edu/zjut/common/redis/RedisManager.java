package cn.edu.zjut.common.redis;

import cn.edu.zjut.common.utils.SpringContextHolder;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * redis manager
 *
 * @author : wangpeng
 * @version : 1.0
 * @since : 16/6/23 下午12:00
 */

public class RedisManager{
    public static StringRedisTemplate getStringRedisTemplate() {
        return SpringContextHolder.getBean(StringRedisTemplate.class);
    }
}
