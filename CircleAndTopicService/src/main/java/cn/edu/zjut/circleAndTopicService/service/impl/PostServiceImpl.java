package cn.edu.zjut.circleAndTopicService.service.impl;

import cn.edu.zjut.circleAndTopicService.mapper.CircleJoinMapper;
import cn.edu.zjut.circleAndTopicService.mapper.CircleMapper;
import cn.edu.zjut.circleAndTopicService.model.dto.post.PostAddRequest;
import cn.edu.zjut.circleAndTopicService.model.dto.post.PostQueryByTopicRequest;
import cn.edu.zjut.circleAndTopicService.model.dto.post.PostQueryRequest;
import cn.edu.zjut.circleAndTopicService.model.dto.post.PostUpdateRequest;
import cn.edu.zjut.circleAndTopicService.model.entity.*;
import cn.edu.zjut.circleAndTopicService.model.vo.CommentVo;
import cn.edu.zjut.circleAndTopicService.model.vo.PostVo;
import cn.edu.zjut.circleAndTopicService.mq.PostAddMq;
import cn.edu.zjut.circleAndTopicService.service.*;
import cn.edu.zjut.common.annotation.Cache;
import cn.edu.zjut.common.annotation.CacheStrategy;
import cn.edu.zjut.common.constants.MqConstants;
import cn.edu.zjut.common.constants.PatternConstants;
import cn.edu.zjut.common.constants.ScoreConstants;
import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.dto.UserDto;
import cn.edu.zjut.common.model.PageRequest;
import cn.edu.zjut.common.model.ScoreRequest;
import cn.edu.zjut.common.model.ScrollResult;
import cn.edu.zjut.common.redis.RedisConstants;
import cn.edu.zjut.common.utils.CurUserUtil;
import cn.edu.zjut.common.utils.PageUtils;
import cn.edu.zjut.fileService.service.ResourceService;
import cn.edu.zjut.userService.model.vo.UserSimpleVo;
import cn.edu.zjut.userService.service.FollowService;
import cn.edu.zjut.userService.service.UserService;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.zjut.circleAndTopicService.mapper.PostMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
* @author bert
* @description 针对表【post(动态表)】的数据库操作Service实现
* @createDate 2023-01-13 23:38:20
*/
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
    implements PostService{

    @Resource
    private UserService userService;
    @Resource
    private CircleService circleService;
    @Resource
    private RPostTopicService rPostTopicService;
    @Resource
    private CommentService commentService;
    @Resource
    private FollowService followService;
    @Resource
    private ResourceService resourceService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Page<PostVo> page(Page<Post> page, PostQueryRequest postQueryRequest) {
        LambdaQueryWrapper<Post> wrapper = getQueryWrapper(postQueryRequest);
        Page<Post> postPage = this.baseMapper.selectPage(page, wrapper);
        List<PostVo> postVoList = toPostVo(postPage.getRecords());
        return PageUtils.getCopyPage(postPage, postVoList);
    }

    @Override
    public Page<PostVo> getPageOfUser(PostQueryRequest postQueryRequest) {
        Long userId = postQueryRequest.getUserId();
        Page<Post> page = postQueryRequest.getPage(Post.class);

        if (userId == null || userId < 0) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR);
        }
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getUserId, userId);
        Page<Post> postPage = this.baseMapper.selectPage(page, wrapper);
        List<PostVo> collect = postPage.getRecords().stream().map(this::toPostVo).collect(Collectors.toList());
        return PageUtils.getCopyPage(postPage, collect);
    }

    @Override
    public ScrollResult<PostVo> getCirclePostFeed(long max, int offset, int count) {
        UserDto user = CurUserUtil.getCurUserDto();
        if (user == null) {
            ScrollResult<PostVo> result = new ScrollResult<>();
            result.setList(Collections.emptyList());
            result.setOffset(0);
            result.setMinTime(0L);
            return result;
        }
        return getPostVoFeed(max, offset, count, RedisConstants.getUserCircleReceiveKey(user.getUserId()));
    }

    @Override
    public ScrollResult<PostVo> getFollowUserPostFeed(long max, int offset, int count) {
        UserDto user = CurUserUtil.getCurUserDto();
        if (user == null) {
            ScrollResult<PostVo> result = new ScrollResult<>();
            result.setList(Collections.emptyList());
            result.setOffset(0);
            result.setMinTime(0L);
            return result;
        }
        return getPostVoFeed(max, offset, count, RedisConstants.getUserFollowReceiveKey(user.getUserId()));
    }

    @Override
    public Boolean thumb(Long postId) {
        return changeThumb(postId,true);
    }

    @Override
    public Boolean cancelThumb(Long postId) {
        return changeThumb(postId,false);
    }

    private boolean changeThumb(Long postId,boolean isThumb) {
        if (postId == null || postId <=0 ) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR);
        }
        UserDto user = CurUserUtil.getCurUserDtoThrow();
        String key = RedisConstants.getThumbPostKey(user.getUserId());
        boolean isSuccess = false;
        long score = System.currentTimeMillis();
        if (isThumb) {
            isSuccess = stringRedisTemplate.opsForZSet().add(key, String.valueOf(postId), score) != null;
        } else {
            Long removeCount = stringRedisTemplate.opsForZSet().remove(key, String.valueOf(postId));
            isSuccess = removeCount != null && removeCount == 1L;
        }
        if (isSuccess) {
            if (isThumb) {
                lambdaUpdate().eq(Post::getId,postId).setSql(" thumb_count = thumb_count+1 ").update();
            } else {
                lambdaUpdate().eq(Post::getId,postId).setSql(" thumb_count = thumb_count-1 ").update();
            }
        }
        return isSuccess;
    }

    @Override
    public Page<PostVo> getPostOfTopic(PostQueryByTopicRequest request) {
        Long topicId = request.getTopicId();
        Boolean isDesc = request.getIsDesc();
        Page<RPostTopic> rPage = rPostTopicService.lambdaQuery()
                                        .eq(RPostTopic::getTopicId, topicId)
                                        .orderByDesc(isDesc, RPostTopic::getCreatedTime)
                                        .page(request.getPage(RPostTopic.class));
        List<PostVo> collect = rPage.getRecords().stream().map(rPostTopic -> {
            Long postId = rPostTopic.getPostId();
            return getVoById(postId);
        }).collect(Collectors.toList());
        return PageUtils.getCopyPage(rPage,collect);
    }

    @Override
    public List<PostVo> getPostOfLiked(ScoreRequest request) {
        UserDto userDto = CurUserUtil.getCurUserDtoThrow();
        if (request == null) {
            request = new ScoreRequest();
        }
        long timeMillis = System.currentTimeMillis();
        if (request.getMax() == null) {
            request.setMax(timeMillis);
        }
        if (request.getMin() == null || request.getMin() == 0) {
            request.setMin(timeMillis-ScoreConstants.MAX_DATE_SCORE_INTERVAL);
        } else {
            if (request.getMin() < timeMillis-ScoreConstants.MAX_DATE_SCORE_INTERVAL) {
                throw new BusinessException(CodeEnum.PARAMS_ERROR,"只能查询最近一年的记录");
            }
        }

        String key = RedisConstants.getThumbPostKey(userDto.getUserId());
        Set<String> postIds = stringRedisTemplate.opsForZSet().reverseRangeByScore(key, request.getMin(), request.getMax());
        List<PostVo> voList = postIds.stream().map(id -> this.getVoById(Long.parseLong(id))).collect(Collectors.toList());
        return voList;
    }

    @Override
    public Integer unReadCountOfFollowPerson() {
        UserDto userDto = CurUserUtil.getCurUserDto();
        if (userDto == null) {
            return 0;
        }
        String key = RedisConstants.getUnReadFollowPersonCountKey(userDto.getUserId());
        String count = stringRedisTemplate.opsForValue().get(key);
        return count==null?0:Integer.parseInt(count);
    }

    @Override
    public Map<String, Integer> unReadCountOfJoinCircle() {
        UserDto userDto = CurUserUtil.getCurUserDto();
        if (userDto == null) {
            return null;
        }
        Long userId = userDto.getUserId();
        String key = RedisConstants.getFollowCircleKey(userId);
        Set<String> circleSet = stringRedisTemplate.opsForSet().members(key);
        if (circleSet == null) {
            return null;
        }
        HashMap<String, Integer> map = new HashMap<>(circleSet.size());
        for (String circleId : circleSet) {
            String unReadCircleCountKey = RedisConstants.getUnReadCircleCountKey(userId, Long.valueOf(circleId));
            String countStr = stringRedisTemplate.opsForValue().get(unReadCircleCountKey);
            Integer count = countStr == null ? 0 : Integer.parseInt(countStr);
            map.put(circleId, count);
        }
        return map;
    }

    @Override
    public Boolean readAllOfCircle(Long circleId) {
        UserDto userDto = CurUserUtil.getCurUserDto();
        if (userDto == null) {
            return false;
        }
        String key = RedisConstants.getUnReadCircleCountKey(userDto.getUserId(), circleId);
        stringRedisTemplate.opsForValue().set(key,"0");
        return true;
    }

    @Override
    public Boolean readAllOfFollowPerson() {
        UserDto userDto = CurUserUtil.getCurUserDto();
        if (userDto == null) {
            return false;
        }
        String key = RedisConstants.getUnReadFollowPersonCountKey(userDto.getUserId());
        stringRedisTemplate.opsForValue().set(key,"0");
        return true;
    }

    @Override
    public Boolean addReadCount(Long postId) {
        if (postId == null || postId < 0) {
            return false;
        }
        return lambdaUpdate().eq(Post::getId,postId).setSql("read_count=read_count+1").update();
    }

    @Override
    public List<PostVo> getTop10() {
        List<String> strIds = stringRedisTemplate.opsForList().range(RedisConstants.TOP10, 0, -1);
        if (strIds == null) {
            return Collections.emptyList();
        }
        ArrayList<PostVo> postVos = new ArrayList<>(strIds.size());
        for (String strId : strIds) {
            postVos.add(getVoById(Long.parseLong(strId)));
        }
        return postVos;
    }

    private Boolean isThumbPost(Long userId, Long postId) {
        String key = RedisConstants.getThumbPostKey(userId);
        return stringRedisTemplate.opsForZSet().score(key,String.valueOf(postId)) != null;
    }


    private ScrollResult<PostVo> getPostVoFeed(long max, int offset, int count, String receiveZSetKey) {
        Set<ZSetOperations.TypedTuple<String>> typedTuples =
                stringRedisTemplate.opsForZSet().reverseRangeByScoreWithScores(receiveZSetKey, 0, max, offset, count);
        if (typedTuples == null || typedTuples.isEmpty()) {
            return ScrollResult.empty(max, offset);
        }

        // 解析数据：blogId、minTime（时间戳）、offset
        List<Long> ids = new ArrayList<>(typedTuples.size());
        long minTime = max;
        int os = 1;
        for (ZSetOperations.TypedTuple<String> tuple : typedTuples) {
            ids.add(Long.valueOf(tuple.getValue()));
            long time = tuple.getScore().longValue();
            if(time == minTime) {
                os++;
            } else {
                minTime = time;
                os = 1;
            }
        }
        String idStr = StrUtil.join(",", ids);
        List<Post> posts = query().in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list();

        ScrollResult<PostVo> scrollResult = new ScrollResult<>();
        scrollResult.setList(toPostVo(posts));
        scrollResult.setMinTime(minTime);
        scrollResult.setOffset(os);
        return scrollResult;
    }


    @Override
    public PostVo getVoById(Long postId) {
        Post post = this.baseMapper.selectById(postId);
        return toPostVo(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PostAddRequest postAddRequest) {
        UserDto userDto = CurUserUtil.getCurUserDtoThrow();
        Long circleId = postAddRequest.getCircleId();
        // 圈子id有效 -> 判断是否已加入圈子
        if ( circleId != null && circleId>0 ) {
            List<Circle> joinList = circleService.getJoinList(userDto.getUserId());
            boolean isJoin = joinList.stream().anyMatch(circle -> circleId.equals(circle.getId()));
            if (!isJoin) {
                throw new BusinessException(CodeEnum.FAIL, "请先加入圈子");
            }
        }
        // 解析图片
        String pattern = PatternConstants.PARSE_POST_IMAGE;
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(postAddRequest.getContent());
        if (m.find()) {
            String[] split = m.group(2).split(",");
            if (split.length > 9) {
                throw new BusinessException(CodeEnum.PARAMS_ERROR,"图片数量不能大于9");
            }
        }
        Post post = new Post();
        post.setUserId(userDto.getUserId());
        post.setType(postAddRequest.getType());
        post.setContent(postAddRequest.getContent());
        post.setCircleId(circleId);

        // 插入post
        boolean isSuccess = this.baseMapper.insert(post) > 0;
        if (isSuccess) {
            // 发送至消息队列
            PostAddMq postAddMq = new PostAddMq();
            BeanUtils.copyProperties(postAddRequest,postAddMq);
            postAddMq.setPostId(post.getId());
            postAddMq.setPostUserId(userDto.getUserId());
            rabbitTemplate.convertAndSend(MqConstants.POST_ADD_QUEUE,postAddMq);
            return true;
        } else {
            throw new BusinessException(CodeEnum.SYSTEM_ERROR);
        }
    }

    @Override
    public boolean update(PostUpdateRequest postUpdateRequest) {
        Long id = postUpdateRequest.getId();
        if (id == null || id < 0) {
            return false;
        }
        String content = postUpdateRequest.getContent();
        Integer isEssence = postUpdateRequest.getIsEssence();
        Integer isTop = postUpdateRequest.getIsTop();
        String status = postUpdateRequest.getStatus();

        LambdaUpdateWrapper<Post> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Post::getId, id);
        wrapper.set(content!=null, Post::getContent, content)
                .set(isEssence!=null, Post::getIsEssence, isEssence)
                .set(isTop!=null, Post::getIsTop, isTop)
                .set(status!=null, Post::getStatus, status);

        return this.baseMapper.update(null,wrapper) > 0;
    }

    @Override
    public boolean removeById(Long id) {
        if (id == null || id < 0) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR);
        }
        Post post = this.baseMapper.selectById(id);
        if (post == null) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR, "该动态不存在");
        }
        boolean isSuccess = this.baseMapper.deleteById(id) > 0;
        if (!isSuccess) {
            throw new BusinessException(CodeEnum.OPERATION_ERROR, "删除动态失败");
        }
        Long circleId = post.getCircleId();
        if (circleId == null || circleId == 0) {
            return true;
        }
        // 从用户圈子收件箱中删除
        List<Long> joinUserIds = circleService.getJoinUserIds(circleId);
        for (Long uid : joinUserIds) {
            String receiveKey = RedisConstants.getUserCircleReceiveKey(uid);
            stringRedisTemplate.opsForZSet().remove(receiveKey, String.valueOf(id));
        }
        // 从粉丝圈子收件箱中删除
        UserDto userDto = CurUserUtil.getCurUserDtoThrow();
        List<Long> fanIds = followService.getFanIds(userDto.getUserId());
        // 遍历粉丝id，向其关注用户收件箱中删除postId
        for (Long uid : fanIds) {
            String key = RedisConstants.getUserFollowReceiveKey(uid);
            stringRedisTemplate.opsForZSet().remove(key, String.valueOf(id));
        }
        return true;
    }

    private PostVo toPostVo(Post post) {
        if (post == null) {
            return null;
        }
        PostVo postVo = new PostVo();
        BeanUtils.copyProperties(post, postVo);
        // 用户信息
        UserSimpleVo userSimpleVo = userService.getUserSimpleVo(post.getUserId());
        // 圈子信息
        Circle circle = circleService.getById(post.getCircleId());
        // 话题信息
        List<Topic> topicList = rPostTopicService.getListByPostId(post.getId());
        // 热门评论信息
        if (post.getCommentCount() > 0) {
            List<CommentVo> hotCommentOfPost = commentService.getHotCommentOfPost(post.getId(),3);
            postVo.setHotCommentList(hotCommentOfPost);
        } else {
            postVo.setHotCommentList(Collections.emptyList());
        }

        // 当前用户是否点赞该动态
        UserDto userDto = CurUserUtil.getCurUserDto();
        if (userDto!=null) {
            postVo.setIsThumb(isThumbPost(userDto.getUserId(),post.getId()));
        }
        // 解析图片
        String pattern = PatternConstants.PARSE_POST_IMAGE;
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(post.getContent());
        if (m.find()) {
            String[] split = m.group(2).split(",");
            List<String> urlList = resourceService.objectIdsToImgUrlList(List.of(split));
            postVo.setImageList(urlList);
            String content = m.replaceAll(matchResult -> matchResult.group(1));
            postVo.setContent(content);
        } else {
            postVo.setImageList(Collections.emptyList());
        }
        postVo.setUser(userSimpleVo);
        postVo.setCircle(circle);
        postVo.setTopicList(topicList);
        return postVo;
    }

    private List<PostVo> toPostVo(List<Post> postList) {
        if (postList == null) {
            return Collections.emptyList();
        }
        return postList.stream().map(this::toPostVo).collect(Collectors.toList());
    }

    private LambdaQueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest) {
        Long circleId = postQueryRequest.getCircleId();
        Long userId = postQueryRequest.getUserId();
        String type = postQueryRequest.getType();
        String content = postQueryRequest.getContent();
        Integer isEssence = postQueryRequest.getIsEssence();
        String status = postQueryRequest.getStatus();
        Boolean isDesc = postQueryRequest.getIsDesc();

        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(circleId!=null,Post::getCircleId, circleId);
        wrapper.eq(userId!=null,Post::getUserId, userId);
        wrapper.eq(type!=null,Post::getType, type);
        wrapper.eq(isEssence!=null,Post::getIsEssence, isEssence);
        wrapper.eq(status!=null,Post::getStatus, status);
        wrapper.orderByDesc(isDesc!=null&&isDesc,Post::getCreatedTime);

        wrapper.like(content!=null, Post::getContent, content);
        return wrapper;
    }

}




