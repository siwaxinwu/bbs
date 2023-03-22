package cn.edu.zjut.messageService.mq;

import lombok.Data;

import java.io.Serializable;

/**
 * @author bert
 * @date 2023/2/24 22:03
 */
@Data
public class SimpleMessage implements Serializable {

    public enum Type {
        /**
         * 私信消息
         */
        MESSAGE("0"),
        /**
         * 通知消息
         */
        NOTIFICATION("1"),
        ;
        String value;
        Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private String type;
    private Long id;
}
