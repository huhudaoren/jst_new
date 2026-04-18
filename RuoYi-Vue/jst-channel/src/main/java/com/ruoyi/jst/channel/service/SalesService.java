package com.ruoyi.jst.channel.service;

import com.ruoyi.jst.channel.domain.JstSales;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface SalesService {

    JstSales getById(Long salesId);
    JstSales getBySysUserId(Long sysUserId);
    JstSales getBySalesNo(String salesNo);
    List<JstSales> listSubordinates(Long managerId);

    /** 主管的可见销售 ID 列表（含自己）；普通销售返自己；admin 返 null */
    List<Long> resolveScopeSalesIds(Long currentSalesId);

    /** 创建销售（必须先有 sys_user，自动生成 sales_no） */
    JstSales create(JstSales row);
    /** 修改默认费率（仅 admin 调） */
    void updateDefaultRate(Long salesId, BigDecimal rate);
    /** 设置主管 */
    void updateManager(Long salesId, Long managerId);

    /** 状态推进 */
    void applyResign(Long salesId, Date expectedResignDate);
    void executeResign(Long salesId);
    void endTransition(Long salesId);

    /** Admin 全量查询（用 JstSales 作查询参数，走 Mapper 的 selectJstSalesList） */
    List<JstSales> listForAdmin(JstSales query);
}
