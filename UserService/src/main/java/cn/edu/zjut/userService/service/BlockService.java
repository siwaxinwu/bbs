package cn.edu.zjut.userService.service;

import cn.edu.zjut.userService.model.entity.Block;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author bert
* @description 针对表【block(拉黑表)】的数据库操作Service
* @createDate 2023-02-27 15:33:50
*/
public interface BlockService extends IService<Block> {

    boolean isBlocked(Long userId, Long targetUserId);

}
