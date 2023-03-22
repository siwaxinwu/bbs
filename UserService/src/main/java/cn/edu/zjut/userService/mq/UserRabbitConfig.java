package cn.edu.zjut.userService.mq;

import cn.edu.zjut.common.constants.MqConstants;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bert
 */
//@Configuration
public class UserRabbitConfig {

//    @Bean
//    public Queue cancelFollowUser() {
//        // 取消关注后 删去用户接收箱中的内容
//        return new Queue(MqConstants.CANCEL_FOLLOW_USER);
//    }
}

