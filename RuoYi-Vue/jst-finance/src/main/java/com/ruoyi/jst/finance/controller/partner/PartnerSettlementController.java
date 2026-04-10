package com.ruoyi.jst.finance.controller.partner;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.common.annotation.PartnerDataScope;
import com.ruoyi.jst.common.controller.BasePartnerController;
import com.ruoyi.jst.finance.dto.PartnerSettlementQueryReqDTO;
import com.ruoyi.jst.finance.dto.SettlementDisputeReqDTO;
import com.ruoyi.jst.finance.service.PartnerSettlementService;
import com.ruoyi.jst.finance.vo.PartnerSettlementListResVO;
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
 * 赛事方工作台结算接口。
 */
@Validated
@RestController
@RequestMapping("/jst/partner/settlement")
public class PartnerSettlementController extends BasePartnerController {

    @Autowired
    private PartnerSettlementService partnerSettlementService;

    /**
     * 查询当前赛事方结算单列表。
     */
    @PreAuthorize("@ss.hasPermi('jst:channel:event_settlement:list')")
    @PartnerDataScope(deptAlias = "s")
    @GetMapping("/list")
    public TableDataInfo list(@Validated PartnerSettlementQueryReqDTO query) {
        startPage();
        List<PartnerSettlementListResVO> list = partnerSettlementService.listSettlements(query);
        return getDataTable(list);
    }

    /**
     * 查询结算单详情。
     */
    @PreAuthorize("@ss.hasPermi('jst:channel:event_settlement:query')")
    @GetMapping("/{id}")
    public AjaxResult detail(@PathVariable("id") Long id) {
        return success(partnerSettlementService.getSettlementDetail(id));
    }

    /**
     * 赛事方确认结算单。
     */
    @PreAuthorize("@ss.hasPermi('jst:channel:event_settlement:edit')")
    @PostMapping("/{id}/confirm")
    public AjaxResult confirm(@PathVariable("id") Long id) {
        return success(partnerSettlementService.confirmSettlement(id));
    }

    /**
     * 赛事方提交结算争议。
     */
    @PreAuthorize("@ss.hasPermi('jst:channel:event_settlement:edit')")
    @PostMapping("/{id}/dispute")
    public AjaxResult dispute(@PathVariable("id") Long id, @Valid @RequestBody SettlementDisputeReqDTO req) {
        return success(partnerSettlementService.disputeSettlement(id, req));
    }
}
