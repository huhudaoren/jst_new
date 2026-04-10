package com.ruoyi.jst.message.service.impl;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.support.SchemaInspector;
import com.ruoyi.jst.message.dto.WxMessageQueryDTO;
import com.ruoyi.jst.message.mapper.WxMessageMapper;
import com.ruoyi.jst.message.service.WxMessageService;
import com.ruoyi.jst.message.vo.WxMessageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * Mini-program message service implementation.
 */
@Service
public class WxMessageServiceImpl implements WxMessageService {

    private static final String MESSAGE_TABLE = "jst_message";

    @Autowired
    private WxMessageMapper wxMessageMapper;

    @Autowired
    private SchemaInspector schemaInspector;

    @Override
    public List<WxMessageVO> selectMyMessages(Long userId, WxMessageQueryDTO query) {
        if (userId == null || !canUseMessageTable()) {
            return Collections.emptyList();
        }
        normalizeQuery(query);
        List<WxMessageVO> list = wxMessageMapper.selectMyMessageList(userId, query);
        return list == null ? Collections.emptyList() : list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markMessageRead(Long userId, Long messageId) {
        if (userId == null || messageId == null || !canUseMessageTable()) {
            return;
        }
        wxMessageMapper.markMessageRead(messageId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllRead(Long userId) {
        if (userId == null || !canUseMessageTable()) {
            return;
        }
        wxMessageMapper.markAllRead(userId);
    }

    private void normalizeQuery(WxMessageQueryDTO query) {
        if (query == null) {
            return;
        }
        query.setType(StringUtils.trimToNull(query.getType()));
    }

    private boolean canUseMessageTable() {
        return schemaInspector.hasColumns(MESSAGE_TABLE,
                "message_id", "user_id", "type", "title", "content",
                "read_status", "biz_type", "biz_id", "create_time", "del_flag");
    }
}
