package com.ruoyi.jst.finance.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.context.JstLoginContext;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.finance.service.WxFinanceService;
import com.ruoyi.jst.finance.vo.ContractRecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 小程序端合同接口。
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/wx/contract")
public class WxContractController extends BaseController {

    @Autowired
    private WxFinanceService wxFinanceService;

    /**
     * 我的合同列表。
     *
     * @return 合同分页数据
     * @关联表 jst_contract_record
     * @关联状态机 SM-22
     * @关联权限 hasRole('jst_channel') or hasRole('jst_partner')
     */
    @PreAuthorize("@ss.hasRole('jst_channel') or @ss.hasRole('jst_partner')")
    @GetMapping("/list")
    public TableDataInfo list() {
        TargetScope scope = resolveTargetScope();
        startPage();
        List<ContractRecordVO> rows = wxFinanceService.listMyContracts(scope.targetType, scope.targetId);
        return getDataTable(rows);
    }

    /**
     * 合同详情。
     *
     * @param contractId 合同ID
     * @return 合同详情
     * @关联表 jst_contract_record
     * @关联状态机 SM-22
     * @关联权限 hasRole('jst_channel') or hasRole('jst_partner')
     */
    @PreAuthorize("@ss.hasRole('jst_channel') or @ss.hasRole('jst_partner')")
    @GetMapping("/{contractId}")
    public AjaxResult detail(@PathVariable("contractId") Long contractId) {
        TargetScope scope = resolveTargetScope();
        return AjaxResult.success(wxFinanceService.getContractDetail(contractId, scope.targetType, scope.targetId));
    }

    private TargetScope resolveTargetScope() {
        if (JstLoginContext.hasRole("jst_channel")) {
            Long channelId = JstLoginContext.currentChannelId();
            if (channelId == null) {
                throw new ServiceException(BizErrorCode.JST_CHANNEL_NOT_AUTHED.message(),
                        BizErrorCode.JST_CHANNEL_NOT_AUTHED.code());
            }
            return new TargetScope("channel", channelId);
        }
        if (JstLoginContext.hasRole("jst_partner")) {
            Long partnerId = JstLoginContext.currentPartnerId();
            if (partnerId == null) {
                throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                        BizErrorCode.JST_COMMON_AUTH_DENIED.code());
            }
            return new TargetScope("partner", partnerId);
        }
        throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                BizErrorCode.JST_COMMON_AUTH_DENIED.code());
    }

    private static final class TargetScope {
        private final String targetType;
        private final Long targetId;

        private TargetScope(String targetType, Long targetId) {
            this.targetType = targetType;
            this.targetId = targetId;
        }
    }
}
