package cn.edu.zjut.messageService.model.vo;

import cn.edu.zjut.userService.model.vo.UserSimpleVo;
import lombok.Data;

/**
 * @author bert
 * @date 2023/2/25 18:24
 */
@Data
public class ResourceVo {
    private Long postId;
    private Long commentId;
    private Long parentCommentId;
    private Long userId;
    private Long postUserId;
}
