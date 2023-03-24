package cn.edu.zjut.messageService.mq;

import cn.edu.zjut.circleAndTopicService.enums.CommentTypeEnum;
import cn.edu.zjut.circleAndTopicService.model.entity.Comment;
import cn.edu.zjut.circleAndTopicService.model.entity.Post;
import cn.edu.zjut.circleAndTopicService.service.CommentService;
import cn.edu.zjut.circleAndTopicService.service.PostService;
import cn.edu.zjut.common.constants.MqConstants;
import cn.edu.zjut.common.constants.PatternConstants;
import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.redis.RedisConstants;
import cn.edu.zjut.messageService.enums.NotificationTypeEnum;
import cn.edu.zjut.messageService.model.entity.Message;
import cn.edu.zjut.messageService.model.entity.Notification;
import cn.edu.zjut.messageService.service.MessageService;
import cn.edu.zjut.messageService.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author bert
 * @date 2023/2/24 21:58
 */
@Component
public class MessageListener {

    @Resource
    private MessageService messageService;
    @Resource
    private PostService postService;
    @Resource
    private CommentService commentService;
    @Resource
    private NotificationService notificationService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = MqConstants.MESSAGE_ADD_QUEUE)
    public void listenSimpleQueueMessage(SimpleMessage message) {
        String type = message.getType();
        Long id = message.getId();
        // 增加一条私信
        if (SimpleMessage.Type.MESSAGE.value.equals(type)) {
            Message msg = messageService.getById(id);
            // 发送方未读消息数设为0 （相当于添加一个聊天列）
            String sendUserKey = RedisConstants.getPersonMessageKey(msg.getSendUserId(),msg.getReceiveUserId());
            stringRedisTemplate.opsForHash().put(sendUserKey,"unReadCount",String.valueOf(0));
            stringRedisTemplate.opsForHash().put(sendUserKey,"lastMessageId", String.valueOf(id));
            // 对方未读消息数+1
            String receiveUserKey = RedisConstants.getPersonMessageKey(msg.getReceiveUserId(),msg.getSendUserId());
            stringRedisTemplate.opsForHash().increment(receiveUserKey,"unReadCount",1L);
            stringRedisTemplate.opsForHash().put(receiveUserKey,"lastMessageId", String.valueOf(id));
        }
        // 增加一条通知
        else if (SimpleMessage.Type.NOTIFICATION.value.equals(type)) {
            Notification notification = notificationService.getById(id);
            String notificationType = notification.getType();
            String key;
            if (NotificationTypeEnum.SYSTEM.getValue().equals(notificationType)) {
                key = RedisConstants.getSystemNotificationKey(notification.getReceiveUserId());
            } else {
                key = RedisConstants.getInteractNotificationKey(notification.getReceiveUserId());
            }
            stringRedisTemplate.opsForHash().increment(key,"unReadCount",1L);
            stringRedisTemplate.opsForHash().put(key,"lastNotificationId",String.valueOf(id));
        }
        else {
            throw new BusinessException(CodeEnum.SYSTEM_ERROR);
        }
    }

    @RabbitListener(queues = MqConstants.COMMENT_ADD_QUEUE)
    public void listenSimpleQueueMessage(String commentId) {
        // 获取评论具体信息
        Comment comment = commentService.getById(commentId);

        // 动态的评论数+1
        postService.lambdaUpdate().setSql("comment_count=comment_count+1").eq(Post::getId,comment.getPostId()).update();

        // 数据库中添加一条通知记录
        CommentTypeEnum commentTypeEnum = CommentTypeEnum.transform(comment.getType());
        String type;
        // 为评论动态

        if (CommentTypeEnum.COMMENT.equals(commentTypeEnum)) {
            type = NotificationTypeEnum.COMMENT.getValue();
        }
        // 为回复评论
        else if (CommentTypeEnum.REPLY.equals(commentTypeEnum)) {
            type = NotificationTypeEnum.REPLY.getValue();
            // 被评论的回复数+1
            commentService.lambdaUpdate().setSql("reply_count=reply_count+1").eq(Comment::getId,comment.getReplyCommentId()).update();
            // 父级评论回复数+1 （如果被回复评论和父级评论不是同一条的话）
            if (comment.getCommentId() != null && !comment.getCommentId().equals(comment.getReplyCommentId())) {
                commentService.lambdaUpdate().setSql("reply_count=reply_count+1").eq(Comment::getId, comment.getCommentId()).update();
            }
        }
        else {
            throw new BusinessException(CodeEnum.UN_KNOW_ERROR);
        }
        // 过滤自己
        if (comment.getUserId().equals(comment.getReplyUserId())) {
            return;
        }
        Notification notification = new Notification();
        notification.setSendUserId(comment.getUserId());
        notification.setReceiveUserId(comment.getReplyUserId());
        notification.setContent(comment.getContent());
        notification.setResourceId(comment.getId());
        notification.setType(type);
        // 设置标题和封面 （标题就是要评论的内容）
        if (comment.getCommentId()!=null) {
            Comment replyComment = commentService.getById(comment.getReplyCommentId());
            notification.setTitle(replyComment.getContent());
        } else {
            Post replyPost = postService.getById(comment.getPostId());
            notification.setTitle(replyPost.getContent());
            // 解析封面
            String pattern = PatternConstants.PARSE_POST_IMAGE;
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(replyPost.getContent());
            if (m.find()) {
                String[] split = m.group(2).split(",");
                notification.setCoverResourceId(split[0]);
                String content = m.replaceAll(matchResult -> matchResult.group(1));
                notification.setTitle(content);
            }
        }
        notificationService.save(notification);

        // 发送互动通知消息
        SimpleMessage simpleMessage = new SimpleMessage();
        simpleMessage.setType(SimpleMessage.Type.NOTIFICATION.getValue());
        simpleMessage.setId(notification.getId());
        rabbitTemplate.convertAndSend(MqConstants.MESSAGE_ADD_QUEUE,simpleMessage);
    }

}
