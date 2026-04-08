package com.ruoyi.jst.organizer.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.organizer.dto.ApproveReqDTO;
import com.ruoyi.jst.organizer.dto.PartnerApplyQueryDTO;
import com.ruoyi.jst.organizer.dto.RejectReqDTO;
import com.ruoyi.jst.organizer.dto.SupplementReqDTO;
import com.ruoyi.jst.organizer.service.PartnerApplyService;
import com.ruoyi.jst.organizer.vo.PartnerApplyApproveResVO;
import com.ruoyi.jst.organizer.vo.PartnerApplyDetailVO;
import com.ruoyi.jst.organizer.vo.PartnerApplyListVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 赛事方入驻申请-后台审核 Controller
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/organizer/partner/apply")
public class PartnerApplyAdminController extends BaseController {

    @Autowired
    private PartnerApplyService partnerApplyService;

    /**
     * 查询赛事方入驻申请列表。
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @PreAuthorize("@ss.hasPermi('jst:organizer:partnerApply:list')")
    @GetMapping("/list")
    public TableDataInfo list(PartnerApplyQueryDTO query) {
        startPage();
        List<PartnerApplyListVO> list = partnerApplyService.selectAdminList(query);
        return getDataTable(list);
    }

    /**
     * 查询赛事方入驻申请详情。
     *
     * @param applyId 申请ID
     * @return 详情结果
     */
    @PreAuthorize("@ss.hasPermi('jst:organizer:partnerApply:detail')")
    @GetMapping("/{applyId}")
    public AjaxResult detail(@PathVariable("applyId") Long applyId) {
        PartnerApplyDetailVO vo = partnerApplyService.getAdminDetail(applyId);
        return AjaxResult.success(vo);
    }

    /**
     * 审核通过赛事方入驻申请。
     *
     * @param applyId 申请ID
     * @param req     请求体
     * @return 创建结果
     */
    @PreAuthorize("@ss.hasPermi('jst:organizer:partnerApply:approve')")
    @PostMapping("/{applyId}/approve")
    public AjaxResult approve(@PathVariable("applyId") Long applyId, @Valid @RequestBody ApproveReqDTO req) {
        PartnerApplyApproveResVO vo = partnerApplyService.approve(applyId, req);
        return AjaxResult.success(vo);
    }

    /**
     * 驳回赛事方入驻申请。
     *
     * @param applyId 申请ID
     * @param req     请求体
     * @return 处理结果
     */
    @PreAuthorize("@ss.hasPermi('jst:organizer:partnerApply:reject')")
    @PostMapping("/{applyId}/reject")
    public AjaxResult reject(@PathVariable("applyId") Long applyId, @Valid @RequestBody RejectReqDTO req) {
        partnerApplyService.reject(applyId, req);
        return AjaxResult.success();
    }

    /**
     * 要求赛事方补充材料。
     *
     * @param applyId 申请ID
     * @param req     请求体
     * @return 处理结果
     */
    @PreAuthorize("@ss.hasPermi('jst:organizer:partnerApply:supplement')")
    @PostMapping("/{applyId}/supplement")
    public AjaxResult supplement(@PathVariable("applyId") Long applyId, @Valid @RequestBody SupplementReqDTO req) {
        partnerApplyService.supplement(applyId, req);
        return AjaxResult.success();
    }
}
