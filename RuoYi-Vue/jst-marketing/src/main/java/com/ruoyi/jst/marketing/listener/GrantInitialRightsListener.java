package com.ruoyi.jst.marketing.listener;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.jst.common.event.ChannelAuthApprovedEvent;
import com.ruoyi.jst.marketing.domain.JstRightsTemplate;
import com.ruoyi.jst.marketing.domain.JstUserRights;
import com.ruoyi.jst.marketing.mapper.JstUserRightsMapper;
import com.ruoyi.jst.marketing.mapper.RightsTemplateMapperExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 渠道认证通过时自动发放初始等级权益（Q-05）
 * <p>
 * 监听 {@link ChannelAuthApprovedEvent}，按等级配置自动发放基础权益到 jst_user_rights。
 * <p>
 * 当前实现：查询 jst_rights_template 中 applicable_role 包含 'channel' 且 auto_grant=1 的模板，
 * 为用户创建 jst_user_rights 记录。
 * <p>
 * TODO: 当 jst_level_rights_config 表完成后，改为按等级查配置发放。
 *
 * @author jst
 * @since 1.0.0
 */
@Component
public class GrantInitialRightsListener {

    private static final Logger log = LoggerFactory.getLogger(GrantInitialRightsListener.class);

    @Autowired
    private RightsTemplateMapperExt rightsTemplateMapperExt;

    @Autowired
    private JstUserRightsMapper jstUserRightsMapper;

    /**
     * 监听渠道认证通过事件，自动发放初始权益
     *
     * @param event 认证通过事件
     * @关联表 jst_rights_template / jst_user_rights
     * @关联决策 Q-05
     */
    @EventListener
    @Transactional(rollbackFor = Exception.class) // TX: grantInitialRights
    public void onChannelAuthApproved(ChannelAuthApprovedEvent event) {
        Long userId = event.getUserId();
        Long channelId = event.getChannelId();
        String channelType = event.getChannelType();

        log.info("[GrantInitialRights] 收到认证通过事件 userId={} channelId={} channelType={}",
                userId, channelId, channelType);

        // 查询可自动发放的权益模板
        // TODO: 当 jst_level_rights_config 完成后，按 user_level 查配置
        List<Map<String, Object>> templates = rightsTemplateMapperExt.selectAutoGrantTemplates("channel");
        if (templates == null || templates.isEmpty()) {
            log.info("[GrantInitialRights] 无可自动发放的权益模板 userId={}", userId);
            return;
        }

        Date now = DateUtils.getNowDate();
        int granted = 0;
        for (Map<String, Object> tpl : templates) {
            Long templateId = toLong(tpl.get("rightsTemplateId"));
            if (templateId == null) {
                continue;
            }

            JstUserRights rights = new JstUserRights();
            rights.setRightsTemplateId(templateId);
            rights.setOwnerType("channel");
            rights.setOwnerId(userId);
            rights.setSourceType("auth_grant");
            rights.setSourceRefId(channelId);
            rights.setRemainQuota(toBigDecimal(tpl.get("quotaValue")));
            rights.setStatus("available");
            rights.setValidStart(now);
            // 默认有效期 365 天
            rights.setValidEnd(DateUtils.addDays(now, 365));
            rights.setCreateBy("system");
            rights.setCreateTime(now);
            rights.setUpdateBy("system");
            rights.setUpdateTime(now);
            rights.setDelFlag("0");
            jstUserRightsMapper.insertJstUserRights(rights);
            granted++;
        }

        log.info("[GrantInitialRights] 发放完成 userId={} channelId={} granted={}", userId, channelId, granted);
    }

    private Long toLong(Object val) {
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).longValue();
        try { return Long.valueOf(String.valueOf(val)); }
        catch (NumberFormatException e) { return null; }
    }

    private BigDecimal toBigDecimal(Object val) {
        if (val == null) return BigDecimal.ONE;
        if (val instanceof BigDecimal) return (BigDecimal) val;
        try { return new BigDecimal(String.valueOf(val)); }
        catch (NumberFormatException e) { return BigDecimal.ONE; }
    }
}
