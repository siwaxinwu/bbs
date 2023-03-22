package cn.edu.zjut.circleAndTopicService.service;

import cn.edu.zjut.circleAndTopicService.model.entity.Topic;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author bert
* @description 针对表【topic(话题表)】的数据库操作Service
* @createDate 2023-01-13 23:38:20
*/
public interface TopicService extends IService<Topic> {

    /**
     * 获得count个热门话题
     */
    List<Topic> hotTopicList(int count);

    /**
     * 获得最新的count个话题
     */
    List<Topic> newTopicList(int count);

    /**
     * 增加动态数
     * @param id id
     * @param count 可以为负数
     */
    boolean increaseCount(Long id, int count);

    List<Topic> recommendTopicList(int count);

}
