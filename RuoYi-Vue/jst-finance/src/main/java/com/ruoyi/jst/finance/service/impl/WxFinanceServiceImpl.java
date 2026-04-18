package com.ruoyi.jst.finance.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.common.audit.OperateLog;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.finance.domain.JstContractRecord;
import com.ruoyi.jst.finance.domain.JstInvoiceRecord;
import com.ruoyi.jst.finance.dto.InvoiceApplyForm;
import com.ruoyi.jst.finance.mapper.JstContractRecordMapper;
import com.ruoyi.jst.finance.mapper.JstInvoiceRecordMapper;
import com.ruoyi.jst.finance.service.WxFinanceService;
import com.ruoyi.jst.finance.vo.ContractRecordVO;
import com.ruoyi.jst.finance.vo.InvoiceRecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 小程序端财务服务实现（合同/发票）。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class WxFinanceServiceImpl implements WxFinanceService {

    @Autowired
    private JstContractRecordMapper jstContractRecordMapper;

    @Autowired
    private JstInvoiceRecordMapper jstInvoiceRecordMapper;

    /**
     * 查询我的合同列表。
     *
     * @param targetType 对象类型(channel/partner)
     * @param targetId   对象ID
     * @return 合同列表
     * @关联表 jst_contract_record
     * @关联状态机 SM-22
     * @关联权限 hasRole('jst_channel') or hasRole('jst_partner')
     */
    @Override
    public List<ContractRecordVO> listMyContracts(String targetType, Long targetId) {
        List<JstContractRecord> records = jstContractRecordMapper.selectByTarget(targetType, targetId);
        List<ContractRecordVO> result = new ArrayList<>();
        for (JstContractRecord record : records) {
            result.add(toContractVO(record));
        }
        return result;
    }

    /**
     * 查询合同详情（归属校验）。
     *
     * @param contractId 合同ID
     * @param targetType 对象类型(channel/partner)
     * @param targetId   对象ID
     * @return 合同详情
     * @关联表 jst_contract_record
     * @关联状态机 SM-22
     * @关联权限 hasRole('jst_channel') or hasRole('jst_partner')
     */
    @Override
    public ContractRecordVO getContractDetail(Long contractId, String targetType, Long targetId) {
        JstContractRecord record = jstContractRecordMapper.selectByIdAndTarget(contractId, targetType, targetId);
        if (record == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        return toContractVO(record);
    }

    /**
     * 查询我的发票列表。
     *
     * @param targetType 对象类型(channel/partner)
     * @param targetId   对象ID
     * @return 发票列表
     * @关联表 jst_invoice_record
     * @关联状态机 SM-23
     * @关联权限 hasRole('jst_channel') or hasRole('jst_partner')
     */
    @Override
    public List<InvoiceRecordVO> listMyInvoices(String targetType, Long targetId) {
        List<JstInvoiceRecord> records = jstInvoiceRecordMapper.selectByTarget(targetType, targetId);
        List<InvoiceRecordVO> result = new ArrayList<>();
        for (JstInvoiceRecord record : records) {
            result.add(toInvoiceVO(record));
        }
        return result;
    }

    /**
     * 查询发票详情（归属校验）。
     *
     * @param invoiceId  发票ID
     * @param targetType 对象类型(channel/partner)
     * @param targetId   对象ID
     * @return 发票详情
     * @关联表 jst_invoice_record
     * @关联状态机 SM-23
     * @关联权限 hasRole('jst_channel') or hasRole('jst_partner')
     */
    @Override
    public InvoiceRecordVO getInvoiceDetail(Long invoiceId, String targetType, Long targetId) {
        JstInvoiceRecord record = jstInvoiceRecordMapper.selectByIdAndTarget(invoiceId, targetType, targetId);
        if (record == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        return toInvoiceVO(record);
    }

    /**
     * 申请开票。支持单选 refSettlementNo 与多选 settlementIds：
     * <ul>
     *   <li>settlementIds 非空：按 ID 列表聚合金额，多选时 refSettlementNo 取 GROUP_CONCAT</li>
     *   <li>settlementIds 为空：退回走单选 refSettlementNo 路径（原逻辑）</li>
     * </ul>
     * 多选校验：①归属当前渠道/partner ②全部 paid ③未被任何有效发票绑定 ④count 等于请求数
     *
     * @param form       申请参数
     * @param targetType 对象类型(channel/partner)
     * @param targetId   对象ID
     * @return 新建发票ID
     * @关联表 jst_invoice_record / jst_rebate_settlement / jst_event_settlement
     * @关联状态机 SM-23
     * @关联权限 hasRole('jst_channel') or hasRole('jst_partner')
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // TX: applyInvoice
    @OperateLog(module = "财务", action = "WX_INVOICE_APPLY", target = "#{form.refSettlementNo}")
    public Long applyInvoice(InvoiceApplyForm form, String targetType, Long targetId) {
        List<Long> settlementIds = form.getSettlementIds();
        boolean multi = settlementIds != null && !settlementIds.isEmpty();

        BigDecimal totalSettlementAmount;
        String refSettlementNo;

        if (multi) {
            // 多选路径
            MultiSettlementResolved resolved = resolveMultiSettlements(settlementIds, targetType, targetId);
            totalSettlementAmount = resolved.totalAmount;
            refSettlementNo = resolved.settlementNos; // 多选拼接 settlement_no
        } else {
            // 单选路径兼容
            if (form.getRefSettlementNo() == null || form.getRefSettlementNo().isBlank()) {
                throw new ServiceException("关联结算单号不能为空",
                        BizErrorCode.JST_COMMON_PARAM_INVALID.code());
            }
            BigDecimal single = querySettlementAmount(form.getRefSettlementNo(), targetType, targetId);
            if (single == null) {
                throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                        BizErrorCode.JST_COMMON_AUTH_DENIED.code());
            }
            totalSettlementAmount = single;
            refSettlementNo = form.getRefSettlementNo();
        }

        BigDecimal applyAmount = normalizeAmount(form.getAmount());
        if (applyAmount.compareTo(totalSettlementAmount) > 0) {
            throw new ServiceException("开票金额不能超过结算实付金额",
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }

        Date now = DateUtils.getNowDate();
        JstInvoiceRecord record = new JstInvoiceRecord();
        record.setTargetType(targetType);
        record.setTargetId(targetId);
        record.setRefSettlementNo(refSettlementNo);
        record.setInvoiceTitle(form.getInvoiceTitle());
        record.setTaxNo(form.getTaxNo());
        record.setAmount(applyAmount);
        record.setRemark(form.getRemark());
        record.setCreateBy(currentOperator());
        record.setCreateTime(now);
        record.setUpdateBy(currentOperator());
        record.setUpdateTime(now);
        record.setDelFlag("0");
        record.setStatus("pending_apply"); // SM-23: create -> pending_apply

        int inserted = jstInvoiceRecordMapper.insertJstInvoiceRecord(record);
        if (inserted <= 0 || record.getInvoiceId() == null) {
            throw new ServiceException("申请开票失败",
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        return record.getInvoiceId();
    }

    /**
     * 多选结算单聚合校验。
     */
    private MultiSettlementResolved resolveMultiSettlements(List<Long> settlementIds,
                                                             String targetType, Long targetId) {
        // 防重复
        java.util.Set<Long> dedup = new java.util.LinkedHashSet<>();
        for (Long id : settlementIds) {
            if (id != null) dedup.add(id);
        }
        if (dedup.isEmpty()) {
            throw new ServiceException("结算单 ID 列表不能为空",
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        List<Long> ids = new java.util.ArrayList<>(dedup);

        // 已被开票的结算单（status 非 voided/red_offset）需拒绝
        List<Long> alreadyInvoiced = jstInvoiceRecordMapper.selectAlreadyInvoicedSettlementIds(ids, targetType);
        if (alreadyInvoiced != null && !alreadyInvoiced.isEmpty()) {
            throw new ServiceException("结算单已被开票，请先作废原发票: " + alreadyInvoiced,
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }

        java.util.Map<String, Object> row;
        if ("channel".equals(targetType)) {
            row = jstInvoiceRecordMapper.sumRebateSettlementAmountByIds(ids, targetId);
        } else if ("partner".equals(targetType)) {
            row = jstInvoiceRecordMapper.sumEventSettlementAmountByIds(ids, targetId);
        } else {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }

        Long matchedCount = toLong(row == null ? null : row.get("matchedCount"));
        BigDecimal totalAmount = toBigDecimal(row == null ? null : row.get("totalAmount"));
        String settlementNos = row == null ? null : (String) row.get("settlementNos");

        if (matchedCount == null || matchedCount.intValue() != ids.size()) {
            // 数量不匹配 → 有不归属 / 非 paid / 已删除的结算单
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }

        MultiSettlementResolved r = new MultiSettlementResolved();
        r.totalAmount = normalizeAmount(totalAmount);
        r.settlementNos = settlementNos;
        return r;
    }

    private static Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.longValue();
        try { return Long.parseLong(String.valueOf(v)); } catch (Exception e) { return null; }
    }

    private static BigDecimal toBigDecimal(Object v) {
        if (v == null) return BigDecimal.ZERO;
        if (v instanceof BigDecimal d) return d;
        if (v instanceof Number n) return new BigDecimal(n.toString());
        try { return new BigDecimal(String.valueOf(v)); } catch (Exception e) { return BigDecimal.ZERO; }
    }

    private static final class MultiSettlementResolved {
        BigDecimal totalAmount;
        String settlementNos;
    }

    private BigDecimal querySettlementAmount(String settlementNo, String targetType, Long targetId) {
        if ("channel".equals(targetType)) {
            return normalizeAmount(jstInvoiceRecordMapper.selectRebateSettlementActualAmount(settlementNo, targetId));
        }
        if ("partner".equals(targetType)) {
            return normalizeAmount(jstInvoiceRecordMapper.selectEventSettlementFinalAmount(settlementNo, targetId));
        }
        throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                BizErrorCode.JST_COMMON_PARAM_INVALID.code());
    }

    private BigDecimal normalizeAmount(BigDecimal amount) {
        return amount == null ? null : amount.setScale(2, RoundingMode.HALF_UP);
    }

    private ContractRecordVO toContractVO(JstContractRecord record) {
        ContractRecordVO vo = new ContractRecordVO();
        vo.setContractId(record.getContractId());
        vo.setContractNo(record.getContractNo());
        vo.setContractType(record.getContractType());
        vo.setStatus(record.getStatus());
        vo.setFileUrl(record.getFileUrl());
        vo.setEffectiveTime(record.getEffectiveTime());
        vo.setSignTime(record.getSignTime());
        vo.setRemark(record.getRemark());
        vo.setCreateTime(record.getCreateTime());
        // 甲乙方字段（容错：新字段可能 null）
        try {
            vo.setPartyA(record.getPartyA());
            vo.setPartyAName(record.getPartyAName());
            vo.setPartyB(record.getPartyB());
            vo.setPartyBName(record.getPartyBName());
        } catch (Exception ignored) {
            // 字段未落地时忽略
        }
        // 关联结算单：contractId 反查 refSettlementId → settlementNo
        Long refSettlementId = record.getRefSettlementId();
        if (refSettlementId != null) {
            vo.setSettlementId(refSettlementId);
            // 根据 target_type 决定查 rebate 还是 event
            try {
                String targetType = record.getTargetType();
                String settlementNo = null;
                if ("channel".equals(targetType)) {
                    settlementNo = jstInvoiceRecordMapper.selectRebateSettlementNoById(refSettlementId);
                } else if ("partner".equals(targetType)) {
                    settlementNo = jstInvoiceRecordMapper.selectEventSettlementNoById(refSettlementId);
                }
                vo.setSettlementNo(settlementNo);
            } catch (Exception ignored) {
                // 查不到静默
            }
        }
        return vo;
    }

    private InvoiceRecordVO toInvoiceVO(JstInvoiceRecord record) {
        InvoiceRecordVO vo = new InvoiceRecordVO();
        vo.setInvoiceId(record.getInvoiceId());
        vo.setInvoiceNo(record.getInvoiceNo());
        vo.setRefSettlementNo(record.getRefSettlementNo());
        vo.setInvoiceTitle(record.getInvoiceTitle());
        vo.setTaxNo(record.getTaxNo());
        vo.setAmount(record.getAmount());
        vo.setStatus(record.getStatus());
        vo.setFileUrl(record.getFileUrl());
        vo.setExpressStatus(record.getExpressStatus());
        vo.setIssueTime(record.getIssueTime());
        vo.setCreateTime(record.getCreateTime());
        // 物流与邮箱字段（容错）
        try {
            vo.setTrackingCompany(record.getTrackingCompany());
            vo.setTrackingNo(record.getTrackingNo());
            vo.setDeliveryEmail(maskEmail(record.getDeliveryEmail()));
        } catch (Exception ignored) {
            // 字段未落地时忽略
        }
        // settlementNo = refSettlementNo（冗余对齐命名）
        vo.setSettlementNo(record.getRefSettlementNo());
        // 反查 settlementId
        if (record.getRefSettlementNo() != null && !record.getRefSettlementNo().isBlank()) {
            try {
                Long id = null;
                String targetType = record.getTargetType();
                if ("channel".equals(targetType)) {
                    id = jstInvoiceRecordMapper.selectRebateSettlementIdByNo(record.getRefSettlementNo());
                } else if ("partner".equals(targetType)) {
                    id = jstInvoiceRecordMapper.selectEventSettlementIdByNo(record.getRefSettlementNo());
                }
                vo.setSettlementId(id);
            } catch (Exception ignored) {
                // 静默
            }
        }
        return vo;
    }

    /**
     * 邮箱脱敏：前 2 字符 + *** + @domain。
     */
    private String maskEmail(String email) {
        if (email == null || email.isEmpty()) return email;
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) return email;
        String prefix = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        if (prefix.length() <= 2) {
            return prefix.charAt(0) + "***" + domain;
        }
        return prefix.substring(0, 2) + "***" + domain;
    }

    private String currentOperator() {
        try {
            String username = SecurityUtils.getUsername();
            if (username != null && !username.isEmpty()) {
                return username;
            }
        } catch (Exception ignored) {
        }
        Long userId = SecurityUtils.getUserId();
        return userId == null ? "system" : String.valueOf(userId);
    }
}
