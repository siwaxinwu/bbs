package cn.edu.zjut.common.annotation;

import java.lang.annotation.*;

/**
 * @author bert
 */

@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {

}
