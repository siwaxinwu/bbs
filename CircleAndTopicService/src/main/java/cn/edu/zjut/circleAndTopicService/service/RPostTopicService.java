package cn.edu.zjut.circleAndTopicService.service;

import cn.edu.zjut.circleAndTopicService.model.entity.RPostTopic;
import cn.edu.zjut.circleAndTopicService.model.entity.Topic;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author bert
* @description 针对表【r_post_topic(动态-话题关系表)】的数据库操作Service
* @createDate 2023-01-14 10:42:53
*/
public interface RPostTopicService extends IService<RPostTopic> {
    /**
     * 获取动态的话题列表
     * @param postId postId
     * @return List<Topic>
     */
    List<Topic> getListByPostId(Long postId);
}
