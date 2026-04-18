package com.ruoyi.jst.finance.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.finance.domain.JstInvoiceTitle;
import com.ruoyi.jst.finance.dto.InvoiceTitleSaveReqDTO;
import com.ruoyi.jst.finance.mapper.JstInvoiceTitleMapper;
import com.ruoyi.jst.finance.service.InvoiceTitleService;
import com.ruoyi.jst.finance.vo.InvoiceTitleVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 发票抬头领域服务实现。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class InvoiceTitleServiceImpl implements InvoiceTitleService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceTitleServiceImpl.class);

    private static final String TYPE_COMPANY = "company";
    private static final String TYPE_PERSONAL = "personal";

    @Autowired
    private JstInvoiceTitleMapper jstInvoiceTitleMapper;

    @Override
    public List<InvoiceTitleVO> listByUser(Long userId) {
        if (userId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        List<JstInvoiceTitle> rows = jstInvoiceTitleMapper.selectByUserId(userId);
        List<InvoiceTitleVO> result = new ArrayList<>(rows == null ? 0 : rows.size());
        if (rows != null) {
            for (JstInvoiceTitle row : rows) {
                result.add(toVO(row));
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // TX: save
    public Long save(Long userId, InvoiceTitleSaveReqDTO req) {
        if (userId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        validateTypeAndTaxNo(req);

        Date now = DateUtils.getNowDate();
        String operator = currentOperator();

        if (req.getTitleId() != null) {
            // 更新路径：归属校验
            JstInvoiceTitle existing = jstInvoiceTitleMapper.selectByTitleId(req.getTitleId());
            if (existing == null || !"0".equals(existing.getDelFlag())) {
                throw new ServiceException("发票抬头不存在",
                        BizErrorCode.JST_COMMON_PARAM_INVALID.code());
            }
            if (!userId.equals(existing.getUserId())) {
                throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                        BizErrorCode.JST_COMMON_AUTH_DENIED.code());
            }
            JstInvoiceTitle upd = new JstInvoiceTitle();
            upd.setTitleId(req.getTitleId());
            upd.setTitleType(req.getTitleType());
            upd.setTitleName(req.getTitleName());
            // company 清空 taxNo 时显式写 null；personal 强制置 null 保持语义干净
            upd.setTaxNo(TYPE_COMPANY.equals(req.getTitleType()) ? req.getTaxNo() : null);
            upd.setUpdateBy(operator);
            upd.setUpdateTime(now);
            jstInvoiceTitleMapper.update(upd);
            return req.getTitleId();
        }
        // 新增路径
        JstInvoiceTitle entity = new JstInvoiceTitle();
        entity.setUserId(userId);
        entity.setTitleType(req.getTitleType());
        entity.setTitleName(req.getTitleName());
        entity.setTaxNo(TYPE_COMPANY.equals(req.getTitleType()) ? req.getTaxNo() : null);
        entity.setIsDefault(0);
        entity.setDelFlag("0");
        entity.setCreateBy(operator);
        entity.setCreateTime(now);
        entity.setUpdateBy(operator);
        entity.setUpdateTime(now);
        jstInvoiceTitleMapper.insert(entity);
        log.info("[invoice-title] create userId={} titleId={} type={}",
                userId, entity.getTitleId(), entity.getTitleType());
        return entity.getTitleId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // TX: softDelete
    public void softDelete(Long userId, Long titleId) {
        JstInvoiceTitle existing = requireOwned(userId, titleId);
        if (!"0".equals(existing.getDelFlag())) {
            return; // 幂等：已删除视为成功
        }
        jstInvoiceTitleMapper.softDelete(titleId, currentOperator(), DateUtils.getNowDate());
        log.info("[invoice-title] soft-delete userId={} titleId={}", userId, titleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // TX: setDefault
    public void setDefault(Long userId, Long titleId) {
        JstInvoiceTitle existing = requireOwned(userId, titleId);
        if (!"0".equals(existing.getDelFlag())) {
            throw new ServiceException("发票抬头不存在",
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        Date now = DateUtils.getNowDate();
        String operator = currentOperator();
        // 先清所有默认
        jstInvoiceTitleMapper.clearDefaultByUserId(userId, operator, now);
        // 再标当前
        jstInvoiceTitleMapper.markDefault(titleId, operator, now);
        log.info("[invoice-title] set-default userId={} titleId={}", userId, titleId);
    }

    // ========== private helpers ==========

    private void validateTypeAndTaxNo(InvoiceTitleSaveReqDTO req) {
        if (!TYPE_PERSONAL.equals(req.getTitleType()) && !TYPE_COMPANY.equals(req.getTitleType())) {
            throw new ServiceException("抬头类型仅支持 personal / company",
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        if (TYPE_COMPANY.equals(req.getTitleType()) && StringUtils.isEmpty(req.getTaxNo())) {
            throw new ServiceException("企业抬头必须填写税号",
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
    }

    private JstInvoiceTitle requireOwned(Long userId, Long titleId) {
        if (userId == null || titleId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        JstInvoiceTitle existing = jstInvoiceTitleMapper.selectByTitleId(titleId);
        if (existing == null) {
            throw new ServiceException("发票抬头不存在",
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        if (!userId.equals(existing.getUserId())) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        return existing;
    }

    private static String currentOperator() {
        try {
            String username = SecurityUtils.getUsername();
            return StringUtils.isNotEmpty(username) ? username : "system";
        } catch (Exception e) {
            return "system";
        }
    }

    private static InvoiceTitleVO toVO(JstInvoiceTitle entity) {
        InvoiceTitleVO vo = new InvoiceTitleVO();
        vo.setTitleId(entity.getTitleId());
        vo.setTitleType(entity.getTitleType());
        vo.setTitleName(entity.getTitleName());
        vo.setTaxNo(entity.getTaxNo());
        vo.setIsDefault(entity.getIsDefault());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }
}
