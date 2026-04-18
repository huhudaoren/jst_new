package com.ruoyi.jst.channel.service;

/**
 * 销售离职 3 阶段编排 (spec §4.5)。
 * <p>
 * 阶段 1 (resign_apply): admin 提交离职申请，销售账号保持登录但限权（拦截器控制）。
 *   → 由 AdminSalesController.applyResign + SalesService.applyResign 驱动。
 * 阶段 2 (resigned_pending_settle): 实际离职日执行，账号停权 + 名下渠道转主管 + 预录入失效。
 *   → 由 executeResign 编排（本 Service），Quartz ResignExecuteTask 到期自动触发。
 * 阶段 3 (resigned_final): 财务过渡期结束，sys_user 可归档。
 *   → 由 endTransition 编排（本 Service），Quartz TransitionEndTask 到期自动触发。
 *
 * @author jst
 * @since 1.0.0
 */
public interface SalesResignationService {

    /** 执行离职（阶段 2）：状态机 + 转移 binding + 失效 pre_register */
    void executeResign(Long salesId);

    /** 过渡期结束（阶段 3）：status = resigned_final */
    void endTransition(Long salesId);

    /** Quartz 用：批量执行到期的 resign_apply，返回处理数量 */
    int batchExecuteExpectedResigns();

    /** Quartz 用：批量结束到期的过渡期，返回处理数量 */
    int batchEndExpiredTransitions();
}
