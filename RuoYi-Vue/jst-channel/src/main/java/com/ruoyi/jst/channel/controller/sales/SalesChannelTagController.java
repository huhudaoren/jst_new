package com.ruoyi.jst.channel.controller.sales;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.channel.service.SalesChannelTagService;
import com.ruoyi.jst.common.controller.BaseSalesController;
import com.ruoyi.jst.common.util.SalesScopeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 渠道标签管理（销售本人视角）。
 * <p>
 * 路径前缀：/sales/me/tags
 * <p>
 * 权限点：sales:me:tags:{list,edit}
 */
@RestController
@RequestMapping("/sales/me/tags")
public class SalesChannelTagController extends BaseSalesController {

    @Autowired
    private SalesChannelTagService tagService;

    /**
     * 按渠道查标签列表。
     *
     * @param channelId 渠道 ID（必传）
     */
    @GetMapping
    @PreAuthorize("@ss.hasPermi('sales:me:tags:list')")
    public AjaxResult list(@RequestParam Long channelId) {
        return AjaxResult.success(tagService.listByChannel(channelId));
    }

    /**
     * 添加标签（UK 冲突时返回友好错误）。
     * <p>
     * 请求体：channelId, tagCode, tagColor
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('sales:me:tags:edit')")
    @Log(title = "添加渠道标签", businessType = BusinessType.INSERT)
    public AjaxResult addTag(@RequestBody TagAddReqBody req) {
        Long salesId = SalesScopeUtils.currentSalesIdRequired();
        return AjaxResult.success(tagService.addTag(req.getChannelId(), req.getTagCode(), req.getTagColor(), salesId));
    }

    /**
     * 删除标签（仅创建者或 admin 可删）。
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('sales:me:tags:edit')")
    @Log(title = "删除渠道标签", businessType = BusinessType.DELETE)
    public AjaxResult removeTag(@PathVariable Long id) {
        String currentUserName = SecurityUtils.getUsername();
        boolean isAdmin = SecurityUtils.isAdmin(SecurityUtils.getUserId());
        tagService.removeTag(id, currentUserName, isAdmin);
        return AjaxResult.success();
    }

    /** 内联请求体 DTO，避免新增独立文件 */
    public static class TagAddReqBody {
        private Long channelId;
        private String tagCode;
        private String tagColor;

        public Long getChannelId() { return channelId; }
        public void setChannelId(Long channelId) { this.channelId = channelId; }
        public String getTagCode() { return tagCode; }
        public void setTagCode(String tagCode) { this.tagCode = tagCode; }
        public String getTagColor() { return tagColor; }
        public void setTagColor(String tagColor) { this.tagColor = tagColor; }
    }
}
