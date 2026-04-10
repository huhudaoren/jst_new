package com.ruoyi.jst.finance.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 赛事方结算单详情视图。
 */
public class PartnerSettlementDetailResVO extends PartnerSettlementListResVO {

    private List<PartnerSettlementOrderResVO> orderList = new ArrayList<>();
    private List<PartnerSettlementFileResVO> payoutFiles = new ArrayList<>();
    private List<PartnerSettlementFileResVO> contractFiles = new ArrayList<>();
    private List<PartnerSettlementFileResVO> invoiceFiles = new ArrayList<>();

    public List<PartnerSettlementOrderResVO> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<PartnerSettlementOrderResVO> orderList) {
        this.orderList = orderList;
    }

    public List<PartnerSettlementFileResVO> getPayoutFiles() {
        return payoutFiles;
    }

    public void setPayoutFiles(List<PartnerSettlementFileResVO> payoutFiles) {
        this.payoutFiles = payoutFiles;
    }

    public List<PartnerSettlementFileResVO> getContractFiles() {
        return contractFiles;
    }

    public void setContractFiles(List<PartnerSettlementFileResVO> contractFiles) {
        this.contractFiles = contractFiles;
    }

    public List<PartnerSettlementFileResVO> getInvoiceFiles() {
        return invoiceFiles;
    }

    public void setInvoiceFiles(List<PartnerSettlementFileResVO> invoiceFiles) {
        this.invoiceFiles = invoiceFiles;
    }
}
