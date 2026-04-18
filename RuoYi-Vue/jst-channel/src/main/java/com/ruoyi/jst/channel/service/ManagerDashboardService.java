package com.ruoyi.jst.channel.service;

import java.util.Map;

public interface ManagerDashboardService {
    /** 主管团队聚合。salesId=null 时 admin 看全量 */
    Map<String, Object> aggregate(Long managerSalesId, String month);
}
