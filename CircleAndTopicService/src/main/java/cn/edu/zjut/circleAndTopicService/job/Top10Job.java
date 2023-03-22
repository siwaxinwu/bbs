package cn.edu.zjut.circleAndTopicService.job;

import cn.edu.zjut.circleAndTopicService.model.entity.Post;
import cn.edu.zjut.circleAndTopicService.service.PostService;
import cn.edu.zjut.common.redis.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bert
 * @date 2023/3/4 9:05
 */
@Component
public class Top10Job {

    @Resource
    private PostService postService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Scheduled(cron = "0 0/30 * * * ?")
    public void refreshTop10() {
        List<Post> postList = getTop10ofDaysAgo(7);
        List<Post> top10 = calculateTop10(postList);
        // 保存到redis
        cacheTop10(top10);
    }


    private void cacheTop10(List<Post> list) {
        stringRedisTemplate.delete(RedisConstants.TOP10);
//        System.out.println("分数： ");
        for (Post post : list) {
//            System.out.print(post.getContent().substring(0,8)+":"+getScore(post) + "  ");
            stringRedisTemplate.opsForList().rightPush(RedisConstants.TOP10,post.getId().toString());
        }
//        System.out.println();
    }

    private List<Post> getTop10ofDaysAgo(int day) {
        long currentTimeMillis = System.currentTimeMillis();
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(currentTimeMillis - (long) day *24*60*60*1000);
        // 获取day天前的最新100条记录
        List<Post> list = postService.lambdaQuery().ge(Post::getCreatedTime, instance.getTime())
                .orderByDesc(Post::getCreatedTime)
                .last("limit 100").list();
        if (list.size() < 10) {
            // 如果day天前10条都不够，则获取所有记录的前100条
            list = postService.lambdaQuery().orderByDesc(Post::getCreatedTime).last("limit 100").list();
        }
        return calculateTop10(list);
    }

    private List<Post> calculateTop10(List<Post> list) {
        return list.stream()
                .sorted((o1, o2) -> (int) (getScore(o2)*100 - getScore(o1)*100))
                .limit(10).collect(Collectors.toList());
    }

    private Double getScore(Post post) {
        long millis = System.currentTimeMillis();
        long gap = millis - post.getCreatedTime().getTime();
        long hour = 1000*60*60;
        long gapHour = gap / hour;
        // 阅读量*1+评论数*3+点赞量*3
        double basicScore = post.getReadCount()*0.1 + post.getCommentCount()*3 + post.getThumbCount()*3;
        // 7天外
        if (gapHour > 24*7) {
            basicScore += 0;
        }
        // 3天外
        if (gapHour > 72) {
            basicScore += 10;
        }
        // 两天外
        else if ( gapHour > 48 ) {
            basicScore += 10*3;
        }
        // 一天外
        else if ( gapHour > 24 ) {
            basicScore += 30*3;
        }
        // 一天内
        else  {
            basicScore += 50*3;
        }
        return Math.log10(Math.max(basicScore,1))*post.getWeight();
    }

}
