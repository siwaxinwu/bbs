package cn.edu.zjut.messageService.service;

import cn.edu.zjut.messageService.model.dto.message.MessageAddRequest;
import cn.edu.zjut.messageService.model.entity.Message;
import cn.edu.zjut.messageService.model.vo.ChatItemVo;
import cn.edu.zjut.messageService.model.vo.MessageVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author bert
* @description 针对表【message(消息表)】的数据库操作Service
* @createDate 2023-02-24 09:18:14
*/
public interface MessageService extends IService<Message> {

    /**
     * 为vo对象设置用户的vo
     */
    MessageVo setUserOfVo(MessageVo messageVo,Long userId);

    MessageVo toMessageVo(Message message);

    MessageVo getVoById(Long messageId);

    MessageVo addMessage(MessageAddRequest request);

    /**
     * 查询聊天对话列表
     */
    List<ChatItemVo> getChatItemList();

    Boolean readAll(Long userId);

    Boolean deleteChatItem(Long userId);
}
