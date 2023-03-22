package cn.edu.zjut.circleAndTopicService.service;

import cn.edu.zjut.circleAndTopicService.model.dto.post.PostAddRequest;
import cn.edu.zjut.circleAndTopicService.model.dto.post.PostQueryByTopicRequest;
import cn.edu.zjut.circleAndTopicService.model.dto.post.PostQueryRequest;
import cn.edu.zjut.circleAndTopicService.model.dto.post.PostUpdateRequest;
import cn.edu.zjut.circleAndTopicService.model.entity.Post;
import cn.edu.zjut.circleAndTopicService.model.vo.PostVo;
import cn.edu.zjut.common.model.PageRequest;
import cn.edu.zjut.common.model.ScoreRequest;
import cn.edu.zjut.common.model.ScrollResult;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
* @author bert
* @description 针对表【post(动态表)】的数据库操作Service
* @createDate 2023-01-13 23:38:20
*/
public interface PostService extends IService<Post> {


    /**
     * 发布动态
     * @param page postAddRequest
     * @param postQueryRequest postQueryRequest
     * @return Page<PostVo>
     */
    Page<PostVo> page(Page<Post> page, PostQueryRequest postQueryRequest);

    PostVo getVoById(Long postId);

    /**
     * 发布动态
     * @param postAddRequest postAddRequest
     * @return boolean
     */
    boolean save(PostAddRequest postAddRequest);

    /**
     * 更新动态
     * @param postUpdateRequest postUpdateRequest
     * @return boolean
     */
    boolean update(PostUpdateRequest postUpdateRequest);

    /**
     * 删除动态
     * @param id id
     * @return boolean
     */
    boolean removeById(Long id);

    /**
     * 获取某个用户的动态
     * @param postQueryRequest postQueryRequest
     * @return Page<Post>
     */
    Page<PostVo> getPageOfUser(PostQueryRequest postQueryRequest);

    /**
     * 获取加入的圈子动态
     * @param max max
     * @param offset offset
     * @param count count
     * @return ScrollResult<PostVo>
     */
    ScrollResult<PostVo> getCirclePostFeed(long max, int offset, int count);

    /**
     * 获取关注用户的动态
     * @param max max
     * @param offset offset
     * @param count count
     * @return ScrollResult<PostVo>
     */
    ScrollResult<PostVo> getFollowUserPostFeed(long max, int offset, int count);

    List<PostVo> getTop10();

    Page<PostVo> getPostOfTopic(PostQueryByTopicRequest request);

    List<PostVo> getPostOfLiked(ScoreRequest request);

    // region 数据

    /**
     * 点赞
     * @param postId postId
     * @return true成功 false失败
     */
    Boolean thumb(Long postId);

    /**
     * 取消点赞
     * @param postId postId
     * @return true成功 false失败
     */
    Boolean cancelThumb(Long postId);

    /**
     * 关注用户的未读消息数
     * @return 消息数
     */
    Integer unReadCountOfFollowPerson();

    Map<String, Integer> unReadCountOfJoinCircle();

    Boolean readAllOfCircle(Long circleId);

    Boolean readAllOfFollowPerson();

    Boolean addReadCount(Long postId);

    // endregion 数据
}
