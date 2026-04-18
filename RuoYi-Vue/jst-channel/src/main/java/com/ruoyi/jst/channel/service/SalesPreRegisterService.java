package com.ruoyi.jst.channel.service;

import com.ruoyi.jst.channel.domain.JstSalesPreRegister;

import java.util.List;

public interface SalesPreRegisterService {

    /** 销售新建预录入（A1/A2/A4/A5/A10 校验） */
    JstSalesPreRegister create(Long salesId, String phone, String targetName);

    /** 续期 +90 天（A7，最多 2 次） */
    void renew(Long preId, Long currentSalesId);

    /** 销售自己删除（A9，仅 pending） */
    void remove(Long preId, Long currentSalesId);

    /** 列表（销售视角） */
    List<JstSalesPreRegister> listMine(Long salesId);

    /** 取一行 pending（自动绑定 Step 1 用） */
    JstSalesPreRegister findActiveByPhone(String phone);

    /** 标记已匹配（自动绑定命中后调） */
    void markMatched(Long preId, Long channelId);

    /** Quartz 任务用：批量失效已过期 */
    int expirePendingByCron();

    /** 销售离职时调：失效该销售名下所有 pending 预录入 */
    int expireBySales(Long salesId);
}
