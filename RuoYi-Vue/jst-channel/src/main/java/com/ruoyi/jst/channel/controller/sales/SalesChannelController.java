package com.ruoyi.jst.channel.controller.sales;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.channel.domain.JstSalesChannelBinding;
import com.ruoyi.jst.channel.mapper.JstSalesChannelBindingMapper;
import com.ruoyi.jst.channel.mapper.lookup.ChannelDetailLookupMapper;
import com.ruoyi.jst.channel.mapper.lookup.ChannelLookupMapper;
import com.ruoyi.jst.common.util.SalesScopeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    /**
     * 当前销售名下渠道列表（含基础信息，手机号脱敏）
     */
    @GetMapping("/list")
    public TableDataInfo list() {
        Long salesId = SalesScopeUtils.currentSalesIdRequired();
        startPage();
        List<JstSalesChannelBinding> bindings = bindingMapper.selectCurrentByOwnerSales(salesId);
        List<Map<String, Object>> result = buildChannelListWithMask(bindings, true);
        return getDataTable(result);
    }

    /**
     * 渠道详情（手机号自动脱敏）
     *
     * @param id 渠道 ID
     */
    @GetMapping("/{id}")
    public AjaxResult detail(@PathVariable Long id) {
        // 验证归属
        Long salesId = SalesScopeUtils.currentSalesIdRequired();
        JstSalesChannelBinding binding = bindingMapper.selectCurrentByChannelId(id);
        if (binding == null || !salesId.equals(binding.getSalesId())) {
            throw new ServiceException("渠道不属于当前销售");
        }
        Map<String, Object> channelMap = channelLookupMapper.selectCurrentByUserId(
                queryUserIdByChannelId(id));
        if (channelMap == null) channelMap = new HashMap<>();
        channelMap.put("channelId", id);
        // 脱敏
        maskMobile(channelMap, true);
        return AjaxResult.success(channelMap);
    }

    /**
     * 查看完整手机号（写审计日志）
     *
     * @param id 渠道 ID
     */
    @GetMapping("/{id}/phone-full")
    @Log(title = "销售查看渠道手机号", businessType = BusinessType.OTHER)
    public AjaxResult phoneFull(@PathVariable Long id) {
        Long salesId = SalesScopeUtils.currentSalesIdRequired();
        JstSalesChannelBinding binding = bindingMapper.selectCurrentByChannelId(id);
        if (binding == null || !salesId.equals(binding.getSalesId())) {
            throw new ServiceException("渠道不属于当前销售");
        }
        // 取原始渠道信息（含联系手机号）
        Map<String, Object> channelMap = channelLookupMapper.selectCurrentByUserId(
                queryUserIdByChannelId(id));
        if (channelMap == null) channelMap = new HashMap<>();
        channelMap.put("channelId", id);
        // phone-full 接口不脱敏
        return AjaxResult.success(channelMap);
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
