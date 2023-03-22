package cn.edu.zjut.common.constants;

/**
 * @author bert
 * @date 2023/2/24 22:12
 */
public class MqConstants {
    /**
     * 新增私信消息/通知队列
     */
    public static final String MESSAGE_ADD_QUEUE = "message.add";
    /**
     * 新增评论队列
     */
    public static final String COMMENT_ADD_QUEUE = "comment.add";
    public static final String POST_ADD_QUEUE = "post.add";

    /**
     * 用户取消关注某个用户
     */
    public static final String CANCEL_FOLLOW_USER = "user.cancelFollow";
}
