package cn.edu.zjut.messageService.service;

import cn.edu.zjut.messageService.model.dto.notification.NotificationAddRequest;
import cn.edu.zjut.messageService.model.entity.Notification;
import cn.edu.zjut.messageService.model.vo.NotificationItemVo;
import cn.edu.zjut.messageService.model.vo.NotificationVo;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author bert
* @description 针对表【notification(通知表)】的数据库操作Service
* @createDate 2023-02-24 09:18:14
*/
public interface NotificationService extends IService<Notification> {

    Page<NotificationVo> voPage(int pageNum, int pageSize, Wrapper<Notification> wrapper);

    NotificationVo toVo(Notification notification, boolean isSetUserOfVo);
    default NotificationVo toVo(Notification notification) {
        return this.toVo(notification,true);
    }

    boolean add(NotificationAddRequest request);

    NotificationItemVo interactNotificationItem();

    NotificationItemVo systemNotificationItem();

    Boolean readAllOfSystem();

    Boolean readAllOfInteract();
}
