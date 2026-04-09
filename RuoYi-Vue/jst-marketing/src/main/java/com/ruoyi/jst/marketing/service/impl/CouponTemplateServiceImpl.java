package com.ruoyi.jst.marketing.service.impl;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.marketing.domain.JstCouponIssueBatch;
import com.ruoyi.jst.marketing.domain.JstCouponTemplate;
import com.ruoyi.jst.marketing.domain.JstUserCoupon;
import com.ruoyi.jst.marketing.dto.CouponIssueDTO;
import com.ruoyi.jst.marketing.dto.CouponTemplateSaveDTO;
import com.ruoyi.jst.marketing.enums.CouponStatus;
import com.ruoyi.jst.marketing.enums.CouponType;
import com.ruoyi.jst.marketing.mapper.CouponTemplateMapperExt;
import com.ruoyi.jst.marketing.mapper.JstCouponIssueBatchMapper;
import com.ruoyi.jst.marketing.mapper.JstCouponTemplateMapper;
import com.ruoyi.jst.marketing.mapper.JstUserCouponMapper;
import com.ruoyi.jst.marketing.mapper.UserCouponMapperExt;
import com.ruoyi.jst.marketing.mapper.lookup.ContestLookupMapper;
import com.ruoyi.jst.marketing.mapper.lookup.MarketingUserLookupMapper;
import com.ruoyi.jst.marketing.service.CouponTemplateService;
import com.ruoyi.jst.marketing.vo.ClaimableCouponVO;
import com.ruoyi.jst.marketing.vo.CouponTemplateVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Coupon template service implementation.
 */
@Service
public class CouponTemplateServiceImpl implements CouponTemplateService {

    @Autowired
    private JstCouponTemplateMapper jstCouponTemplateMapper;

    @Autowired
    private JstUserCouponMapper jstUserCouponMapper;

    @Autowired
    private JstCouponIssueBatchMapper jstCouponIssueBatchMapper;

    @Autowired
    private CouponTemplateMapperExt couponTemplateMapperExt;

    @Autowired
    private UserCouponMapperExt userCouponMapperExt;

    @Autowired
    private MarketingUserLookupMapper marketingUserLookupMapper;

    @Autowired
    private ContestLookupMapper contestLookupMapper;

