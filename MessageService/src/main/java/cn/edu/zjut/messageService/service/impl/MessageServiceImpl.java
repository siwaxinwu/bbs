package cn.edu.zjut.messageService.service.impl;

import cn.edu.zjut.common.dto.UserDto;
import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.redis.RedisConstants;
import cn.edu.zjut.common.utils.CurUserUtil;
import cn.edu.zjut.fileService.enums.ResourceStatusEnum;
import cn.edu.zjut.fileService.service.ResourceService;
import cn.edu.zjut.messageService.enums.MessageTypeEnum;
import cn.edu.zjut.messageService.model.dto.message.MessageAddRequest;
import cn.edu.zjut.messageService.model.vo.ChatItemVo;
import cn.edu.zjut.messageService.model.vo.MessageVo;
import cn.edu.zjut.common.constants.MqConstants;
import cn.edu.zjut.messageService.mq.SimpleMessage;
import cn.edu.zjut.userService.model.vo.UserSimpleVo;
import cn.edu.zjut.userService.service.BlockService;
import cn.edu.zjut.userService.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.zjut.messageService.model.entity.Message;
import cn.edu.zjut.messageService.service.MessageService;
import cn.edu.zjut.messageService.mapper.MessageMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
* @author bert
* @description 针对表【message(消息表)】的数据库操作Service实现
* @createDate 2023-02-24 09:18:14
*/
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
    implements MessageService{

    @Resource
    private UserService userService;
    @Resource
    private BlockService blockService;
    @Resource
    private ResourceService resourceService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public MessageVo setUserOfVo(MessageVo messageVo, Long userId) {
        UserSimpleVo userSimpleVo = userService.getUserSimpleVo(userId);
        messageVo.setUser(userSimpleVo);
        return messageVo;
    }

    @Override
    public MessageVo toMessageVo(Message message) {
        MessageVo messageVo = new MessageVo();
        BeanUtils.copyProperties(message, messageVo);
        setUserOfVo(messageVo, message.getSendUserId());
        // 若消息为图片，则获取相应的url
        if (message.getType().equals(MessageTypeEnum.IMAGE.getValue())) {
            String url = resourceService.getImgUrlByObjectId(message.getContent(),true);
            messageVo.setContent(url);
        }
        return messageVo;
    }

    @Override
    public MessageVo getVoById(Long messageId) {
        Message message = this.baseMapper.selectById(messageId);
        return toMessageVo(message);
    }

    @Override
    public MessageVo addMessage(MessageAddRequest request) {
        UserDto userDto = CurUserUtil.getCurUserDtoThrow();

        // 判断是否被拉黑
        boolean blocked = blockService.isBlocked(request.getReceiveUserId(),userDto.getUserId());
        if (blocked) {
            throw new BusinessException(CodeEnum.FAIL,"已被拉黑");
        }

        Message message = new Message();
        message.setSendUserId(userDto.getUserId());
        message.setReceiveUserId(request.getReceiveUserId());
        message.setType(request.getType());
        message.setContent(request.getContent());
        MessageTypeEnum type = MessageTypeEnum.transform(request.getType());
        switch (type) {
            case TEXT: {
                break;
            }
            case IMAGE: {
                // 图片发送时，将图片设置为审核通过
                resourceService.changeStatus(request.getContent(), ResourceStatusEnum.APPROVED);
                break;
            }
            default: break;
        }
        boolean save = this.save(message);
        if (save) {
            SimpleMessage simpleMessage = new SimpleMessage();
            simpleMessage.setType(SimpleMessage.Type.MESSAGE.getValue());
            simpleMessage.setId(message.getId());
            rabbitTemplate.convertAndSend(MqConstants.MESSAGE_ADD_QUEUE,simpleMessage);
            return this.getVoById(message.getId());
        }
        throw new BusinessException(CodeEnum.FAIL,"发送失败");
    }

    @Override
    public List<ChatItemVo> getChatItemList() {
        UserDto userDto = CurUserUtil.getCurUserDto();
        if (userDto == null) {
            return Collections.emptyList();
        }
        String keyPer = "user:"+userDto.getUserId()+":unReadMessage:";
        Set<String> keys = stringRedisTemplate.keys(keyPer+"*");
        if (keys == null) {
            return Collections.emptyList();
        }
        ArrayList<ChatItemVo> list = new ArrayList<>(keys.size());
        for (String key : keys) {
            String unReadCount = (String) stringRedisTemplate.opsForHash().get(key, "unReadCount");
            String lastMessageId = (String) stringRedisTemplate.opsForHash().get(key, "lastMessageId");
            if (unReadCount == null || lastMessageId == null) {
                continue;
            }
            ChatItemVo chatItemVo = new ChatItemVo();
            Message message = getById(Long.parseLong(lastMessageId));
            MessageVo messageVo = new MessageVo();
            BeanUtils.copyProperties(message,messageVo);
            if (messageVo.getType().equals(MessageTypeEnum.IMAGE.getValue())) {
                messageVo.setContent("[图片]");
            }
            // 显示对方用户信息
            Long userId = message.getSendUserId();
            if (userId.equals(userDto.getUserId())) {
                userId = message.getReceiveUserId();
            }
            UserSimpleVo userSimpleVo = userService.getUserSimpleVo(userId);
            chatItemVo.setUser(userSimpleVo);
            chatItemVo.setLastMessage(messageVo);
            chatItemVo.setUnReadCount(Integer.parseInt(unReadCount));
            list.add(chatItemVo);
        }
        list.sort((o1, o2) -> Math.toIntExact(o2.getLastMessage().getCreatedTime().getTime() - o1.getLastMessage().getCreatedTime().getTime()));
        return list;
    }

    @Override
    public Boolean readAll(Long userId) {
        UserDto userDto = CurUserUtil.getCurUserDtoThrow();
        String key = RedisConstants.getPersonMessageKey(userDto.getUserId(), userId);
        stringRedisTemplate.opsForHash().put(key,"unReadCount","0");
        return true;
    }

    @Override
    public Boolean deleteChatItem(Long userId) {
        UserDto userDto = CurUserUtil.getCurUserDto();
        if (userDto == null) {
            return false;
        }
        String key = RedisConstants.getPersonMessageKey(userDto.getUserId(), userId);
        return stringRedisTemplate.delete(key);
    }
}




