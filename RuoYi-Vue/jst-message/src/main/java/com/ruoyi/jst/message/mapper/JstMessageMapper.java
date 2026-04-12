package com.ruoyi.jst.message.mapper;

import com.ruoyi.jst.message.domain.JstMessage;

/**
 * 站内消息 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface JstMessageMapper {

    /**
     * 新增站内消息。
     *
     * @param jstMessage 站内消息
     * @return 影响行数
     */
    int insertJstMessage(JstMessage jstMessage);
}

