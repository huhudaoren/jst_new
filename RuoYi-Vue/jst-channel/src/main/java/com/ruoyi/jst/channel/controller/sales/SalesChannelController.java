package com.ruoyi.jst.channel.controller.sales;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.channel.domain.JstSalesChannelBinding;
import com.ruoyi.jst.channel.mapper.JstSalesChannelBindingMapper;
import com.ruoyi.jst.channel.mapper.JstSalesCommissionLedgerMapper;
import com.ruoyi.jst.channel.mapper.JstSalesFollowupRecordMapper;
import com.ruoyi.jst.channel.mapper.lookup.ChannelLookupMapper;
import com.ruoyi.jst.common.util.SalesScopeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 销售工作台 — 我的渠道管理 Controller（AC1 反带客户加固）
 * <p>
 * 路径前缀：/sales/me/channels
 * 角色：jst_sales / jst_sales_manager / admin / jst_operator
 * <p>
 * 端点：
 * <ul>
 *   <li>GET  /list          — 当前销售名下渠道列表（binding + 渠道基础信息）</li>
 *   <li>GET  /{id}          — 渠道详情（手机号自动脱敏）</li>
 *   <li>GET  /{id}/phone-full — 完整手机号 + 写 SALES_VIEW_PHONE 审计日志</li>
 * </ul>
 *
 * @author jst
 * @since 1.0.0
 */
@RestController
@RequestMapping("/sales/me/channels")
@PreAuthorize("@ss.hasAnyRoles('jst_sales,jst_sales_manager,admin,jst_operator')")
public class SalesChannelController extends BaseController {

    @Autowired
    private JstSalesChannelBindingMapper bindingMapper;

    @Autowired
    private ChannelLookupMapper channelLookupMapper;

    @Autowired
    private JstSalesFollowupRecordMapper followupRecordMapper;

    @Autowired
    private JstSalesCommissionLedgerMapper commissionLedgerMapper;

    /**
     * 当前销售名下渠道列表（含基础信息，手机号脱敏）。
     * admin / jst_operator 时 salesId 为 null，查全部 binding。
     */
    @GetMapping("/list")
    public TableDataInfo list() {
        Long salesId = SalesScopeUtils.currentSalesIdOrAllowAdminView();
        startPage();
        List<JstSalesChannelBinding> bindings;
        if (salesId == null) {
            bindings = bindingMapper.selectAllCurrent(1000);
        } else {
            bindings = bindingMapper.selectCurrentByOwnerSales(salesId);
        }
        List<Map<String, Object>> result = buildChannelListWithMask(bindings, true);
        return getDataTable(result);
    }

    /**
     * 渠道详情（手机号自动脱敏）。admin 不校验归属。
     *
     * @param id 渠道 ID
     */
    @GetMapping("/{id}")
    public AjaxResult detail(@PathVariable Long id) {
        Long salesId = SalesScopeUtils.currentSalesIdOrAllowAdminView();
        if (salesId != null) {
            // 普通销售：校验归属
            JstSalesChannelBinding binding = bindingMapper.selectCurrentByChannelId(id);
            if (binding == null || !salesId.equals(binding.getSalesId())) {
                throw new ServiceException("渠道不属于当前销售");
            }
        }
        Map<String, Object> channelMap = channelLookupMapper.selectCurrentByUserId(
                queryUserIdByChannelId(id));
        if (channelMap == null) channelMap = new HashMap<>();
        channelMap.put("channelId", id);
        maskMobile(channelMap, true);
        return AjaxResult.success(channelMap);
    }

    /**
     * 查看完整手机号（写审计日志）。admin 不校验归属，仍写日志。
     *
     * @param id 渠道 ID
     */
    @GetMapping("/{id}/phone-full")
    @Log(title = "销售查看渠道手机号", businessType = BusinessType.OTHER)
    public AjaxResult phoneFull(@PathVariable Long id) {
        Long salesId = SalesScopeUtils.currentSalesIdOrAllowAdminView();
        if (salesId != null) {
            JstSalesChannelBinding binding = bindingMapper.selectCurrentByChannelId(id);
            if (binding == null || !salesId.equals(binding.getSalesId())) {
                throw new ServiceException("渠道不属于当前销售");
            }
        }
        Map<String, Object> channelMap = channelLookupMapper.selectCurrentByUserId(
                queryUserIdByChannelId(id));
        if (channelMap == null) channelMap = new HashMap<>();
        channelMap.put("channelId", id);
        // phone-full 接口不脱敏
        return AjaxResult.success(channelMap);
    }

