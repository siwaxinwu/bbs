package cn.edu.zjut.circleAndTopicService.mq;

import cn.edu.zjut.circleAndTopicService.model.entity.Circle;
import cn.edu.zjut.circleAndTopicService.model.entity.Post;
import cn.edu.zjut.circleAndTopicService.model.entity.RPostTopic;
import cn.edu.zjut.circleAndTopicService.service.*;
import cn.edu.zjut.common.constants.MqConstants;
import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.redis.RedisConstants;
import cn.edu.zjut.userService.model.entity.User;
import cn.edu.zjut.userService.service.FollowService;
import cn.edu.zjut.userService.service.UserService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @author bert
 * @date 2023/2/24 21:58
 */
@Component
public class Listener {

    @Resource
    private PostService postService;
    @Resource
    private UserService userService;
    @Resource
    private RPostTopicService rPostTopicService;
    @Resource
    private CircleService circleService;
    @Resource
    private FollowService followService;
    @Resource
    private TopicService topicService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @RabbitListener(queues = MqConstants.POST_ADD_QUEUE)
    public void listenPostAdd (PostAddMq request) {
        if (request.getPostUserId()==null) {
            return;
        }
        Long circleId = request.getCircleId();
        Long postId = request.getPostId();
        // 建立话题-动态关联
        Set<Long> topicIds = request.getTopicIds();
        topicIds.forEach(topicId -> {
            if (topicService.increaseCount(topicId,1)) {
                RPostTopic rPostTopic = new RPostTopic();
                rPostTopic.setPostId(postId);
                rPostTopic.setTopicId(topicId);
                rPostTopicService.save(rPostTopic);
            }
        });

        // 个人动态数+1
        userService.lambdaUpdate().setSql("post_count=post_count+1").eq(User::getUserId,request.getUserId()).update();

        // 允许未选择圈子
        if (circleId == null || circleId == 0) {
            return;
        }
        // 圈子的动态数+1
        circleService.lambdaUpdate().setSql(" post_count=post_count+1").eq(Circle::getId, circleId).update();

        Post newPost = postService.getById(postId);
        // 发送新动态至所有加入圈子的用户
        List<Long> joinUserIds = circleService.getJoinUserIds(circleId);
            // 遍历圈子所有用户进行通知
        for (Long uid : joinUserIds) {
            String userCircleReceiveKey = RedisConstants.getUserCircleReceiveKey(uid);
            stringRedisTemplate.opsForZSet()
                    .add(userCircleReceiveKey, newPost.getId().toString(), newPost.getCreatedTime().getTime());

            // 圈子未读记录+1
            // 过滤自己
            if (request.getPostUserId().equals(uid)) {
                continue;
            }
            String circleUnReadCountKey = RedisConstants.getUnReadCircleCountKey(uid, circleId);
            stringRedisTemplate.opsForValue().increment(circleUnReadCountKey);
        }
        // 发送新动态至所有粉丝
        List<Long> fanIds = followService.getFanIds(request.getPostUserId());
            // 遍历粉丝id，向其关注用户收件箱中添加postId
        for (Long uid : fanIds) {
            String key = RedisConstants.getUserFollowReceiveKey(uid);
            stringRedisTemplate.opsForZSet()
                    .add(key, newPost.getId().toString(), newPost.getCreatedTime().getTime());
            String unReadFollowPersonCountKey = RedisConstants.getUnReadFollowPersonCountKey(uid);
            // 关注未读记录+1
            stringRedisTemplate.opsForValue().increment(unReadFollowPersonCountKey);
        }
    }



}
