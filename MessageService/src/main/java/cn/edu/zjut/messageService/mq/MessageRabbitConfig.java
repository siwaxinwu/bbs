package cn.edu.zjut.messageService.mq;

import cn.edu.zjut.common.constants.MqConstants;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bert
 */
@Configuration
public class MessageRabbitConfig {

    @Bean
    public Queue messageAddQueue() {
        return new Queue(MqConstants.MESSAGE_ADD_QUEUE);
    }
}

