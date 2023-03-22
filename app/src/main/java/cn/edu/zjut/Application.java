package cn.edu.zjut;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author lcg
 */
//@EnableSwagger2
@SpringBootApplication
@EnableScheduling
public class Application {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(Application.class, args);
        System.out.println("项目已启动：http://localhost:8080/");
        System.out.println("api文档地址：http://localhost:8080/doc.html");
    }
}
