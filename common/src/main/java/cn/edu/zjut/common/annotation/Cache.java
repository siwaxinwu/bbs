package cn.edu.zjut.common.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author bert
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cache {
    /**
     * 过期时间，单位为ms
     */
    long expire() default 1000*60;

    CacheStrategy strategy() default CacheStrategy.DEFAULT;
}