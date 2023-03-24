package cn.edu.zjut.circleAndTopicService.service.impl;

import cn.edu.zjut.circleAndTopicService.enums.CommentTypeEnum;
import cn.edu.zjut.circleAndTopicService.enums.OrderModeEnum;
import cn.edu.zjut.circleAndTopicService.model.dto.comment.CommentAddRequest;
import cn.edu.zjut.circleAndTopicService.model.dto.comment.CommentQueryRequest;
import cn.edu.zjut.circleAndTopicService.model.dto.comment.CommentUpdateRequest;
import cn.edu.zjut.circleAndTopicService.model.entity.Post;
import cn.edu.zjut.circleAndTopicService.model.vo.CommentVo;
import cn.edu.zjut.circleAndTopicService.service.PostService;
import cn.edu.zjut.common.constants.MqConstants;
import cn.edu.zjut.common.dto.UserDto;
import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.model.PageRequest;
import cn.edu.zjut.common.redis.RedisConstants;
import cn.edu.zjut.common.utils.CurUserUtil;
import cn.edu.zjut.common.utils.HttpUtils;
import cn.edu.zjut.common.utils.PageUtils;
import cn.edu.zjut.userService.model.vo.UserSimpleVo;
import cn.edu.zjut.userService.service.BlockService;
import cn.edu.zjut.userService.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.zjut.circleAndTopicService.model.entity.Comment;
import cn.edu.zjut.circleAndTopicService.service.CommentService;
import cn.edu.zjut.circleAndTopicService.mapper.CommentMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author bert
* @description 针对表【comment(评论表)】的数据库操作Service实现
* @createDate 2023-01-15 15:23:57
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{

    @Resource
    private PostService postService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserService userService;
    @Resource
    private BlockService blockService;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public CommentVo addComment(CommentAddRequest request) {
        UserDto userDto = CurUserUtil.getCurUserDtoThrow();

        // 判断动态是否存在
        Long postId = request.getPostId();
        Post post = postService.lambdaQuery().eq(Post::getId, postId).one();
        if (post == null) {
            throw new BusinessException(CodeEnum.FAIL, "动态不存在");
        }
        // 判断用户是否被拉黑
        Long userId = post.getUserId();
        boolean blocked = blockService.isBlocked(userId,userDto.getUserId());
        if (blocked) {
            throw new BusinessException(CodeEnum.FAIL,"您已被楼主拉黑，无法发送");
        }

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUserId(userDto.getUserId());
        comment.setType(request.getType());
        comment.setReplyUserId(userId);
        CommentTypeEnum commentTypeEnum = CommentTypeEnum.transform(request.getType());
        // 若为回复，设置回复的评论id和用户id、父级评论id
        if (CommentTypeEnum.REPLY.equals(commentTypeEnum)) {
            Long replyCommentId = request.getReplyCommentId();
            Long replyUserId = request.getReplyUserId();
            Long commentId = request.getCommentId();
            if (replyCommentId == null || replyUserId == null || commentId == null) {
                throw new BusinessException(CodeEnum.PARAMS_ERROR);
            }
            comment.setReplyCommentId(replyCommentId);
            comment.setReplyUserId(replyUserId);
            comment.setCommentId(commentId);
        }
        comment.setPostId(postId);
        comment.setIp(HttpUtils.getIpAddress());

        // 插入到数据库
        if (this.baseMapper.insert(comment) > 0) {
            // 消息队列
            rabbitTemplate.convertAndSend(MqConstants.COMMENT_ADD_QUEUE,comment.getId());
            return this.getCommentVo(comment.getId());
        }
        throw new BusinessException(CodeEnum.FAIL, "评论失败");
    }

    @Override
    public boolean updateComment(CommentUpdateRequest request) {
        CommentTypeEnum commentTypeEnum = CommentTypeEnum.transform(request.getType());
        Comment comment = new Comment();
        comment.setId(request.getId());
        comment.setContent(request.getContent());
        comment.setType(commentTypeEnum.getValue());
        return this.baseMapper.updateById(comment) > 0;
    }

    @Override
    public Page<Comment> queryCommentPage(CommentQueryRequest commentQueryRequest) {
        PageUtils.checkPageParam(commentQueryRequest);
        LambdaQueryWrapper<Comment> wrapper = getQueryWrapper(commentQueryRequest);
        Page<Comment> page = new Page<>(commentQueryRequest.getPageNum(), commentQueryRequest.getPageSize());
        return page(page, wrapper);
    }

    @Override
    public Page<CommentVo> getReplyPage(Page<Comment> page, Long commentId) {
        Page<Comment> commentPage = lambdaQuery().eq(Comment::getReplyCommentId, commentId).page(page);
        List<Comment> records = commentPage.getRecords();
        List<CommentVo> commentVoList = records.stream().map(this::toCommentVo).collect(Collectors.toList());
        return PageUtils.getCopyPage(commentPage, commentVoList);
    }

    @Override
    public List<CommentVo> getHotCommentOfPost(Long postId, Integer count) {
        List<Comment> list = lambdaQuery().eq(Comment::getPostId, postId)
                                            .eq(Comment::getType, CommentTypeEnum.COMMENT.getValue())
                                            .orderByDesc(Comment::getThumbCount, Comment::getReplyCount, Comment::getCreatedTime)
                                            .last(" limit " + count)
                                            .list();
        return list.stream().map(this::toCommentVo).collect(Collectors.toList());
    }

    @Override
    public CommentVo toCommentVo(Comment comment) {
        CommentVo commentVo = new CommentVo();
        BeanUtils.copyProperties(comment,commentVo);
        UserSimpleVo userSimpleVo = userService.getUserSimpleVo(comment.getUserId());
        // 设置用户
        commentVo.setUser(userSimpleVo);
        // ip属地，暂不设置
//        commentVo.setIpAddress(AddressUtils.getRealAddressByIp(comment.getIp()));
        // 当前用户是否点过赞
        UserDto curUser = CurUserUtil.getCurUserDto();
        if (curUser != null) {
            commentVo.setIsThumb(isThumbComment(curUser.getUserId(), comment.getId()));
        }

        // 其他默认值
        commentVo.setHotReplyList(Collections.emptyList());
        return commentVo;
    }

    private Boolean isThumbComment(Long userId, Long commentId) {
        String key = RedisConstants.getThumbCommentKey(userId);
        return stringRedisTemplate.opsForSet().isMember(key, commentId.toString());
    }

    @Override
    public CommentVo getCommentVo(Long commentId) {
        Comment comment = this.baseMapper.selectById(commentId);
        return toCommentVo(comment);
    }

    @Override
    public void setHotReplyListOfCommentVo(CommentVo commentVo, Integer count) {
        if (commentVo.getReplyCount() == 0) {
            commentVo.setHotReplyList(Collections.emptyList());
            return;
        }
        Long commentId = commentVo.getId();
        List<Comment> list = lambdaQuery().eq(Comment::getCommentId, commentId)
                                            .orderByDesc(Comment::getThumbCount)
                                            .last(" limit " + count).list();
        List<CommentVo> collect = list.stream().map(this::toCommentVo).collect(Collectors.toList());
        commentVo.setHotReplyList(collect);
    }

    @Override
    public Page<CommentVo> queryCommentVoPageOfPost(CommentQueryRequest request) {
        PageUtils.checkPageParam(request);
        // 设置查询条件
        CommentQueryRequest newRequest = new CommentQueryRequest();
        newRequest.setPostId(request.getPostId());
        newRequest.setPageNum(request.getPageNum());
        newRequest.setPageSize(request.getPageSize());
        newRequest.setOrderMode(request.getOrderMode());
        List<Comment> commentList = new ArrayList<>();
        if (request.getPageNum() <= 1) {
            // 第一页 获取置顶评论
            newRequest.setType(CommentTypeEnum.TOP.getValue());
            Page<Comment> topCommentPage = queryCommentPage(newRequest);
            commentList.addAll(topCommentPage.getRecords());
        }
        // 获取普通评论
        newRequest.setType(CommentTypeEnum.COMMENT.getValue());
        Page<Comment> commentPage = queryCommentPage(newRequest);
        commentPage.setTotal(commentPage.getTotal() + commentList.size());
        // 合并成最终评论列表
        commentList.addAll(commentPage.getRecords());
        List<CommentVo> collect = commentList.stream().map(this::toCommentVo).collect(Collectors.toList());
        collect.forEach(commentVo -> setHotReplyListOfCommentVo(commentVo,3));
        return PageUtils.getCopyPage(commentPage, collect);
    }

    @Override
    public Page<CommentVo> getCommentOfCurUser(PageRequest request) {
        UserDto userDto = CurUserUtil.getCurUserDtoThrow();
        PageUtils.checkPageParam(request);
        Page<Comment> page = lambdaQuery()
                .eq(Comment::getUserId, userDto.getUserId())
                .orderByDesc(Comment::getCreatedTime)
                .page(request.getPage(Comment.class));
        List<Comment> records = page.getRecords();
        List<CommentVo> voList = new ArrayList<>(records.size());
        for (Comment comment : records) {
            CommentVo commentVo = toCommentVo(comment);
            if (comment.getType().equals(CommentTypeEnum.REPLY.getValue())) {
                CommentVo parentCommentVo = getCommentVo(comment.getReplyCommentId());
                commentVo.setParentComment(parentCommentVo);
            }
            voList.add(commentVo);
        }
        return PageUtils.getCopyPage(page,voList);
    }

    @Override
    public Boolean thumb(Long commentId) {
        if (commentId == null || commentId <=0 ) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR);
        }
        UserDto user = CurUserUtil.getCurUserDtoThrow();
        String key = RedisConstants.getThumbCommentKey(user.getUserId());
        Long addCount = stringRedisTemplate.opsForSet().add(key, commentId.toString());
        boolean isSuccess = addCount != null && addCount == 1L;
        if (isSuccess) {
            lambdaUpdate().eq(Comment::getId,commentId).setSql(" thumb_count = thumb_count+1 ").update();
        }
        return isSuccess;
    }

    @Override
    public Boolean cancelThumb(Long commentId) {
        if (commentId == null || commentId <=0 ) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR);
        }
        UserDto user = CurUserUtil.getCurUserDtoThrow();
        String key = RedisConstants.getThumbCommentKey(user.getUserId());
        Long removeCount = stringRedisTemplate.opsForSet().remove(key, commentId.toString());
        boolean isSuccess = removeCount != null && removeCount == 1L;
        if (isSuccess) {
            lambdaUpdate().eq(Comment::getId,commentId).setSql(" thumb_count = thumb_count-1 ").update();
        }
        return isSuccess;
    }


    @Override
    public Page<CommentVo> queryReplyVoPage(CommentQueryRequest request) {
        PageUtils.checkPageParam(request);
        // 设置查询条件
        CommentQueryRequest newRequest = new CommentQueryRequest();
        newRequest.setCommentId(request.getCommentId());
        newRequest.setPageNum(request.getPageNum());
        newRequest.setPageSize(request.getPageSize());
        newRequest.setOrderMode(request.getOrderMode());
        newRequest.setType(CommentTypeEnum.REPLY.getValue());

        // 转换为commentVo列表
        Page<Comment> commentPage = queryCommentPage(newRequest);
        List<Comment> commentList = commentPage.getRecords();
        List<CommentVo> commentVoList = new ArrayList<>(commentList.size());
        for (Comment comment : commentList) {
            CommentVo commentVo = toCommentVo(comment);
            // 非回复自己则设置被回复用户的信息
            if (!comment.getUserId().equals(comment.getReplyUserId())) {
                setReplyUserOfCommentVo(commentVo,comment.getReplyUserId());
            }
            commentVoList.add(commentVo);
        }
        return PageUtils.getCopyPage(commentPage, commentVoList);
    }

    @Override
    public void setReplyUserOfCommentVo(CommentVo commentVo, Long userId) {
        commentVo.setReplyUser(userService.getUserSimpleVo(userId));
    }

    private LambdaQueryWrapper<Comment> getQueryWrapper(CommentQueryRequest commentQueryRequest) {
        String content = commentQueryRequest.getContent();
        Long userId = commentQueryRequest.getUserId();
        String type = commentQueryRequest.getType();
        Long postId = commentQueryRequest.getPostId();
        Integer orderMode = commentQueryRequest.getOrderMode();
        Long replyCommentId = commentQueryRequest.getReplyCommentId();
        Long commentId = commentQueryRequest.getCommentId();

        if (content == null && userId == null && type == null && postId == null && replyCommentId == null) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR, "请至少输入一个查询条件");
        }
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userId != null, Comment::getUserId, userId);
        wrapper.eq(type != null, Comment::getType, type);
        wrapper.eq(postId != null, Comment::getPostId, postId);
        wrapper.eq(replyCommentId != null, Comment::getReplyCommentId, replyCommentId);
        wrapper.eq(commentId != null, Comment::getCommentId, commentId);
        wrapper.like(content != null, Comment::getContent, content);
        OrderModeEnum mode = OrderModeEnum.transform(orderMode);
        switch (mode) {
            case NEW:
                wrapper.orderByDesc(Comment::getCreatedTime);
                break;
            case HOT:
                wrapper.orderByDesc(Comment::getReplyCount).orderByDesc(Comment::getThumbCount)
                        .orderByDesc(Comment::getCreatedTime);
            case OLD:
            default:
        }
        return wrapper;
    }
}




