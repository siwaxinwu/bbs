package cn.edu.zjut.circleAndTopicService.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.zjut.circleAndTopicService.model.entity.Topic;
import cn.edu.zjut.circleAndTopicService.service.TopicService;
import cn.edu.zjut.circleAndTopicService.mapper.TopicMapper;
import net.sf.jsqlparser.statement.select.Top;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author bert
* @description 针对表【topic(话题表)】的数据库操作Service实现
* @createDate 2023-01-13 23:38:20
*/
@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic>
    implements TopicService{

    @Override
    public List<Topic> hotTopicList(int count) {
        List<Topic> list = lambdaQuery().orderByDesc(Topic::getPostCount)
                .orderByDesc(Topic::getCreatedTime)
                .last("limit " + count).list();
        return list;
    }

    @Override
    public List<Topic> newTopicList(int count) {
        List<Topic> list = lambdaQuery().orderByDesc(Topic::getCreatedTime).last("limit " + count).list();
        return list;
    }

    @Override
    public boolean increaseCount(Long id, int count) {
        String setSql = "post_count = post_count+"+count;
        if (count < 0) {
            setSql = "post_count = post_count-"+count;
        }
        return lambdaUpdate().setSql(setSql).eq(Topic::getId,id).update();
    }

    @Override
    public List<Topic> recommendTopicList(int count) {
        List<Topic> topics = newTopicList(100);
        if (topics.size() == 0 || count == 0) {
            return Collections.emptyList();
        }
        return topics.stream().sorted((o1, o2) -> getScore(o2)-getScore(o1)).limit(count).collect(Collectors.toList());
    }

    private Integer getScore(Topic topic) {
        Integer basicScore = topic.getPostCount();
        long currentTimeMillis = System.currentTimeMillis();
        long time = topic.getCreatedTime().getTime();
        long gap = currentTimeMillis - time;
        int dayMillis = 1000*60*60*24;
        if (gap / dayMillis < 3) {
            basicScore += 200;
        }
        else if (gap / dayMillis < 7) {
            basicScore += 100;
        }
        else if (gap / dayMillis < 14) {
            basicScore += 40;
        }
        else {
            basicScore += 0;
        }
        return basicScore;
    }
}




