package com.ruoyi.jst.finance.service;

import com.ruoyi.jst.finance.dto.InvoiceTitleSaveReqDTO;
import com.ruoyi.jst.finance.vo.InvoiceTitleVO;

import java.util.List;

/**
 * 发票抬头领域服务。
 * <p>
 * 来源任务：BACKEND-UX-POLISH-TODO-ROUND-2.md · B3
 * 强约束：所有方法通过 userId 入参做归属校验，越权抛 JST_COMMON_AUTH_DENIED。
 *
 * @author jst
 * @since 1.0.0
 */
public interface InvoiceTitleService {

    /** 列出用户全部抬头（默认在前） */
    List<InvoiceTitleVO> listByUser(Long userId);

    /**
     * 新增或更新（titleId 为空则新增）。
     * <p>
     * company 类型必须提供 taxNo；更新时会校验 titleId 属于 userId。
     *
     * @return 操作后的 titleId
     */
    Long save(Long userId, InvoiceTitleSaveReqDTO req);

    /** 软删（归属校验） */
    void softDelete(Long userId, Long titleId);

    /**
     * 设为默认（事务内清旧 + 标新）。
     *
     * @关联锁 无（用户内串行，冲突极低，依赖事务隔离足够）
     */
    void setDefault(Long userId, Long titleId);
}
