package com.ruoyi.jst.user.login;

import com.ruoyi.jst.common.context.JstLoginContext;
import com.ruoyi.jst.common.context.JstLoginEnricher;
import com.ruoyi.jst.user.mapper.JstChannelMapper;
import com.ruoyi.jst.user.mapper.JstUserMapper;
import com.ruoyi.jst.user.mapper.PartnerLookupMapper;
import com.ruoyi.jst.user.mapper.SalesLookupMapper;
import com.ruoyi.jst.user.domain.JstChannel;
import com.ruoyi.jst.user.domain.JstUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 登录上下文充实器实现
 * <p>
 * 根据 userId 查找:
 * 1. jst_channel.user_id = userId → 设置 channelId
 * 2. jst_event_partner.user_id = userId → 设置 partnerId (TODO 待 partner 表 user_id 字段上线)
 * 3. jst_user.user_type → 设置 userType
 * <p>
 * 性能：每个请求 1-2 次 DB 查询(走索引,< 1ms)。
 * 后续可加 Caffeine/Redis 缓存,key=user:enrich:{userId} TTL 5min。
 *
 * @author jst
 * @since 1.0.0
 */
@Component
public class JstLoginEnricherImpl implements JstLoginEnricher {

    @Autowired
    private JstChannelMapper jstChannelMapper;

    @Autowired
    private JstUserMapper jstUserMapper;

    @Autowired
    private PartnerLookupMapper partnerLookupMapper;

    @Autowired
    private SalesLookupMapper salesLookupMapper;

    @Override
    public void enrich(Long userId, JstLoginContext ctx) {
        if (userId == null) return;

        // 1. 查 jst_user 拿 user_type
        JstUser u = jstUserMapper.selectJstUserByUserId(userId);
        if (u != null) {
            ctx.setUserType(u.getUserType());
        }

        // 2. 查 jst_channel 拿 channelId
        JstChannel channelQuery = new JstChannel();
        channelQuery.setUserId(userId);
        java.util.List<JstChannel> channels = jstChannelMapper.selectJstChannelList(channelQuery);
        if (channels != null && !channels.isEmpty()) {
            ctx.setChannelId(channels.get(0).getChannelId());
        }

        // 3. 查 jst_event_partner 拿 partnerId (通过 PartnerLookupMapper 跨域 SQL,无需依赖 jst-event)
        Long partnerId = partnerLookupMapper.selectPartnerIdByUserId(userId);
        ctx.setPartnerId(partnerId);

        // 4. 查 jst_sales 拿 salesId（销售/销售主管角色）
        Long salesId = salesLookupMapper.selectSalesIdByUserId(userId);
        ctx.setSalesId(salesId);
    }
}
