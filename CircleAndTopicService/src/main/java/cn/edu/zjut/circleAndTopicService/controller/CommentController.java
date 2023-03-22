package cn.edu.zjut.circleAndTopicService.controller;

import cn.edu.zjut.circleAndTopicService.model.dto.comment.CommentAddRequest;
import cn.edu.zjut.circleAndTopicService.model.dto.comment.CommentQueryRequest;
import cn.edu.zjut.circleAndTopicService.model.dto.comment.CommentUpdateRequest;
import cn.edu.zjut.circleAndTopicService.model.entity.Comment;
import cn.edu.zjut.circleAndTopicService.model.vo.CommentVo;
import cn.edu.zjut.circleAndTopicService.service.CommentService;
import cn.edu.zjut.common.annotation.Log;
import cn.edu.zjut.common.annotation.RequireAdmin;
import cn.edu.zjut.common.model.PageRequest;
import cn.edu.zjut.common.model.Result;
import cn.edu.zjut.common.utils.PageUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author bert
 * @description 评论 接口
 * @date 2023/1/10 22:47
 */
@RestController
@RequestMapping("comment")
@Api(tags = "评论")
public class CommentController {

    @Resource
    private CommentService commentService;

    // region 增删改查

    @PostMapping("/page")
    @ApiOperation("查询")
    public Result<Page<Comment>> getCommentList(@RequestBody CommentQueryRequest request) {
       return Result.ok(commentService.queryCommentPage(request));
    }

    @GetMapping("/{commentId}")
    @ApiOperation("查询单条评论")
    public Result<CommentVo> getCommentById(@PathVariable Long commentId) {
        return Result.ok(commentService.getCommentVo(commentId));
    }

    @PostMapping("/page/of/post")
    @ApiOperation("查询某个动态的评论")
    public Result<Page<CommentVo>> getCommentPage(@RequestBody CommentQueryRequest request) {
        Page<CommentVo> voPage = commentService.queryCommentVoPageOfPost(request);
        return Result.ok(voPage);
    }

    @PostMapping("/page/of/comment")
    @ApiOperation("查询回复")
    public Result<Page<CommentVo>> getReplyPage(@RequestBody CommentQueryRequest request) {
        Page<CommentVo> voPage = commentService.queryReplyVoPage(request);
        return Result.ok(voPage);
    }

    @PostMapping("/page/of/me")
    @ApiOperation("查询登录用户的评论")
    public Result<Page<CommentVo>> getCommentOfMe(@RequestBody PageRequest request) {
        Page<CommentVo> voPage = commentService.getCommentOfCurUser(request);
        return Result.ok(voPage);
    }


    @PostMapping
    @ApiOperation("添加评论")
    @Log
    public Result<CommentVo> addComment(@RequestBody CommentAddRequest request) {
        return Result.ok(commentService.addComment(request));
    }

    @PutMapping
    @ApiOperation("更新评论")
    @RequireAdmin
    public Result<Boolean> updateComment(@RequestBody CommentUpdateRequest request) {
        return Result.bool(commentService.updateComment(request));
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除评论")
    @RequireAdmin
    public Result<Boolean> deleteComment(@PathVariable Long id) {
        return Result.bool(commentService.removeById(id));
    }

    // endregion


    // region 点赞

    @GetMapping("/thumb/{commentId}")
    @ApiOperation("点赞评论")
    public Result<Boolean> thumbComment(@PathVariable Long commentId) {
        return Result.ok(commentService.thumb(commentId));
    }

    @GetMapping("/cancelThumb/{commentId}")
    @ApiOperation("取消点赞评论")
    public Result<Boolean> cancelThumbComment(@PathVariable Long commentId) {
        return Result.ok(commentService.cancelThumb(commentId));
    }
    // endregion 点赞


}
