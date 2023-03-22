package cn.edu.zjut.circleAndTopicService.mq;

import cn.edu.zjut.common.constants.MqConstants;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bert
 */
@Configuration
public class CommunityRabbitConfig {

    @Bean
    public Queue commentAddQueue() {
        return new Queue(MqConstants.COMMENT_ADD_QUEUE);
    }
    @Bean
    public Queue postAddQueue() {
        return new Queue(MqConstants.POST_ADD_QUEUE);
    }
}

