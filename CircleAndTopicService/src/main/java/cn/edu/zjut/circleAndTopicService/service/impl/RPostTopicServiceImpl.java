package cn.edu.zjut.circleAndTopicService.service.impl;

import cn.edu.zjut.circleAndTopicService.model.entity.Topic;
import cn.edu.zjut.circleAndTopicService.service.TopicService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.zjut.circleAndTopicService.model.entity.RPostTopic;
import cn.edu.zjut.circleAndTopicService.service.RPostTopicService;
import cn.edu.zjut.circleAndTopicService.mapper.RPostTopicMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author bert
* @description 针对表【r_post_topic(动态-话题关系表)】的数据库操作Service实现
* @createDate 2023-01-14 10:42:53
*/
@Service
public class RPostTopicServiceImpl extends ServiceImpl<RPostTopicMapper, RPostTopic>
    implements RPostTopicService{

    @Resource
    private TopicService topicService;

    @Override
    public List<Topic> getListByPostId(Long postId) {
        if (postId == null || postId < 0) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<RPostTopic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RPostTopic::getPostId, postId);
        return this.baseMapper.selectList(wrapper).stream()
                .map(rPostTopic -> topicService.getById(rPostTopic.getTopicId()))
                .collect(Collectors.toList());
    }
}




