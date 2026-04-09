package com.ruoyi.jst.marketing.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.marketing.domain.JstRightsTemplate;
import com.ruoyi.jst.marketing.domain.JstUserRights;
import com.ruoyi.jst.marketing.dto.RightsIssueDTO;
import com.ruoyi.jst.marketing.dto.RightsTemplateSaveDTO;
import com.ruoyi.jst.marketing.enums.QuotaMode;
import com.ruoyi.jst.marketing.enums.RightsStatus;
import com.ruoyi.jst.marketing.enums.WriteoffMode;
import com.ruoyi.jst.marketing.mapper.JstRightsTemplateMapper;
import com.ruoyi.jst.marketing.mapper.JstUserRightsMapper;
import com.ruoyi.jst.marketing.mapper.RightsTemplateMapperExt;
import com.ruoyi.jst.marketing.mapper.lookup.MarketingUserLookupMapper;
import com.ruoyi.jst.marketing.service.RightsTemplateService;
import com.ruoyi.jst.marketing.vo.RightsTemplateVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Rights template service implementation.
 */
@Service
public class RightsTemplateServiceImpl implements RightsTemplateService {

    private static final BigDecimal UNLIMITED_REMAIN = new BigDecimal("-1.00");

    @Autowired
    private JstRightsTemplateMapper jstRightsTemplateMapper;

    @Autowired
    private JstUserRightsMapper jstUserRightsMapper;

    @Autowired
    private RightsTemplateMapperExt rightsTemplateMapperExt;

    @Autowired
    private MarketingUserLookupMapper marketingUserLookupMapper;

