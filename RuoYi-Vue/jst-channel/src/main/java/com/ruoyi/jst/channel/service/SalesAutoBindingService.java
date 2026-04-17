package com.ruoyi.jst.channel.service;

public interface SalesAutoBindingService {
    /**
     * 渠道注册时触发自动绑定。算法详见 spec §2.1。
     * @param channelId           新注册的渠道 ID
     * @param registeredPhone     渠道方手机号（A2 主匹配键）
     * @param filledBusinessNo    渠道注册时填的销售业务编号（可空，B3 回退键）
     * @return 命中并绑定的 sales_id；未命中返 null
     */
    Long autoBind(Long channelId, String registeredPhone, String filledBusinessNo);
}
