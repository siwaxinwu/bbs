package cn.edu.zjut.userService.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.zjut.userService.model.entity.CoinRecord;
import cn.edu.zjut.userService.service.CoinRecordService;
import cn.edu.zjut.userService.mapper.CoinRecordMapper;
import org.springframework.stereotype.Service;

/**
* @author bert
* @description 针对表【coin_record(积分记录表)】的数据库操作Service实现
* @createDate 2023-03-03 10:05:24
*/
@Service
public class CoinRecordServiceImpl extends ServiceImpl<CoinRecordMapper, CoinRecord>
    implements CoinRecordService{

}




