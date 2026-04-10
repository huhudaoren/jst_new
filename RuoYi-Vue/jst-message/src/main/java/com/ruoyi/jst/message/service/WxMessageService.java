package com.ruoyi.jst.message.service;

import com.ruoyi.jst.message.dto.WxMessageQueryDTO;
import com.ruoyi.jst.message.vo.WxMessageVO;

import java.util.List;

/**
 * Service for mini-program message center.
 */
public interface WxMessageService {

    List<WxMessageVO> selectMyMessages(Long userId, WxMessageQueryDTO query);

    void markMessageRead(Long userId, Long messageId);

    void markAllRead(Long userId);
}
