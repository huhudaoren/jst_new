package com.ruoyi.jst.channel.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.channel.domain.JstSales;
import com.ruoyi.jst.channel.mapper.JstSalesMapper;
import com.ruoyi.jst.channel.service.SalesChannelBindingService;
import com.ruoyi.jst.channel.service.SalesPreRegisterService;
import com.ruoyi.jst.channel.service.SalesResignationService;
import com.ruoyi.jst.channel.service.SalesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 销售离职 3 阶段编排实现 (spec §4.5)。
 * <p>
 * executeResign 顺序：
 * 1. salesService.executeResign(salesId) — 状态机：active/resign_apply → resigned_pending_settle
 * 2. bindingService.transferAllToManager(salesId, managerId, operatorId) — 名下渠道转主管
 * 3. preRegisterService.expireBySales(salesId) — 失效所有 pending 预录入
 * 4. 禁用 sys_user — 实际离职日后账号无法登录（AC1/AC3 反带客户，阶段 2 停权）
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class SalesResignationServiceImpl implements SalesResignationService {

    private static final Logger log = LoggerFactory.getLogger(SalesResignationServiceImpl.class);

    @Autowired private JstSalesMapper salesMapper;
    @Autowired private SalesService salesService;
    @Autowired private SalesChannelBindingService bindingService;
    @Autowired private SalesPreRegisterService preRegisterService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeResign(Long salesId) {
        JstSales s = salesMapper.selectJstSalesBySalesId(salesId);
        if (s == null) throw new ServiceException("销售不存在: " + salesId);

        // 1. 状态机推进（active/resign_apply → resigned_pending_settle）
        salesService.executeResign(salesId);

        // 2. 名下渠道批量转主管
        Long managerId = s.getManagerId();
        int transferred = bindingService.transferAllToManager(salesId, managerId, null);
        log.info("[Resign] sales={} 转移 {} 个渠道给主管 {}", salesId, transferred, managerId);

        // 3. 失效 pending 预录入
        int expired = preRegisterService.expireBySales(salesId);
        log.info("[Resign] sales={} 失效 {} 条 pending 预录入", salesId, expired);

        // 4. sys_user 禁用 TODO: 跨模块 (jst-channel 不能直接依赖 ruoyi-system)
        // 临时方案：admin 在阶段 2 触发后手工在系统用户管理里禁用对应 sys_user。
        // 长期：发 UserDisableEvent，ruoyi-admin 中的 listener 调 ISysUserService.updateUserStatus
        if (s.getSysUserId() != null) {
            log.info("[Resign] sales={} sysUser={} 需要 admin 手工禁用（跨模块暂未自动）",
                    salesId, s.getSysUserId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void endTransition(Long salesId) {
        salesService.endTransition(salesId);
    }

    @Override
    public int batchExecuteExpectedResigns() {
        List<Long> ids = salesMapper.selectExpectedToResign();
        if (ids == null || ids.isEmpty()) return 0;
        int ok = 0;
        for (Long id : ids) {
            try {
                executeResign(id);
                ok++;
            } catch (Exception ex) {
                log.error("[Resign] sales={} 批量执行失败", id, ex);
            }
        }
        return ok;
    }

    @Override
    public int batchEndExpiredTransitions() {
        List<Long> ids = salesMapper.selectTransitionExpired();
        if (ids == null || ids.isEmpty()) return 0;
        int ok = 0;
        for (Long id : ids) {
            try {
                endTransition(id);
                ok++;
            } catch (Exception ex) {
                log.error("[Resign] sales={} 终结过渡期失败", id, ex);
            }
        }
        return ok;
    }
}
