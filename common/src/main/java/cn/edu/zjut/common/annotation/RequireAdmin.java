package cn.edu.zjut.common.annotation;

import java.lang.annotation.*;

/**
 * @author bert
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireAdmin {
}