    /**
     * 渠道画像：跟进次数 / 成交笔数 / 业务类型 / 近 6 月活跃趋势。
     *
     * @param id 渠道 ID
     */
    @GetMapping("/{id}/profile")
    public AjaxResult channelProfile(@PathVariable Long id) {
        Long salesId = SalesScopeUtils.currentSalesIdOrAllowAdminView();
        if (salesId != null) {
            JstSalesChannelBinding binding = bindingMapper.selectCurrentByChannelId(id);
            if (binding == null || !salesId.equals(binding.getSalesId())) {
                throw new ServiceException("渠道不属于当前销售");
            }
        }

        // 跟进次数
        List<Map<String, Object>> followupRows = followupRecordMapper.selectFollowupCountByChannel(id);
        int followupCount = followupRows != null && !followupRows.isEmpty()
                ? ((Number) followupRows.get(0).getOrDefault("cnt", 0)).intValue() : 0;

        // 成交笔数 + 业务类型
        List<Map<String, Object>> ledgerRows = commissionLedgerMapper.selectOrderStatsByChannel(id);
        int orderCount = 0;
        List<String> businessTypes = new ArrayList<>();
        if (ledgerRows != null) {
            for (Map<String, Object> row : ledgerRows) {
                orderCount += ((Number) row.getOrDefault("orderCount", 0)).intValue();
                Object bt = row.get("businessType");
                if (bt != null && !businessTypes.contains(bt.toString())) {
                    businessTypes.add(bt.toString());
                }
            }
        }

        // 近 6 月活跃趋势
        YearMonth now = YearMonth.now();
        List<Map<String, Object>> trend = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = now.minusMonths(i);
            Date start = Date.from(ym.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date end = Date.from(ym.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            List<Map<String, Object>> monthRows = followupRecordMapper.selectFollowupCountByChannelAndPeriod(id, start, end);
            int cnt = monthRows != null && !monthRows.isEmpty()
                    ? ((Number) monthRows.get(0).getOrDefault("cnt", 0)).intValue() : 0;
            Map<String, Object> point = new HashMap<>();
            point.put("month", ym.toString());
            point.put("count", cnt);
            trend.add(point);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("followupCount", followupCount);
        result.put("orderCount", orderCount);
        result.put("bizTypeCount", businessTypes.size());
        result.put("businessTypes", businessTypes);
        result.put("activityTrend", trend);
        return AjaxResult.success(result);
    }

    // ===== private helpers =====

    private List<Map<String, Object>> buildChannelListWithMask(
            List<JstSalesChannelBinding> bindings, boolean mask) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (bindings == null) return result;
        for (JstSalesChannelBinding b : bindings) {
            Long userId = channelLookupMapper.selectUserIdByChannelId(b.getChannelId());
            Map<String, Object> row;
            if (userId != null) {
                row = channelLookupMapper.selectCurrentByUserId(userId);
            } else {
                row = new HashMap<>();
            }
            if (row == null) row = new HashMap<>();
            row.put("channelId", b.getChannelId());
            row.put("bindingId", b.getBindingId());
            row.put("bindSource", b.getBindSource());
            row.put("effectiveFrom", b.getEffectiveFrom());
            maskMobile(row, mask);
            result.add(row);
        }
        return result;
    }

    /**
     * 手机号脱敏：将 contactMobile 字段中间 4 位替换为 ****。
     *
     * @param map  渠道信息 Map（原地修改）
     * @param mask 是否脱敏
     */
    private void maskMobile(Map<String, Object> map, boolean mask) {
        if (!mask) return;
        String phone = (String) map.get("contactMobile");
        if (phone == null || phone.length() < 11) return;
        map.put("contactMobile", phone.substring(0, 3) + "****" + phone.substring(7));
    }

    /**
     * 通过 channelId 查对应的 userId（用于 ChannelLookupMapper.selectCurrentByUserId）
     */
    private Long queryUserIdByChannelId(Long channelId) {
        return channelLookupMapper.selectUserIdByChannelId(channelId);
    }
}
