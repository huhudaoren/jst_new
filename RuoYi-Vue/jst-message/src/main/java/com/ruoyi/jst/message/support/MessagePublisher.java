package com.ruoyi.jst.message.support;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.jst.common.support.SchemaInspector;
import com.ruoyi.jst.message.domain.JstMessage;
import com.ruoyi.jst.message.mapper.JstMessageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 站内消息写入发布器。
 * <p>
 * 约束：
 * 1. 写入失败必须降级，不阻塞主流程；
 * 2. jst_message 缺表或缺关键列时仅告警并跳过。
 *
 * @author jst
 * @since 1.0.0
 */
@Component
public class MessagePublisher {

    private static final Logger log = LoggerFactory.getLogger(MessagePublisher.class);

    private static final String MESSAGE_TABLE = "jst_message";
    private static final String[] REQUIRED_COLUMNS = new String[]{
            "message_id", "user_id", "type", "title", "content",
            "read_status", "biz_type", "biz_id", "create_time", "del_flag"
    };

    private final JstMessageMapper messageMapper;
    private final SchemaInspector schemaInspector;
    private final AtomicBoolean missingSchemaLogged = new AtomicBoolean(false);

    public MessagePublisher(JstMessageMapper messageMapper, SchemaInspector schemaInspector) {
        this.messageMapper = messageMapper;
        this.schemaInspector = schemaInspector;
    }

    /**
     * 发送业务消息。
     *
     * @param userId  接收用户ID
     * @param type    消息类型（system/business）
     * @param bizType 业务类型（order_paid/refund_success 等）
     * @param bizId   业务ID
     * @param title   消息标题
     * @param content 消息内容
     */
    public void send(Long userId, String type, String bizType, Long bizId, String title, String content) {
        if (userId == null || !StringUtils.hasText(type) || !StringUtils.hasText(title) || !StringUtils.hasText(content)) {
            return;
        }
        if (!canUseMessageTable()) {
            if (missingSchemaLogged.compareAndSet(false, true)) {
                log.warn("[MessagePublisher] skip send because jst_message table/columns missing");
            }
            return;
        }

        try {
            JstMessage message = new JstMessage();
            message.setUserId(userId);
            message.setType(type);
            message.setBizType(bizType);
            message.setBizId(bizId);
            message.setTitle(title);
            message.setContent(content);
            message.setReadStatus(0);
            message.setCreateBy(String.valueOf(userId));
            message.setCreateTime(DateUtils.getNowDate());
            message.setUpdateBy(String.valueOf(userId));
            message.setUpdateTime(DateUtils.getNowDate());
            message.setDelFlag("0");
            messageMapper.insertJstMessage(message);
        } catch (Exception ex) {
            // 消息系统降级：不影响主事务。
            log.warn("[MessagePublisher] send failed userId={} bizType={} bizId={}", userId, bizType, bizId, ex);
        }
    }

    private boolean canUseMessageTable() {
        return schemaInspector.hasColumns(MESSAGE_TABLE, REQUIRED_COLUMNS);
    }
}