    @Override
    public List<RightsTemplateVO> selectAdminList(String rightsName, String rightsType, Integer status) {
        List<Map<String, Object>> rows = rightsTemplateMapperExt.selectAdminList(rightsName, rightsType, status);
        List<RightsTemplateVO> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            RightsTemplateVO vo = new RightsTemplateVO();
            vo.setRightsTemplateId(MarketingSupport.longValue(row.get("rightsTemplateId")));
            vo.setRightsName(MarketingSupport.stringValue(row.get("rightsName")));
            vo.setRightsType(MarketingSupport.stringValue(row.get("rightsType")));
            vo.setQuotaMode(MarketingSupport.stringValue(row.get("quotaMode")));
            vo.setQuotaValue((BigDecimal) row.get("quotaValue"));
            vo.setValidDays(MarketingSupport.intValue(row.get("validDays")));
            vo.setWriteoffMode(MarketingSupport.stringValue(row.get("writeoffMode")));
            vo.setApplicableRole(MarketingSupport.stringValue(row.get("applicableRole")));
            vo.setStatus(MarketingSupport.intValue(row.get("status")));
            vo.setRemark(MarketingSupport.stringValue(row.get("remark")));
            vo.setCreateTime(MarketingSupport.dateValue(row.get("createTime")));
            vo.setUpdateTime(MarketingSupport.dateValue(row.get("updateTime")));
            list.add(vo);
        }
        return list;
    }

    @Override
    public RightsTemplateVO getAdminDetail(Long rightsTemplateId) {
        JstRightsTemplate template = requireTemplate(rightsTemplateId);
        RightsTemplateVO vo = new RightsTemplateVO();
        BeanUtils.copyProperties(template, vo);
        vo.setValidDays(template.getValidDays() == null ? null : template.getValidDays().intValue());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(RightsTemplateSaveDTO req) {
        validateTemplateReq(req);
        Date now = DateUtils.getNowDate();
        String operator = MarketingSupport.currentOperator();
        JstRightsTemplate template = new JstRightsTemplate();
        fillTemplate(template, req);
        template.setStatus(0);
        template.setCreateBy(operator);
        template.setCreateTime(now);
        template.setUpdateBy(operator);
        template.setUpdateTime(now);
        template.setDelFlag("0");
        jstRightsTemplateMapper.insertJstRightsTemplate(template);
        return template.getRightsTemplateId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(RightsTemplateSaveDTO req) {
        if (req.getRightsTemplateId() == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        validateTemplateReq(req);
        JstRightsTemplate template = requireTemplate(req.getRightsTemplateId());
        fillTemplate(template, req);
        template.setUpdateBy(MarketingSupport.currentOperator());
        template.setUpdateTime(DateUtils.getNowDate());
        jstRightsTemplateMapper.updateJstRightsTemplate(template);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long rightsTemplateId) {
        requireTemplate(rightsTemplateId);
        if (rightsTemplateMapperExt.markDeleted(rightsTemplateId, MarketingSupport.currentOperator(), DateUtils.getNowDate()) == 0) {
            throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(Long rightsTemplateId) {
        requireTemplate(rightsTemplateId);
        if (rightsTemplateMapperExt.updateStatus(rightsTemplateId, 1, MarketingSupport.currentOperator(), DateUtils.getNowDate()) == 0) {
            throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offline(Long rightsTemplateId) {
        requireTemplate(rightsTemplateId);
        if (rightsTemplateMapperExt.updateStatus(rightsTemplateId, 0, MarketingSupport.currentOperator(), DateUtils.getNowDate()) == 0) {
            throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> issue(Long rightsTemplateId, RightsIssueDTO req) {
        JstRightsTemplate template = requireTemplate(rightsTemplateId);
        if (!Objects.equals(template.getStatus(), 1)) {
            throw new ServiceException(BizErrorCode.JST_MARKETING_RIGHTS_TEMPLATE_NOT_FOUND.message(),
                    BizErrorCode.JST_MARKETING_RIGHTS_TEMPLATE_NOT_FOUND.code());
        }
        List<Long> userIds = MarketingSupport.normalizeIds(req.getUserIds());
        Map<Long, Map<String, Object>> userMap = toUserMap(marketingUserLookupMapper.selectByUserIds(userIds));
        Date now = DateUtils.getNowDate();
        String operator = MarketingSupport.currentOperator();
        long successCount = 0L;
        long failCount = 0L;

        for (Long userId : userIds) {
            Map<String, Object> userRow = userMap.get(userId);
            if (userRow == null || !Objects.equals(MarketingSupport.intValue(userRow.get("status")), 1)) {
                failCount++;
                continue;
            }
            JstUserRights userRights = new JstUserRights();
            userRights.setRightsTemplateId(rightsTemplateId);
            userRights.setOwnerType(MarketingSupport.normalizeOwnerType(MarketingSupport.stringValue(userRow.get("userType"))));
            userRights.setOwnerId(userId);
            userRights.setSourceType("manual");
            userRights.setSourceRefId(rightsTemplateId);
            userRights.setRemainQuota(resolveInitialQuota(template, req.getRemainQuotaOverride()));
            userRights.setValidStart(now);
            userRights.setValidEnd(DateUtils.addDays(now, template.getValidDays() == null ? 30 : template.getValidDays().intValue()));
            userRights.setStatus(RightsStatus.AVAILABLE.dbValue());
            userRights.setCreateBy(operator);
            userRights.setCreateTime(now);
            userRights.setUpdateBy(operator);
            userRights.setUpdateTime(now);
            userRights.setDelFlag("0");
            jstUserRightsMapper.insertJstUserRights(userRights);
            successCount++;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("rightsTemplateId", rightsTemplateId);
        result.put("totalCount", userIds.size());
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        return result;
    }

    private void validateTemplateReq(RightsTemplateSaveDTO req) {
        if (QuotaMode.fromValue(req.getQuotaMode()) == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        if (!WriteoffMode.isSelfApply(req.getWriteoffMode())
                && !WriteoffMode.isScan(req.getWriteoffMode())
                && !WriteoffMode.MANUAL_REVIEW.dbValue().equalsIgnoreCase(req.getWriteoffMode())) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        if (!QuotaMode.isUnlimited(req.getQuotaMode()) && req.getQuotaValue() == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        if (req.getValidDays() == null || req.getValidDays() <= 0) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
    }

    private void fillTemplate(JstRightsTemplate template, RightsTemplateSaveDTO req) {
        template.setRightsName(req.getRightsName());
        template.setRightsType(req.getRightsType());
        template.setQuotaMode(req.getQuotaMode());
        template.setQuotaValue(req.getQuotaValue());
        template.setValidDays(req.getValidDays() == null ? null : req.getValidDays().longValue());
        template.setWriteoffMode(req.getWriteoffMode());
        template.setApplicableRole(req.getApplicableRole());
        template.setRemark(req.getRemark());
    }

    private JstRightsTemplate requireTemplate(Long rightsTemplateId) {
        JstRightsTemplate template = jstRightsTemplateMapper.selectJstRightsTemplateByRightsTemplateId(rightsTemplateId);
        if (template == null || "2".equals(template.getDelFlag())) {
            throw new ServiceException(BizErrorCode.JST_MARKETING_RIGHTS_TEMPLATE_NOT_FOUND.message(),
                    BizErrorCode.JST_MARKETING_RIGHTS_TEMPLATE_NOT_FOUND.code());
        }
        return template;
    }

    private Map<Long, Map<String, Object>> toUserMap(List<Map<String, Object>> rows) {
        Map<Long, Map<String, Object>> map = new LinkedHashMap<>();
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                Long userId = MarketingSupport.longValue(row.get("userId"));
                if (userId != null) {
                    map.put(userId, row);
                }
            }
        }
        return map;
    }

    private BigDecimal resolveInitialQuota(JstRightsTemplate template, BigDecimal overrideQuota) {
        if (QuotaMode.isUnlimited(template.getQuotaMode())) {
            return UNLIMITED_REMAIN;
        }
        if (overrideQuota != null) {
            return overrideQuota.setScale(2, RoundingMode.HALF_UP);
        }
        return template.getQuotaValue() == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : template.getQuotaValue().setScale(2, RoundingMode.HALF_UP);
    }
}