    @Override
    public List<CouponTemplateVO> selectAdminList(String couponName, String couponType, Integer status) {
        List<Map<String, Object>> rows = couponTemplateMapperExt.selectAdminList(couponName, couponType, status);
        List<CouponTemplateVO> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            list.add(buildCouponTemplateVO(row));
        }
        return list;
    }

    @Override
    public CouponTemplateVO getAdminDetail(Long couponTemplateId) {
        JstCouponTemplate template = requireTemplate(couponTemplateId, BizErrorCode.JST_MARKETING_COUPON_TEMPLATE_NOT_FOUND);
        CouponTemplateVO vo = new CouponTemplateVO();
        BeanUtils.copyProperties(template, vo);
        vo.setValidDays(template.getValidDays() == null ? null : template.getValidDays().intValue());
        vo.setApplicableContestIds(MarketingSupport.parseIds(template.getApplicableContestIds()));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CouponTemplateSaveDTO req) {
        validateTemplateReq(req);
        Date now = DateUtils.getNowDate();
        String operator = MarketingSupport.currentOperator();
        JstCouponTemplate template = new JstCouponTemplate();
        fillTemplateFromReq(template, req);
        template.setStatus(0);
        template.setCreateBy(operator);
        template.setCreateTime(now);
        template.setUpdateBy(operator);
        template.setUpdateTime(now);
        template.setDelFlag("0");
        jstCouponTemplateMapper.insertJstCouponTemplate(template);
        return template.getCouponTemplateId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CouponTemplateSaveDTO req) {
        if (req.getCouponTemplateId() == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        validateTemplateReq(req);
        JstCouponTemplate exist = requireTemplate(req.getCouponTemplateId(), BizErrorCode.JST_MARKETING_COUPON_TEMPLATE_NOT_FOUND);
        fillTemplateFromReq(exist, req);
        exist.setUpdateBy(MarketingSupport.currentOperator());
        exist.setUpdateTime(DateUtils.getNowDate());
        jstCouponTemplateMapper.updateJstCouponTemplate(exist);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long couponTemplateId) {
        requireTemplate(couponTemplateId, BizErrorCode.JST_MARKETING_COUPON_TEMPLATE_NOT_FOUND);
        if (couponTemplateMapperExt.markDeleted(couponTemplateId, MarketingSupport.currentOperator(), DateUtils.getNowDate()) == 0) {
            throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(Long couponTemplateId) {
        requireTemplate(couponTemplateId, BizErrorCode.JST_MARKETING_COUPON_TEMPLATE_NOT_FOUND);
        if (couponTemplateMapperExt.updateStatus(couponTemplateId, 1, MarketingSupport.currentOperator(), DateUtils.getNowDate()) == 0) {
            throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offline(Long couponTemplateId) {
        requireTemplate(couponTemplateId, BizErrorCode.JST_MARKETING_COUPON_TEMPLATE_NOT_FOUND);
        if (couponTemplateMapperExt.updateStatus(couponTemplateId, 0, MarketingSupport.currentOperator(), DateUtils.getNowDate()) == 0) {
            throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> issue(Long couponTemplateId, CouponIssueDTO req) {
        JstCouponTemplate template = requireTemplate(couponTemplateId, BizErrorCode.JST_MARKETING_COUPON_TEMPLATE_NOT_FOUND);
        assertTemplateCanIssue(template);

        List<Long> userIds = MarketingSupport.normalizeIds(req.getUserIds());
        Map<Long, Map<String, Object>> userMap = toUserMap(marketingUserLookupMapper.selectByUserIds(userIds));
        Date now = DateUtils.getNowDate();
        String operator = MarketingSupport.currentOperator();

        JstCouponIssueBatch batch = new JstCouponIssueBatch();
        batch.setBatchNo(generateBatchNo(template.getCouponTemplateId(), now));
        batch.setCouponTemplateId(template.getCouponTemplateId());
        batch.setTargetType("user");
        batch.setTargetJson(JSON.toJSONString(userIds));
        batch.setTotalCount((long) userIds.size());
        batch.setSuccessCount(0L);
        batch.setFailCount(0L);
        batch.setStatus("running");
        batch.setOperatorId(com.ruoyi.common.utils.SecurityUtils.getUserId());
        batch.setCreateBy(operator);
        batch.setCreateTime(now);
        batch.setUpdateBy(operator);
        batch.setUpdateTime(now);
        batch.setDelFlag("0");
        jstCouponIssueBatchMapper.insertJstCouponIssueBatch(batch);

        long successCount = 0L;
        long failCount = 0L;
        for (Long userId : userIds) {
            Map<String, Object> userRow = userMap.get(userId);
            if (userRow == null || !Objects.equals(MarketingSupport.intValue(userRow.get("status")), 1)) {
                failCount++;
                continue;
            }
            if ("once".equalsIgnoreCase(template.getStackRule())
                    && userCouponMapperExt.countAnyByUserAndTemplate(userId, template.getCouponTemplateId()) > 0) {
                failCount++;
                continue;
            }
            JstUserCoupon userCoupon = new JstUserCoupon();
            userCoupon.setCouponTemplateId(template.getCouponTemplateId());
            userCoupon.setUserId(userId);
            userCoupon.setIssueBatchNo(batch.getBatchNo());
            userCoupon.setIssueSource("platform");
            userCoupon.setStatus(CouponStatus.UNUSED.dbValue());
            userCoupon.setValidStart(resolveCouponValidStart(template, now));
            userCoupon.setValidEnd(resolveCouponValidEnd(template, now));
            userCoupon.setCreateBy(operator);
            userCoupon.setCreateTime(now);
            userCoupon.setUpdateBy(operator);
            userCoupon.setUpdateTime(now);
            userCoupon.setDelFlag("0");
            jstUserCouponMapper.insertJstUserCoupon(userCoupon);
            successCount++;
        }
        batch.setSuccessCount(successCount);
        batch.setFailCount(failCount);
        batch.setStatus("completed");
        batch.setUpdateBy(operator);
        batch.setUpdateTime(now);
        jstCouponIssueBatchMapper.updateJstCouponIssueBatch(batch);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("batchId", batch.getBatchId());
        result.put("batchNo", batch.getBatchNo());
        result.put("totalCount", userIds.size());
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        return result;
    }

    @Override
    public List<ClaimableCouponVO> selectClaimableTemplates(Long userId) {
        Map<String, Object> currentUser = requireCurrentUser(userId);
        List<Long> excludeTemplateIds = userCouponMapperExt.selectOwnedTemplateIds(userId);
        List<Map<String, Object>> rows = couponTemplateMapperExt.selectClaimableTemplates(
                MarketingSupport.normalizeOwnerType(MarketingSupport.stringValue(currentUser.get("userType"))),
                DateUtils.getNowDate(),
                excludeTemplateIds);
        return buildClaimableCouponList(rows);
    }

    @Override
    public List<ClaimableCouponVO> selectClaimableTemplatesByIds(Long userId, List<Long> templateIds) {
        List<Long> normalizedIds = MarketingSupport.normalizeIds(templateIds);
        if (normalizedIds.isEmpty()) {
            return new ArrayList<>();
        }
        Map<String, Object> currentUser = requireCurrentUser(userId);
        List<Long> excludeTemplateIds = userCouponMapperExt.selectOwnedTemplateIds(userId);
        List<Map<String, Object>> rows = couponTemplateMapperExt.selectClaimableTemplatesByIds(
                normalizedIds,
                MarketingSupport.normalizeOwnerType(MarketingSupport.stringValue(currentUser.get("userType"))),
                DateUtils.getNowDate(),
                excludeTemplateIds);
        return buildClaimableCouponList(rows);
    }

    Date resolveCouponValidStart(JstCouponTemplate template, Date now) {
        if (template.getValidDays() != null && template.getValidDays() > 0) {
            return now;
        }
        return template.getValidStart() == null ? now : template.getValidStart();
    }

    Date resolveCouponValidEnd(JstCouponTemplate template, Date now) {
        if (template.getValidDays() != null && template.getValidDays() > 0) {
            return DateUtils.addDays(now, template.getValidDays().intValue());
        }
        return template.getValidEnd();
    }

    private void validateTemplateReq(CouponTemplateSaveDTO req) {
        CouponType couponType = CouponType.fromValue(req.getCouponType());
        if (couponType == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        if (!"once".equalsIgnoreCase(req.getStackRule()) && !"unlimited".equalsIgnoreCase(req.getStackRule())) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        if (req.getValidDays() == null || req.getValidDays() <= 0) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        if (couponType == CouponType.DISCOUNT && req.getDiscountRate() == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        if ((couponType == CouponType.FULL_REDUCE || couponType == CouponType.DIRECT_REDUCE || couponType == CouponType.CONTEST_SPECIFIC)
                && req.getFaceValue() == null && req.getDiscountRate() == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        validateContestIds(req.getApplicableContestIds());
    }

    private void validateContestIds(List<Long> contestIds) {
        List<Long> normalized = MarketingSupport.normalizeIds(contestIds);
        if (normalized.isEmpty()) {
            return;
        }
        List<Map<String, Object>> rows = contestLookupMapper.selectContestNamesByIds(normalized);
        if (rows == null || rows.size() != normalized.size()) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
    }

    private void fillTemplateFromReq(JstCouponTemplate template, CouponTemplateSaveDTO req) {
        CouponType couponType = CouponType.fromValue(req.getCouponType());
        template.setCouponName(req.getCouponName());
        template.setCouponType(couponType == null ? req.getCouponType() : couponType.dbValue());
        template.setFaceValue(req.getFaceValue());
        template.setDiscountRate(req.getDiscountRate());
        template.setThresholdAmount(req.getThresholdAmount());
        template.setApplicableRole(req.getApplicableRole());
        template.setApplicableContestIds(MarketingSupport.joinIds(req.getApplicableContestIds()));
        template.setValidDays(req.getValidDays() == null ? null : req.getValidDays().longValue());
        template.setValidStart(req.getValidStart());
        template.setValidEnd(req.getValidEnd());
        template.setStackRule(req.getStackRule());
        template.setRemark(req.getRemark());
    }

    private JstCouponTemplate requireTemplate(Long couponTemplateId, BizErrorCode errorCode) {
        JstCouponTemplate template = jstCouponTemplateMapper.selectJstCouponTemplateByCouponTemplateId(couponTemplateId);
        if (template == null || "2".equals(template.getDelFlag())) {
            throw new ServiceException(errorCode.message(), errorCode.code());
        }
        return template;
    }

    private Map<String, Object> requireCurrentUser(Long userId) {
        Map<String, Object> user = marketingUserLookupMapper.selectByUserId(userId);
        if (user == null || user.isEmpty() || !Objects.equals(MarketingSupport.intValue(user.get("status")), 1)) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        return user;
    }

    private void assertTemplateCanIssue(JstCouponTemplate template) {
        if (!Objects.equals(template.getStatus(), 1)) {
            throw new ServiceException(BizErrorCode.JST_MARKETING_COUPON_INVALID.message(),
                    BizErrorCode.JST_MARKETING_COUPON_INVALID.code());
        }
        Date now = DateUtils.getNowDate();
        if (template.getValidStart() != null && template.getValidStart().after(now)) {
            throw new ServiceException(BizErrorCode.JST_MARKETING_COUPON_INVALID.message(),
                    BizErrorCode.JST_MARKETING_COUPON_INVALID.code());
        }
        if (template.getValidEnd() != null && template.getValidEnd().before(now)) {
            throw new ServiceException(BizErrorCode.JST_MARKETING_COUPON_EXPIRED.message(),
                    BizErrorCode.JST_MARKETING_COUPON_EXPIRED.code());
        }
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

    private CouponTemplateVO buildCouponTemplateVO(Map<String, Object> row) {
        CouponTemplateVO vo = new CouponTemplateVO();
        vo.setCouponTemplateId(MarketingSupport.longValue(row.get("couponTemplateId")));
        vo.setCouponName(MarketingSupport.stringValue(row.get("couponName")));
        vo.setCouponType(MarketingSupport.stringValue(row.get("couponType")));
        vo.setFaceValue((java.math.BigDecimal) row.get("faceValue"));
        vo.setDiscountRate((java.math.BigDecimal) row.get("discountRate"));
        vo.setThresholdAmount((java.math.BigDecimal) row.get("thresholdAmount"));
        vo.setApplicableRole(MarketingSupport.stringValue(row.get("applicableRole")));
        vo.setApplicableContestIds(MarketingSupport.parseIds(MarketingSupport.stringValue(row.get("applicableContestIds"))));
        vo.setValidDays(MarketingSupport.intValue(row.get("validDays")));
        vo.setValidStart(MarketingSupport.dateValue(row.get("validStart")));
        vo.setValidEnd(MarketingSupport.dateValue(row.get("validEnd")));
        vo.setStackRule(MarketingSupport.stringValue(row.get("stackRule")));
        vo.setStatus(MarketingSupport.intValue(row.get("status")));
        vo.setRemark(MarketingSupport.stringValue(row.get("remark")));
        vo.setCreateTime(MarketingSupport.dateValue(row.get("createTime")));
        vo.setUpdateTime(MarketingSupport.dateValue(row.get("updateTime")));
        return vo;
    }

    private List<ClaimableCouponVO> buildClaimableCouponList(List<Map<String, Object>> rows) {
        List<ClaimableCouponVO> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            ClaimableCouponVO vo = new ClaimableCouponVO();
            vo.setCouponTemplateId(MarketingSupport.longValue(row.get("couponTemplateId")));
            vo.setCouponName(MarketingSupport.stringValue(row.get("couponName")));
            vo.setCouponType(MarketingSupport.stringValue(row.get("couponType")));
            vo.setFaceValue((java.math.BigDecimal) row.get("faceValue"));
            vo.setDiscountRate((java.math.BigDecimal) row.get("discountRate"));
            vo.setThresholdAmount((java.math.BigDecimal) row.get("thresholdAmount"));
            vo.setApplicableRole(MarketingSupport.stringValue(row.get("applicableRole")));
            vo.setApplicableContestIds(MarketingSupport.parseIds(MarketingSupport.stringValue(row.get("applicableContestIds"))));
            vo.setValidDays(MarketingSupport.intValue(row.get("validDays")));
            vo.setValidStart(MarketingSupport.dateValue(row.get("validStart")));
            vo.setValidEnd(MarketingSupport.dateValue(row.get("validEnd")));
            vo.setStackRule(MarketingSupport.stringValue(row.get("stackRule")));
            vo.setStatus(MarketingSupport.intValue(row.get("status")));
            list.add(vo);
        }
        return list;
    }

    private String generateBatchNo(Long templateId, Date now) {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(now);
        int random = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "CP" + templateId + timestamp + random;
    }
}
