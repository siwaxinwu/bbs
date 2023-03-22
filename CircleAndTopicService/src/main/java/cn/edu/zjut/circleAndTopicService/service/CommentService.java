package cn.edu.zjut.circleAndTopicService.service;

import cn.edu.zjut.circleAndTopicService.model.dto.comment.CommentAddRequest;
import cn.edu.zjut.circleAndTopicService.model.dto.comment.CommentQueryRequest;
import cn.edu.zjut.circleAndTopicService.model.dto.comment.CommentUpdateRequest;
import cn.edu.zjut.circleAndTopicService.model.entity.Comment;
import cn.edu.zjut.circleAndTopicService.model.vo.CommentVo;
import cn.edu.zjut.common.model.PageRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author bert
* @description 针对表【comment(评论表)】的数据库操作Service
* @createDate 2023-01-15 15:23:57
*/
public interface CommentService extends IService<Comment> {

    /**
     * 添加评论
     * @param request request
     * @return boolean
     */
    CommentVo addComment(CommentAddRequest request);

    /**
     * 更新评论
     * @param request request
     * @return boolean
     */
    boolean updateComment(CommentUpdateRequest request);

    /**
     * 条件查询评论
     * @param commentQueryRequest commentQueryRequest
     * @return Page<Comment>
     */
    Page<Comment> queryCommentPage(CommentQueryRequest commentQueryRequest);

    /**
     * 获取评论的回复
     * @param page page
     * @param commentId commentId
     * @return Page<CommentVo>
     */
    Page<CommentVo> getReplyPage(Page<Comment> page, Long commentId);

    List<CommentVo> getHotCommentOfPost(Long postId, Integer count);

    CommentVo toCommentVo(Comment comment);
    default List<CommentVo> toCommentVo(List<Comment> commentList) {
        return commentList.stream().map(this::toCommentVo).collect(Collectors.toList());
    }
    CommentVo getCommentVo(Long commentId);

    void setHotReplyListOfCommentVo(CommentVo commentVo, Integer count);

    Page<CommentVo> queryReplyVoPage(CommentQueryRequest request);

    void setReplyUserOfCommentVo(CommentVo commentVo, Long userId);

    Page<CommentVo> queryCommentVoPageOfPost(CommentQueryRequest request);

    Page<CommentVo> getCommentOfCurUser(PageRequest request);

    // region 点赞

    /**
     * 点赞评论
     * @param commentId commentId
     * @return true成功 false失败
     */
    Boolean thumb(Long commentId);

    /**
     * 取消点赞评论
     * @param commentId commentId
     * @return true成功 false失败
     */
    Boolean cancelThumb(Long commentId);

    // endregion 点赞
}
