package cn.edu.zjut.userService.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.zjut.userService.model.entity.Level;
import cn.edu.zjut.userService.service.LevelService;
import cn.edu.zjut.userService.mapper.LevelMapper;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author bert
* @description 针对表【level(等级表)】的数据库操作Service实现
* @createDate 2023-01-12 12:22:45
*/
@Service
public class LevelServiceImpl extends ServiceImpl<LevelMapper, Level>
    implements LevelService{

    private final String DEFAULT_LEVEL_NAME = "Lv0";

    @Override
    public String getLevelNameByCount(long count) {
        List<Level> levels = this.baseMapper.selectList(null);
        // 大->小排序
        List<Level> collect = levels.stream().sorted((o1, o2) -> o2.getCount() - o1.getCount()).collect(Collectors.toList());
        for (Level level : collect) {
            if (level.getCount() < count) {
                return level.getName();
            }
        }
        return DEFAULT_LEVEL_NAME;
    }
}




