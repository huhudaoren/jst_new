package com.ruoyi.jst.finance.service;

import com.ruoyi.jst.finance.dto.InvoiceApplyForm;
import com.ruoyi.jst.finance.vo.ContractRecordVO;
import com.ruoyi.jst.finance.vo.InvoiceRecordVO;

import java.util.List;

/**
 * 小程序端财务服务（合同/发票）。
 *
 * @author jst
 * @since 1.0.0
 */
public interface WxFinanceService {

    /**
     * 查询我的合同列表。
     *
     * @param targetType 对象类型(channel/partner)
     * @param targetId   对象ID
     * @return 合同列表
     */
    List<ContractRecordVO> listMyContracts(String targetType, Long targetId);

    /**
     * 查询合同详情（归属校验）。
     *
     * @param contractId 合同ID
     * @param targetType 对象类型(channel/partner)
     * @param targetId   对象ID
     * @return 合同详情
     */
    ContractRecordVO getContractDetail(Long contractId, String targetType, Long targetId);

    /**
     * 查询我的发票列表。
     *
     * @param targetType 对象类型(channel/partner)
     * @param targetId   对象ID
     * @return 发票列表
     */
    List<InvoiceRecordVO> listMyInvoices(String targetType, Long targetId);

    /**
     * 查询发票详情（归属校验）。
     *
     * @param invoiceId  发票ID
     * @param targetType 对象类型(channel/partner)
     * @param targetId   对象ID
     * @return 发票详情
     */
    InvoiceRecordVO getInvoiceDetail(Long invoiceId, String targetType, Long targetId);

    /**
     * 申请开票。
     *
     * @param form       申请参数
     * @param targetType 对象类型(channel/partner)
     * @param targetId   对象ID
     * @return 新建发票ID
     */
    Long applyInvoice(InvoiceApplyForm form, String targetType, Long targetId);
}
