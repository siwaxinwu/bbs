package cn.edu.zjut.common.annotation;

/**
 * 防刷策略枚举
 */
public enum PreventStrategy {
    /**
     * 默认,所有人访问都受到限制
     */
    DEFAULT,
    /**
     * 针对每个ip地址做出限制
     */
    IPADDR,
}