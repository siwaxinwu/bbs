package cn.edu.zjut.common.annotation;

/**
 * 防刷策略枚举
 */
public enum CacheStrategy {
    /**
     * 默认,所有人的缓存
     */
    DEFAULT,
    /**
     * 针对当前用户的(每个用户有自己的缓存)
     */
    CUR_USER,
    /**
     * ip地址
     */
    IP_ADDRESS,
}