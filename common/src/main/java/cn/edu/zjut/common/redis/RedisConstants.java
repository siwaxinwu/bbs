package cn.edu.zjut.common.redis;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author bert
 * @description redis常量
 * @date 2023/1/11 11:49
 */
public class RedisConstants {
    public static final String CAPTCHA = "captcha";
    public static final int CAPTCHA_EXPIRE_MIN = 5;
    public static final String ACTIVATED_OTHER = "status:activated_other";
    public static Duration cacheUserDuration = Duration.ofDays(30);
    public static String CACHE_USER_KEY = "cache:user";
    public static String CACHE_TOKEN_KEY = "cache:token";
    public static String TOP10 = "recommend:TOP10";
    public static String HOT_ACTIVITY = "recommend:hot-activity";
    public static String TOKEN = "token";
    public static String SWIPER = "recommend:swiper";
    public static String CACHE_ANNOTATION = "cache:annotation";
    public static final String SPLIT = ":";

    /**
     * 获取用户圈子收件箱hey
     */
    public static String getUserCircleReceiveKey(Long uid) {
        return String.format("user:%s:receive:circle", uid);
    }

    /**
     * 获取用户关注收件箱key
     */
    public static String getUserFollowReceiveKey(Long uid) {
        return String.format("user:%s:receive:followUser", uid);
    }

    public static String getCircleFansKey(Long circleId) {
        return String.format("circle:%s:fans", circleId);
    }

    public static String getFollowCircleKey(long uid) {
        return String.format("user:%s:follow:circle", uid);
    }

    public static String getFollowUserKey(long uid) {
        return String.format("user:%s:follow:user", uid);
    }

    public static String getFanKey(Long uid) {
        return String.format("user:%s:follow:fan", uid);
    }

    public static String getThumbPostKey(Long userid) {
        return String.format("user:%s:thumb:post", userid);
    }

    public static String getThumbCommentKey(Long userid) {
        return String.format("user:%s:thumb:comment", userid);
    }

    public static String getPersonMessageKey(Long userid,Long sendUserId) {
        return String.format("user:%s:unReadMessage:%s", userid,sendUserId);
    }

    public static String getSystemNotificationKey(Long userId) {
        return String.format("user:%s:unReadNotification:system", userId);
    }
    public static String getInteractNotificationKey(Long userId) {
        return String.format("user:%s:unReadNotification:interact", userId);
    }
    public static String getUnReadCircleCountKey(Long userId,Long circleId) {
        return String.format("user:%s:unReadPostCount:circle:%s", userId,circleId);
    }
    public static String getUnReadFollowPersonCountKey(Long userId) {
        return String.format("user:%s:unReadPostCount:followPerson", userId);
    }

    public static String getSignKey(Long userId,LocalDateTime time) {
        String keySuffix = time.format(DateTimeFormatter.ofPattern("yyyyMM"));
        return String.format("user:%s:sign:"+keySuffix, userId);
    }

    public static String getAppUpdateKey() {
        return "system:appUpdate";
    }
}
