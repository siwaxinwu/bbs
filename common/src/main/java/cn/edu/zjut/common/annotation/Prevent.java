package cn.edu.zjut.common.annotation;


import java.lang.annotation.*;

/**
 * 接口防刷注解
 * 使用：
 * 在相应需要防刷的方法上加上
 * 该注解，即可
 *
 * @author: zetting
 * @date:2018/12/29
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Prevent {

    /**
     * 限制的时间值（ms）
     */
    long value() default 1000L;

    /**
     * 提示
     */
    String message() default "";

    /**
     * 策略
     */
    PreventStrategy strategy() default PreventStrategy.DEFAULT;
}