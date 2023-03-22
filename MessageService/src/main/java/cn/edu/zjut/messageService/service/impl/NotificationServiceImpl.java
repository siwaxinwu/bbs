package cn.edu.zjut.messageService.service.impl;

import cn.edu.zjut.circleAndTopicService.model.entity.Comment;
import cn.edu.zjut.circleAndTopicService.model.entity.Post;
import cn.edu.zjut.circleAndTopicService.service.CommentService;
import cn.edu.zjut.circleAndTopicService.service.PostService;
import cn.edu.zjut.common.dto.UserDto;
import cn.edu.zjut.common.redis.RedisConstants;
import cn.edu.zjut.common.utils.CurUserUtil;
import cn.edu.zjut.common.utils.PageUtils;
import cn.edu.zjut.fileService.service.ResourceService;
import cn.edu.zjut.messageService.enums.NotificationTypeEnum;
import cn.edu.zjut.messageService.model.dto.notification.NotificationAddRequest;
import cn.edu.zjut.messageService.model.vo.NotificationItemVo;
import cn.edu.zjut.messageService.model.vo.NotificationVo;
import cn.edu.zjut.common.constants.MqConstants;
import cn.edu.zjut.messageService.model.vo.ResourceVo;
import cn.edu.zjut.messageService.mq.SimpleMessage;
import cn.edu.zjut.userService.model.vo.UserSimpleVo;
import cn.edu.zjut.userService.service.UserService;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.zjut.messageService.model.entity.Notification;
import cn.edu.zjut.messageService.service.NotificationService;
import cn.edu.zjut.messageService.mapper.NotificationMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author bert
* @description 针对表【notification(通知表)】的数据库操作Service实现
* @createDate 2023-02-24 09:18:14
*/
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification>
    implements NotificationService{

    @Resource
    private UserService userService;
    @Resource
    private ResourceService resourceService;
    @Resource
    private PostService postService;
    @Resource
    private CommentService commentService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Page<NotificationVo> voPage(int pageNum, int pageSize, Wrapper<Notification> wrapper) {
        Page<Notification> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        List<NotificationVo> collect = page.getRecords()
                .stream().map(notification -> {
                    NotificationTypeEnum typeEnum = NotificationTypeEnum.transform(notification.getType());
                    // 系统通知，不用设置发送者信息
                    if (NotificationTypeEnum.SYSTEM.equals(typeEnum)) {
                        return this.toVo(notification,false);
                    }
                    NotificationVo notificationVo = this.toVo(notification);
                    Long commentId = notification.getResourceId();
                    Comment comment = commentService.getById(commentId);
                    Long postId = comment.getPostId();
                    Post post = postService.getById(postId);
                    Long postUserId = post.getUserId();
                    ResourceVo resourceVo = new ResourceVo();
                    resourceVo.setPostId(postId);
                    resourceVo.setCommentId(commentId);
                    resourceVo.setUserId(comment.getUserId());
                    resourceVo.setPostUserId(postUserId);
                    if (NotificationTypeEnum.COMMENT.equals(typeEnum)) {
                        resourceVo.setParentCommentId(commentId);
                        notificationVo.setResource(resourceVo);
                        return notificationVo;
                    }
                    // 若该评论是回复，还需要设置父级评论的id
                    if (NotificationTypeEnum.REPLY.equals(typeEnum)) {
                        Long parentCommentId = comment.getCommentId();
                        resourceVo.setParentCommentId(parentCommentId);
                        notificationVo.setResource(resourceVo);
                        return notificationVo;
                    }
                    return notificationVo;
                })
                .collect(Collectors.toList());
        return PageUtils.getCopyPage(page, collect);
    }

    @Override
    public NotificationVo toVo(Notification notification, boolean isSetUserOfVo) {
        NotificationVo notificationVo = new NotificationVo();
        BeanUtils.copyProperties(notification,notificationVo);
        String resourceId = notification.getCoverResourceId();
        if (StrUtil.isNotBlank(resourceId)) {
            String coverUrl = resourceService.getImgUrlByObjectId(resourceId);
            notificationVo.setCover(coverUrl);
        }
        if (isSetUserOfVo) {
            Long userId = notification.getSendUserId();
            UserSimpleVo userSimpleVo = userService.getUserSimpleVo(userId);
            notificationVo.setUser(userSimpleVo);
        }
        return notificationVo;
    }

    @Override
    public boolean add(NotificationAddRequest request) {
        UserDto userDto = CurUserUtil.getCurUserDtoThrow();
        Notification notification = new Notification();
        notification.setSendUserId(userDto.getUserId());
        notification.setReceiveUserId(request.getReceiveUserId());
        notification.setType(request.getType());
        notification.setTitle(request.getTitle());
        notification.setContent(request.getContent());
        notification.setUrl(request.getUrl());
        notification.setResourceId(request.getResourceId());

        boolean save = save(notification);
        // 放入消息队列
        if (save) {
            SimpleMessage simpleMessage = new SimpleMessage();
            simpleMessage.setType(SimpleMessage.Type.NOTIFICATION.getValue());
            simpleMessage.setId(notification.getId());
            rabbitTemplate.convertAndSend(MqConstants.MESSAGE_ADD_QUEUE,simpleMessage);
            return true;
        }
        return false;
    }

    @Override
    public NotificationItemVo systemNotificationItem() {
        UserDto userDto = CurUserUtil.getCurUserDto();
        if (userDto == null) {
            return new NotificationItemVo();
        }
        String key = RedisConstants.getSystemNotificationKey(userDto.getUserId());
        String unReadCount = (String) stringRedisTemplate.opsForHash().get(key, "unReadCount");
        String lastNotificationId = (String) stringRedisTemplate.opsForHash().get(key, "lastNotificationId");
        if (unReadCount == null || lastNotificationId == null) {
            return new NotificationItemVo();
        }
        Notification notification = getById(lastNotificationId);

        NotificationItemVo itemVo = new NotificationItemVo();
        itemVo.setUnReadCount(Integer.parseInt(unReadCount));
        itemVo.setTitle(notification.getTitle());
        itemVo.setDate(notification.getCreatedTime());
        return itemVo;
    }

    @Override
    public Boolean readAllOfSystem() {
        UserDto userDto = CurUserUtil.getCurUserDto();
        if (userDto == null) {
            return false;
        }
        String key = RedisConstants.getSystemNotificationKey(userDto.getUserId());
        stringRedisTemplate.opsForHash().put(key,"unReadCount","0");
        return true;

    }

    @Override
    public Boolean readAllOfInteract() {
        UserDto userDto = CurUserUtil.getCurUserDto();
        if (userDto == null) {
            return false;
        }
        String key = RedisConstants.getInteractNotificationKey(userDto.getUserId());
        stringRedisTemplate.opsForHash().put(key,"unReadCount","0");
        return true;
    }

    @Override
    public NotificationItemVo interactNotificationItem() {
        UserDto userDto = CurUserUtil.getCurUserDto();
        if (userDto == null) {
            return new NotificationItemVo();
        }
        String key = RedisConstants.getInteractNotificationKey(userDto.getUserId());
        String unReadCount = (String) stringRedisTemplate.opsForHash().get(key, "unReadCount");
        String lastNotificationId = (String) stringRedisTemplate.opsForHash().get(key, "lastNotificationId");
        if (unReadCount == null || lastNotificationId == null) {
            return new NotificationItemVo();
        }
        Notification notification = getById(lastNotificationId);

        NotificationItemVo itemVo = new NotificationItemVo();
        itemVo.setUnReadCount(Integer.parseInt(unReadCount));
        itemVo.setTitle(notification.getContent());
        itemVo.setDate(notification.getCreatedTime());
        return itemVo;
    }
}




