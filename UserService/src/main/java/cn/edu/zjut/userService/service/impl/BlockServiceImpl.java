package cn.edu.zjut.userService.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.zjut.userService.model.entity.Block;
import cn.edu.zjut.userService.service.BlockService;
import cn.edu.zjut.userService.mapper.BlockMapper;
import org.springframework.stereotype.Service;

/**
* @author bert
* @description 针对表【block(拉黑表)】的数据库操作Service实现
* @createDate 2023-02-27 15:33:50
*/
@Service
public class BlockServiceImpl extends ServiceImpl<BlockMapper, Block>
    implements BlockService{

    @Override
    public boolean isBlocked(Long userId, Long targetUserId) {
        Block one = lambdaQuery().eq(Block::getUserId, userId).eq(Block::getTargetUserId,targetUserId).one();
        return one != null;
    }
}




