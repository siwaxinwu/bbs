package cn.edu.zjut.circleAndTopicService.controller;

import cn.edu.zjut.circleAndTopicService.job.Top10Job;
import cn.edu.zjut.circleAndTopicService.model.dto.post.PostAddRequest;
import cn.edu.zjut.circleAndTopicService.model.dto.post.PostQueryRequest;
import cn.edu.zjut.circleAndTopicService.model.dto.post.PostUpdateRequest;
import cn.edu.zjut.circleAndTopicService.model.dto.post.PostQueryByTopicRequest;
import cn.edu.zjut.circleAndTopicService.model.entity.Post;
import cn.edu.zjut.circleAndTopicService.model.vo.PostVo;
import cn.edu.zjut.circleAndTopicService.service.PostService;
import cn.edu.zjut.common.annotation.Cache;
import cn.edu.zjut.common.annotation.CacheStrategy;
import cn.edu.zjut.common.annotation.Log;
import cn.edu.zjut.common.annotation.RequireAdmin;
import cn.edu.zjut.common.constants.PageConstants;
import cn.edu.zjut.common.model.PageRequest;
import cn.edu.zjut.common.model.Result;
import cn.edu.zjut.common.model.ScoreRequest;
import cn.edu.zjut.common.model.ScrollResult;
import cn.edu.zjut.common.utils.PageUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author bert
 * @description 动态 接口
 * @date 2023/1/10 22:47
 */
@RestController
@RequestMapping("post")
@Api(tags = "动态")
public class PostController {

    @Resource
    private PostService postService;
    @Resource
    private Top10Job top10Job;

    // region 增删改查

    @PostMapping("/list/page")
    @ApiOperation("查询")
    public Result<Page<PostVo>> getPostList(@RequestBody PostQueryRequest postQueryRequest) {
        PageUtils.checkPageParam(postQueryRequest);
        Page<Post> page = new Page<>(postQueryRequest.getPageNum(), postQueryRequest.getPageSize());
        return Result.ok(postService.page(page, postQueryRequest));
    }

    @GetMapping("/{postId}")
    @ApiOperation("查询某个动态")
    public Result<PostVo> getPostVoById(@PathVariable Long postId) {
        return Result.ok(postService.getVoById(postId));
    }

    @GetMapping("/top10")
    @ApiOperation("获取top10")
    public Result<List<PostVo>> getTop10() {
        return Result.ok(postService.getTop10());
    }

    @PutMapping("/top10")
    @ApiOperation("刷新top10")
    @RequireAdmin
    public Result<Void> updateTop10() {
        top10Job.refreshTop10();
        return Result.ok();
    }

    @PostMapping("/of/user")
    @ApiOperation("获取某用户的动态")
    public Result<Page<PostVo>> getPostListByUid(@RequestBody PostQueryRequest postQueryRequest) {
        return Result.ok(postService.getPageOfUser(postQueryRequest));
    }

    @GetMapping("/of/circle")
    @ApiOperation("获取当前用户加入圈子的动态")
    public Result<ScrollResult<PostVo>> getCirclePostPage(Long max,
                                                          @RequestParam(defaultValue = "0") Integer offset,
                                                          Integer count) {
        if (count == null || count <= 0) {
            count = PageConstants.DEFAULT_PAGE_SIZE;
        }
        return Result.ok(postService.getCirclePostFeed(max, offset,count));
    }

    @GetMapping("/of/followUser")
    @ApiOperation("获取关注用户的动态")
    public Result<ScrollResult<PostVo>> getFollowUserPostPage(Long max,
                                                          @RequestParam(defaultValue = "0") Integer offset,
                                                          Integer count) {
        if (count == null || count <= 0) {
            count = PageConstants.DEFAULT_PAGE_SIZE;
        }
        return Result.ok(postService.getFollowUserPostFeed(max, offset,count));
    }

    @PostMapping("/of/topic")
    @ApiOperation("话题下的动态列表")
    public Result<Page<PostVo>> postListByTopic(@RequestBody PostQueryByTopicRequest request) {
        return Result.ok(postService.getPostOfTopic(request));
    }

    @PostMapping("/of/liked")
    @ApiOperation("点赞动态列表")
    public Result<List<PostVo>> postListOfLiked(@RequestBody ScoreRequest request) {
        return Result.ok(postService.getPostOfLiked(request));
    }


    @PostMapping
    @ApiOperation("添加动态")
    @Log
    public Result<Boolean> addPost(@RequestBody PostAddRequest request) {
        return Result.bool(postService.save(request));
    }

    @PutMapping("/addCount/{postId}")
    @ApiOperation("增加浏览量")
    public Result<Boolean> addReadCount(@PathVariable Long postId) {
        return Result.bool(postService.addReadCount(postId));
    }

    @PutMapping
    @ApiOperation("更新动态")
    @RequireAdmin
    public Result<Boolean> updatePost(@RequestBody PostUpdateRequest request) {
        return Result.bool(postService.update(request));
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除动态")
    @RequireAdmin
    public Result<Boolean> deletePost(@PathVariable Long id) {
        return Result.bool(postService.removeById(id));
    }

    // endregion


    // region 点赞

    @GetMapping("/thumb/{postId}")
    @ApiOperation("点赞动态")
    public Result<Boolean> thumbPost(@PathVariable Long postId) {
        return Result.ok(postService.thumb(postId));
    }

    @GetMapping("/cancelThumb/{postId}")
    @ApiOperation("取消点赞动态")
    public Result<Boolean> cancelThumbPost(@PathVariable Long postId) {
        return Result.ok(postService.cancelThumb(postId));
    }
    // endregion 点赞


    // region 数据

    @GetMapping("/unReadCount/followPerson")
    @ApiOperation("关注用户的未读动态数")
    public Result<Integer> unReadFollowPerson() {
        return Result.ok(postService.unReadCountOfFollowPerson());
    }

    @PostMapping("/unReadCount/circle/list")
    @ApiOperation("加入圈子的未读消息数")
    public Result<Map<String,Integer>> unReadCountOfJoinCircle() {
        return Result.ok(postService.unReadCountOfJoinCircle());
    }

    @GetMapping("/readAll/circle/{circleId}")
    @ApiOperation("清空圈子未读数")
    public Result<Boolean> readAllOfCircle(@PathVariable Long circleId) {
        return Result.ok(postService.readAllOfCircle(circleId));
    }

    @GetMapping("/readAll/followPerson")
    @ApiOperation("清空关注用户动态未读数")
    public Result<Boolean> readAllOfFollowPerson() {
        return Result.ok(postService.readAllOfFollowPerson());
    }




    // endregion 数据






}
