package com.ruoyi.jst.channel.controller.admin;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.channel.service.SalesChannelBindingService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Admin 销售-渠道绑定管理
 * <p>
 * 路径前缀：/admin/sales/binding
 * 角色：admin / jst_operator
 * <p>
 * 当前端点：POST /manual — 手动绑定或转移渠道归属销售。
 *
 * @author jst
 * @since 1.0.0
 */
@RestController
@RequestMapping("/admin/sales/binding")
@PreAuthorize("@ss.hasAnyRoles('admin,jst_operator')")
public class AdminBindingController extends BaseController {

    @Autowired
    private SalesChannelBindingService bindingService;

    /**
     * 手动绑定 / 转移渠道归属销售
     *
     * @param body { channelId, salesId, remark }
     */
    @PostMapping("/manual")
    @Log(title = "销售渠道绑定", businessType = BusinessType.INSERT)
    public AjaxResult manualBind(@RequestBody Map<String, Object> body) {
        Long channelId = toLong(body.get("channelId"));
        Long salesId = toLong(body.get("salesId"));
        String remark = body.getOrDefault("remark", "").toString();

        bindingService.bindOrTransfer(channelId, salesId, "manual", remark,
                SecurityUtils.getUserId());
        return AjaxResult.success("绑定成功");
    }

    private Long toLong(Object v) {
        if (v == null) throw new com.ruoyi.common.exception.ServiceException("参数缺失");
        if (v instanceof Number) return ((Number) v).longValue();
        return Long.parseLong(v.toString());
    }
}
