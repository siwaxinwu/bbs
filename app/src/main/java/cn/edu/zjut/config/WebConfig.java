package cn.edu.zjut.config;

import cn.edu.zjut.interceptor.PreventInterceptor;
import cn.edu.zjut.interceptor.UserStatusInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;


/**
 * @author bert
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private UserStatusInterceptor userStatusInterceptor;
    @Resource
    private PreventInterceptor preventInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("*")
                .allowedOrigins("*")
            .allowedMethods("PUT", "DELETE","POST","GET","OPTIONS")
            .allowedHeaders("*")
            .maxAge(36000);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userStatusInterceptor)
                .addPathPatterns(
                        "/comment",
                        "/comment/thumb/*",
                        "/post",
                        "/post/thumb/*",
                        "/follow/*",
                        "/message",
                        "/circle/join/*"
                );
        registry.addInterceptor(preventInterceptor)
                .excludePathPatterns(
                        "/file/uploadImg",
                        "/file/img/*"
                );
    }
}