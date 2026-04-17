package com.ruoyi.jst.channel.service.impl;

import com.ruoyi.jst.channel.domain.JstSales;
import com.ruoyi.jst.channel.domain.JstSalesChannelBinding;
import com.ruoyi.jst.channel.domain.JstSalesPreRegister;
import com.ruoyi.jst.channel.service.SalesAutoBindingService;
import com.ruoyi.jst.channel.service.SalesChannelBindingService;
import com.ruoyi.jst.channel.service.SalesPreRegisterService;
import com.ruoyi.jst.channel.service.SalesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * 销售自动绑定算法（spec §2.1）。
 * <p>
 * 边界覆盖：B1（pre 命中在职销售）/ B2（pre 命中已离职销售-跳过）/ B3（销售在 resign_apply 仍允许绑定）/
 * B4（business_no 命中）/ B5（business_no 离职跳过）/ B6（business_no 拼错-审计日志）/
 * B7（无归属）/ B8（自绑防御）/ B9（已绑短路）/ B10（DB 唯一索引保护）/
 * B11/B12（M1 互斥-由 ChannelInviteBindingListener 检查 hasSalesBinding）
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class SalesAutoBindingServiceImpl implements SalesAutoBindingService {

    private static final Logger log = LoggerFactory.getLogger(SalesAutoBindingServiceImpl.class);

    /** 在职状态白名单：active + resign_apply 都允许绑定（B3） */
    private static final Set<String> BINDABLE_STATUS = Set.of("active", "resign_apply");

    @Autowired
    private SalesPreRegisterService preRegisterService;

    @Autowired
    private SalesService salesService;

    @Autowired
    private SalesChannelBindingService bindingService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long autoBind(Long channelId, String registeredPhone, String filledBusinessNo) {
        if (channelId == null) {
            log.warn("[AutoBind] channelId 为空，跳过");
            return null;
        }

        // B9 短路：渠道已有 current binding → 不重绑
        JstSalesChannelBinding existing = bindingService.getCurrentByChannelId(channelId);
        if (existing != null) {
            log.info("[AutoBind] channel={} 已有 binding salesId={}, 跳过", channelId, existing.getSalesId());
            return existing.getSalesId();
        }

        // Step 1: 手机号优先 (A2)
        Long step1SalesId = tryStep1ByPhone(channelId, registeredPhone);
        if (step1SalesId != null) {
            log.info("[AutoBind] channel={} bound via pre_register sales={}", channelId, step1SalesId);
            return step1SalesId;
        }

        // Step 2: 业务编号回退 (B4)
        Long step2SalesId = tryStep2ByBusinessNo(channelId, registeredPhone, filledBusinessNo);
        if (step2SalesId != null) {
            log.info("[AutoBind] channel={} bound via business_no sales={}", channelId, step2SalesId);
            return step2SalesId;
        }

        // Step 3: 无归属
        log.info("[AutoBind] channel={} 无销售归属（pre 未命中 + business_no={} 未命中）",
                channelId, filledBusinessNo);
        return null;
    }

    private Long tryStep1ByPhone(Long channelId, String registeredPhone) {
        if (registeredPhone == null || registeredPhone.isEmpty()) return null;
        // Note: spec §2.1 Step 1 提到"命中多条 → 写 jst_sales_attribution_conflict 不绑"，
        // 但 jst_sales_pre_register 的 uk_phone_pending 唯一索引（DDL 中通过生成列模拟 partial unique）
        // 已在 DB 层保证：同 phone 只能有 1 条 status='pending' 行。
        // 因此 selectByPhonePending LIMIT 1 是安全简化，多命中场景不会发生，无需写 conflict 表。
        // 若未来取消唯一索引（如允许同手机号被多销售竞争预录），需补 conflict 写入逻辑。
        JstSalesPreRegister pre = preRegisterService.findActiveByPhone(registeredPhone);
        if (pre == null) return null;

        JstSales sales = salesService.getById(pre.getSalesId());
        if (sales == null) {
            log.warn("[AutoBind] preRegister preId={} sales 已不存在", pre.getPreId());
            return null;  // 跳过 Step 1，让 Step 2 接力
        }
        // B2: 已离职销售跳过（理论上 A8 已失效该 pre，但兜底）
        if (!BINDABLE_STATUS.contains(sales.getStatus())) {
            log.warn("[AutoBind] preRegister preId={} 销售 status={} 不可绑，跳过 Step 1",
                    pre.getPreId(), sales.getStatus());
            return null;
        }
        // B8: 自绑防御
        if (registeredPhone.equals(sales.getPhone())) {
            log.warn("[AutoBind] B8 self-bind 防御命中：sales={} phone={}", sales.getSalesId(), registeredPhone);
            return null;  // 不绑，让 Step 2 接力
        }

        bindingService.bindOrTransfer(channelId, sales.getSalesId(), "pre_register", null, null);
        preRegisterService.markMatched(pre.getPreId(), channelId);
        return sales.getSalesId();
    }

    private Long tryStep2ByBusinessNo(Long channelId, String registeredPhone, String filledBusinessNo) {
        if (filledBusinessNo == null || filledBusinessNo.isEmpty()) return null;

        JstSales sales = salesService.getBySalesNo(filledBusinessNo);
        if (sales == null) {
            // B6: business_no 填错
            log.info("[AutoBind] B6 business_no={} 填错，无对应销售（已审计）", filledBusinessNo);
            // 审计日志：用 SLF4J INFO 已足够（@Log 注解由 Controller 层负责，本服务仅日志）
            return null;
        }
        // B5: 已离职销售跳过
        if (!BINDABLE_STATUS.contains(sales.getStatus())) {
            log.info("[AutoBind] business_no={} 销售已离职 status={}，跳过 Step 2",
                    filledBusinessNo, sales.getStatus());
            return null;
        }
        // B8: 自绑防御
        if (registeredPhone != null && registeredPhone.equals(sales.getPhone())) {
            log.warn("[AutoBind] B8 self-bind 防御命中（Step 2）：sales={} phone={}",
                    sales.getSalesId(), registeredPhone);
            return null;
        }

        bindingService.bindOrTransfer(channelId, sales.getSalesId(), "business_no", null, null);
        return sales.getSalesId();
    }
}
