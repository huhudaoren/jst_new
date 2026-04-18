package com.ruoyi.jst.channel.service;

import com.ruoyi.jst.channel.domain.JstSalesChannelBinding;

import java.util.Date;
import java.util.List;

public interface SalesChannelBindingService {

    /** 绑定（版本化：当前 binding effective_to=now，新插一行） */
    JstSalesChannelBinding bindOrTransfer(Long channelId, Long newSalesId, String bindSource, String remark, Long operatorId);

    /** 查渠道当前归属销售 */
    JstSalesChannelBinding getCurrentByChannelId(Long channelId);

    /** 查某时刻该渠道归属（提成计算用） */
    JstSalesChannelBinding getBindingAtTime(Long channelId, Date atTime);

    /** 销售名下当前所有渠道（离职转移用） */
    List<JstSalesChannelBinding> listCurrentByOwnerSales(Long salesId);

    /** 离职批量转移给主管，返回转移数量 */
    int transferAllToManager(Long fromSalesId, Long managerId, Long operatorId);
}
