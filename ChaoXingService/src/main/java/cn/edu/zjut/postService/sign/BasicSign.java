package cn.edu.zjut.postService.sign;

import cn.edu.zjut.postService.dto.SignRequest;

/**
 * @author bert
 */
public interface BasicSign {

    /**
     * 签到前置动作
     */
    default void preSign() {}

    /**
     * 签到
     * @return 是否成功
     */
    boolean doSign(SignRequest request);

    /**
     * 签到后置动作
     */
    default void afterSign() {}

    /**
     * 判断是否支持该typeCode
     */
    boolean isSupport(Integer typeCode);
}
