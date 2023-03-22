package cn.edu.zjut.circleAndTopicService.controller;

import cn.edu.zjut.circleAndTopicService.model.dto.topic.TopicAddOrUpdateRequest;
import cn.edu.zjut.circleAndTopicService.model.dto.topic.TopicQueryRequest;
import cn.edu.zjut.circleAndTopicService.model.entity.Topic;
import cn.edu.zjut.circleAndTopicService.service.TopicService;
import cn.edu.zjut.common.annotation.Cache;
import cn.edu.zjut.common.annotation.RequireAdmin;
import cn.edu.zjut.common.constants.PageConstants;
import cn.edu.zjut.common.dto.UserDto;
import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.model.Result;
import cn.edu.zjut.common.utils.CurUserUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author bert
 * @description 话题 接口
 * @date 2023/1/10 22:47
 */
@RestController
@RequestMapping("topic")
@Api(tags = "话题")
public class TopicController {

    @Resource
    private TopicService topicService;

    // region 增删改查

    @PostMapping("/list/page")
    @ApiOperation("分页查询")
    public Result<Page<Topic>> getTopicList(@RequestBody TopicQueryRequest request) {
        LambdaQueryWrapper<Topic> wrapper = getQueryWrapper(request);
        Page<Topic> page = topicService.page(new Page<>(request.getPageNum(), request.getPageSize()), wrapper);
        return Result.ok(page);
    }

    @GetMapping("/list/recommend/{count}")
    @ApiOperation("推荐话题列表")
    public Result<List<Topic>> getRecommendTopicList(@PathVariable int count) {
        if (count <= 0 || count > 50) {
            count = 10;
        }
        List<Topic> list =  topicService.recommendTopicList(count);
        return Result.ok(list);
    }

    @GetMapping("/list/hot/{count}")
    @ApiOperation("热门话题列表")
    public Result<List<Topic>> getHotTopicList(@PathVariable int count) {
        if (count <= 0 || count > 100) {
            count = 20;
        }
        List<Topic> list =  topicService.hotTopicList(count);
        return Result.ok(list);
    }

    @GetMapping("/list/new/{count}")
    @ApiOperation("最新话题列表")
    public Result<List<Topic>> getNewTopicList(@PathVariable int count) {
        if (count <= 0 || count > 100) {
            count = 20;
        }
        List<Topic> list =  topicService.newTopicList(count);
        return Result.ok(list);
    }

    @GetMapping("/{topicId}")
    @ApiOperation("话题详细")
    @Cache
    public Result<Topic> getTopicById(@PathVariable Long topicId) {
        Topic topic = topicService.getById(topicId);
        return Result.ok(topic);
    }


    @PostMapping
    @ApiOperation("添加话题")
    @RequireAdmin
    public Result<Boolean> addTopic(@RequestBody TopicAddOrUpdateRequest request) {
        UserDto userDto = CurUserUtil.getCurUserDtoThrow();
        Topic topic = new Topic();
        topic.setName(request.getName());
        topic.setDescription(request.getDescription());
        topic.setCreatorId(userDto.getUserId());
        return Result.bool(topicService.save(topic));
    }

    @PutMapping
    @ApiOperation("更新话题")
    @RequireAdmin
    public Result<Boolean> updateTopic(@RequestBody TopicAddOrUpdateRequest request) {
        Topic topic = new Topic();
        topic.setId(request.getId());
        topic.setName(request.getName());
        topic.setDescription(request.getDescription());
        return Result.bool(topicService.updateById(topic));
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除话题")
    @RequireAdmin
    public Result<Boolean> deleteTopic(@PathVariable Long id) {
        return Result.bool(topicService.removeById(id));
    }

    // endregion



    private LambdaQueryWrapper<Topic> getQueryWrapper(TopicQueryRequest topicQueryRequest) {
        String name = topicQueryRequest.getName();
        Long creatorId = topicQueryRequest.getCreatorId();
        Integer postCount = topicQueryRequest.getPostCount();
        if (name == null && creatorId == null && postCount == null) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR, "请至少输入一个查询条件");
        }

        LambdaQueryWrapper<Topic> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, Topic::getName, name);
        wrapper.like(creatorId != null, Topic::getCreatorId, creatorId);
        wrapper.gt(postCount != null, Topic::getPostCount, postCount);
        return wrapper;
    }


}
