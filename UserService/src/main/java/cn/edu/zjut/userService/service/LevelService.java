package cn.edu.zjut.userService.service;

import cn.edu.zjut.userService.model.entity.Level;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author bert
* @description 针对表【level(等级表)】的数据库操作Service
* @createDate 2023-01-12 12:22:45
*/
public interface LevelService extends IService<Level> {

    /**
     * 通过经验数获取等级名
     * @param count 经验数
     * @return 等级名
     */
    String getLevelNameByCount(long count);

}
