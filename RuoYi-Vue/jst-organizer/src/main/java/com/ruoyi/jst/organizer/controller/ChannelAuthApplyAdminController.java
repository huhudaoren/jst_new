package com.ruoyi.jst.organizer.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.jst.organizer.dto.ApproveChannelReqDTO;
import com.ruoyi.jst.organizer.dto.ChannelAuthApplyQueryDTO;
import com.ruoyi.jst.organizer.dto.RejectReqDTO;
import com.ruoyi.jst.organizer.service.ChannelAuthApplyService;
import com.ruoyi.jst.organizer.service.RegionDictService;
import com.ruoyi.jst.organizer.vo.ChannelAuthApplyVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 管理端-渠道认证申请审核 Controller
 * <p>
 * 路径前缀：/jst/organizer/channel/apply
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/organizer/channel/apply")
public class ChannelAuthApplyAdminController extends BaseController {

    @Autowired
    private ChannelAuthApplyService channelAuthApplyService;

    @Autowired
    private RegionDictService regionDictService;

    /**
     * 分页查询申请列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @PreAuthorize("@ss.hasPermi('jst:organizer:channelApply:list')")
    @GetMapping("/list")
    public TableDataInfo list(ChannelAuthApplyQueryDTO query) {
        startPage();
        List<ChannelAuthApplyVO> list = channelAuthApplyService.listAdminApplies(query);
        return getDataTable(list);
    }

    /**
     * 查询申请详情
     *
     * @param applyId 申请ID
     * @return 申请详情
     */
    @PreAuthorize("@ss.hasPermi('jst:organizer:channelApply:detail')")
    @GetMapping("/{applyId}")
    public AjaxResult detail(@PathVariable Long applyId) {
        return AjaxResult.success(channelAuthApplyService.getApplyDetail(applyId));
    }

    /**
     * 审核通过
     *
     * @param applyId 申请ID
     * @param req     审核入参
     * @return 渠道ID
     */
    @Log(title = "渠道认证审核通过", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('jst:organizer:channelApply:approve')")
    @PostMapping("/{applyId}/approve")
    public AjaxResult approve(@PathVariable Long applyId, @Valid @RequestBody ApproveChannelReqDTO req) {
        return AjaxResult.success(channelAuthApplyService.approve(applyId, req));
    }

    /**
     * 审核驳回
     *
     * @param applyId 申请ID
     * @param req     驳回入参
     * @return 处理结果
     */
    @Log(title = "渠道认证审核驳回", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('jst:organizer:channelApply:reject')")
    @PostMapping("/{applyId}/reject")
    public AjaxResult reject(@PathVariable Long applyId, @Valid @RequestBody RejectReqDTO req) {
        channelAuthApplyService.reject(applyId, req);
        return AjaxResult.success();
    }

    /**
     * 暂停已通过的渠道方
     *
     * @param applyId 申请ID
     * @return 处理结果
     */
    @Log(title = "渠道认证暂停", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('jst:organizer:channelApply:suspend')")
    @PostMapping("/{applyId}/suspend")
    public AjaxResult suspend(@PathVariable Long applyId) {
        channelAuthApplyService.suspend(applyId);
        return AjaxResult.success();
    }

    /**
     * PATCH-5: admin 修正渠道 region（省级维度）
     * <p>
     * 权限：admin / jst_operator。字典校验：region 必须在 jst_region_province 内。
     *
     * @param applyId 申请ID
     * @param body    {"region":"beijing"}
     * @return 处理结果
     */
    @Log(title = "渠道地区修正", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasAnyRoles('admin,jst_operator')")
    @PutMapping("/{applyId}/region")
    public AjaxResult updateRegion(@PathVariable Long applyId, @RequestBody Map<String, String> body) {
        String region = body == null ? null : body.get("region");
        if (region == null || region.trim().isEmpty()) {
            return AjaxResult.error("region 不能为空");
        }
        if (!regionDictService.isValid(region)) {
            return AjaxResult.error("region 不在字典范围（jst_region_province）");
        }
        channelAuthApplyService.updateRegion(applyId, region);
        return AjaxResult.success();
    }
}
