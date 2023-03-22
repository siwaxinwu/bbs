package cn.edu.zjut.circleAndTopicService.service;

import cn.edu.zjut.circleAndTopicService.model.entity.Circle;
import cn.edu.zjut.circleAndTopicService.model.vo.CircleVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
* @author bert
* @description 针对表【circle(圈子表)】的数据库操作Service
* @createDate 2023-01-12 21:36:36
*/
public interface CircleService extends IService<Circle> {


    /**
     * 获取CircleVo
     * @param circleId circleId
     * @return CircleVo
     */
    CircleVo getVoById(Long circleId);


    /**
     * 获取uid用户加入的圈子
     * @param uid uid
     * @return List<Circle>
     */
    List<Circle> getJoinList(Long uid);


    /**
     * 获取uid用户加入的圈子
     * @param uid uid
     * @return List<Circle>
     */
    List<Circle> getCreateList(Long uid);

    /**
     * 获取圈子的加入用户id列表
     * @param circleId circleId
     * @return List<Long>
     */
    List<Long> getJoinUserIds(Long circleId);

    /**
     * 加入圈子
     * @param id id
     * @return Boolean
     */
    Boolean joinCircle(Long id);

    /**
     * 退出圈子
     * @param id id
     * @return Boolean
     */
    Boolean leaveCircle(Long id);

}
