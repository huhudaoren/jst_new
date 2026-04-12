package com.ruoyi.web.service.jst;

import com.ruoyi.web.controller.jst.vo.ChannelRankVO;
import com.ruoyi.web.controller.jst.vo.ContestRankVO;
import com.ruoyi.web.controller.jst.vo.OverviewVO;
import com.ruoyi.web.controller.jst.vo.TodoVO;
import com.ruoyi.web.controller.jst.vo.TrendPointVO;

import java.util.List;

/**
 * 平台运营数据聚合 Service。
 *
 * @author jst
 * @since 1.0.0
 */
public interface AdminDashboardService {

    /**
     * 获取平台运营总览。
     *
     * @return 总览指标
     * @关联表 jst_order_main / jst_refund_record / jst_user / jst_contest / jst_enroll_record
     * @关联权限 jst:admin:dashboard
     */
    OverviewVO getOverview();

    /**
     * 获取平台运营趋势。
     *
     * @param days 近 N 天，仅支持 7/30
     * @return 趋势列表
     * @关联表 jst_order_main / jst_enroll_record
     * @关联权限 jst:admin:dashboard
     */
    List<TrendPointVO> getTrend(int days);

    /**
     * 获取赛事报名排行。
     *
     * @param limit 返回条数
     * @return 排行列表
     * @关联表 jst_contest / jst_enroll_record / jst_order_main
     * @关联权限 jst:admin:dashboard
     */
    List<ContestRankVO> getTopContests(int limit);

    /**
     * 获取渠道返点排行。
     *
     * @param limit 返回条数
     * @return 排行列表
     * @关联表 jst_channel / jst_rebate_ledger / jst_student_channel_binding
     * @关联权限 jst:admin:dashboard
     */
    List<ChannelRankVO> getTopChannels(int limit);

    /**
     * 获取平台待办计数。
     *
     * @return 待办计数
     * @关联表 jst_contest / jst_enroll_record / jst_refund_record / jst_rebate_settlement / jst_event_partner_apply / jst_channel_auth_apply
     * @关联权限 jst:admin:dashboard
     */
    TodoVO getTodo();
}
