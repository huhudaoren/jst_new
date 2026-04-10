package com.ruoyi.jst.message.mapper;

import com.ruoyi.jst.message.dto.WxMessageQueryDTO;
import com.ruoyi.jst.message.vo.WxMessageVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper for mini-program message center.
 */
public interface WxMessageMapper {

    List<WxMessageVO> selectMyMessageList(@Param("userId") Long userId, @Param("query") WxMessageQueryDTO query);

    int markMessageRead(@Param("messageId") Long messageId, @Param("userId") Long userId);

    int markAllRead(@Param("userId") Long userId);
}
