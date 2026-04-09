package com.ruoyi.jst.marketing.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.lock.JstLockTemplate;
import com.ruoyi.jst.marketing.domain.JstCouponTemplate;
import com.ruoyi.jst.marketing.domain.JstUserCoupon;
import com.ruoyi.jst.marketing.dto.CouponSelectDTO;
import com.ruoyi.jst.marketing.enums.CouponType;
import com.ruoyi.jst.marketing.mapper.JstCouponTemplateMapper;
import com.ruoyi.jst.marketing.mapper.JstUserCouponMapper;
import com.ruoyi.jst.marketing.mapper.UserCouponMapperExt;
import com.ruoyi.jst.marketing.mapper.lookup.MarketingUserLookupMapper;
import com.ruoyi.jst.marketing.service.CouponUserService;
import com.ruoyi.jst.marketing.vo.CouponSelectResVO;
import com.ruoyi.jst.marketing.vo.MyCouponVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Coupon user service implementation.
 */
@Service
public class CouponUserServiceImpl implements CouponUserService {

    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    @Autowired
    private JstCouponTemplateMapper jstCouponTemplateMapper;

    @Autowired
    private JstUserCouponMapper jstUserCouponMapper;

    @Autowired
    private UserCouponMapperExt userCouponMapperExt;

    @Autowired
    private MarketingUserLookupMapper marketingUserLookupMapper;

    @Autowired
    private JstLockTemplate jstLockTemplate;

    @Autowired
    private CouponTemplateServiceImpl couponTemplateService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long claim(Long userId, Long templateId) {
        Map<String, Object> currentUser = marketingUserLookupMapper.selectByUserId(userId);
        if (currentUser == null || currentUser.isEmpty()) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        return jstLockTemplate.execute("lock:coupon:claim:" + userId + ":" + templateId, 3, 10, () -> {
            JstCouponTemplate template = requireTemplate(templateId);
            assertClaimableTemplate(template, MarketingSupport.normalizeOwnerType(MarketingSupport.stringValue(currentUser.get("userType"))));
            if ("once".equalsIgnoreCase(template.getStackRule())
                    && userCouponMapperExt.countAnyByUserAndTemplate(userId, templateId) > 0) {
                throw new ServiceException(BizErrorCode.JST_MARKETING_COUPON_CLAIM_LIMIT.message(),
                        BizErrorCode.JST_MARKETING_COUPON_CLAIM_LIMIT.code());
            }
            Date now = DateUtils.getNowDate();
            JstUserCoupon userCoupon = new JstUserCoupon();
            userCoupon.setCouponTemplateId(templateId);
            userCoupon.setUserId(userId);
            userCoupon.setIssueSource("platform");
            userCoupon.setStatus("unused");
            userCoupon.setValidStart(couponTemplateService.resolveCouponValidStart(template, now));
            userCoupon.setValidEnd(couponTemplateService.resolveCouponValidEnd(template, now));
            userCoupon.setCreateBy(MarketingSupport.currentOperator());
            userCoupon.setCreateTime(now);
            userCoupon.setUpdateBy(MarketingSupport.currentOperator());
            userCoupon.setUpdateTime(now);
            userCoupon.setDelFlag("0");
            jstUserCouponMapper.insertJstUserCoupon(userCoupon);
            return userCoupon.getUserCouponId();
        });
    }

    @Override
    public List<MyCouponVO> selectMyCoupons(Long userId, String status) {
        List<Map<String, Object>> rows = userCouponMapperExt.selectMyCoupons(userId, status, DateUtils.getNowDate());
        List<MyCouponVO> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            MyCouponVO vo = new MyCouponVO();
            vo.setUserCouponId(MarketingSupport.longValue(row.get("userCouponId")));
            vo.setCouponTemplateId(MarketingSupport.longValue(row.get("couponTemplateId")));
            vo.setCouponName(MarketingSupport.stringValue(row.get("couponName")));
            vo.setCouponType(MarketingSupport.stringValue(row.get("couponType")));
            vo.setFaceValue((BigDecimal) row.get("faceValue"));
            vo.setDiscountRate((BigDecimal) row.get("discountRate"));
            vo.setThresholdAmount((BigDecimal) row.get("thresholdAmount"));
            vo.setStatus(MarketingSupport.stringValue(row.get("status")));
            vo.setExpired(Objects.equals(MarketingSupport.intValue(row.get("expired")), 1));
            vo.setValidStart(MarketingSupport.dateValue(row.get("validStart")));
            vo.setValidEnd(MarketingSupport.dateValue(row.get("validEnd")));
            vo.setUseTime(MarketingSupport.dateValue(row.get("useTime")));
            list.add(vo);
        }
        return list;
    }

    @Override
    public CouponSelectResVO selectBestCoupon(Long userId, CouponSelectDTO req) {
        BigDecimal orderAmount = req.getOrderAmount() == null ? ZERO : req.getOrderAmount().setScale(2, RoundingMode.HALF_UP);
        List<Map<String, Object>> rows = userCouponMapperExt.selectSelectableCoupons(
                userId, MarketingSupport.normalizeIds(req.getCandidateCouponIds()), DateUtils.getNowDate());

        List<CouponSelectResVO.AlternativeVO> alternatives = new ArrayList<>();
        CouponSelectResVO.AlternativeVO best = null;
        for (Map<String, Object> row : rows) {
            CouponSelectResVO.AlternativeVO alternative = evaluateAlternative(row, req, orderAmount);
            alternatives.add(alternative);
            if (Boolean.TRUE.equals(alternative.getApplicable())
                    && (best == null || compareAlternative(alternative, best) < 0)) {
                best = alternative;
            }
        }
        alternatives.sort(Comparator.comparing(CouponSelectResVO.AlternativeVO::getApplicable).reversed()
                .thenComparing(CouponSelectResVO.AlternativeVO::getDiscountAmount, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(CouponSelectResVO.AlternativeVO::getCouponId));

        CouponSelectResVO result = new CouponSelectResVO();
        result.setAlternatives(alternatives);
        if (best == null) {
            result.setBestCouponId(null);
            result.setBestDiscount(ZERO);
            result.setNetPayAmount(orderAmount);
        } else {
            result.setBestCouponId(best.getCouponId());
            result.setBestDiscount(best.getDiscountAmount());
            result.setNetPayAmount(best.getNetPayAmount());
        }
        return result;
    }

    private JstCouponTemplate requireTemplate(Long templateId) {
        JstCouponTemplate template = jstCouponTemplateMapper.selectJstCouponTemplateByCouponTemplateId(templateId);
        if (template == null || "2".equals(template.getDelFlag())) {
            throw new ServiceException(BizErrorCode.JST_MARKETING_COUPON_TEMPLATE_NOT_FOUND.message(),
                    BizErrorCode.JST_MARKETING_COUPON_TEMPLATE_NOT_FOUND.code());
        }
        return template;
    }

    private void assertClaimableTemplate(JstCouponTemplate template, String currentRole) {
        if (!Objects.equals(template.getStatus(), 1)) {
            throw new ServiceException(BizErrorCode.JST_MARKETING_COUPON_INVALID.message(),
                    BizErrorCode.JST_MARKETING_COUPON_INVALID.code());
        }
        if (!(Objects.equals(template.getApplicableRole(), currentRole)
                || "all".equalsIgnoreCase(template.getApplicableRole())
                || "both".equalsIgnoreCase(template.getApplicableRole()))) {
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

    private CouponSelectResVO.AlternativeVO evaluateAlternative(Map<String, Object> row, CouponSelectDTO req,
                                                                BigDecimal orderAmount) {
        CouponSelectResVO.AlternativeVO alternative = new CouponSelectResVO.AlternativeVO();
        alternative.setCouponId(MarketingSupport.longValue(row.get("userCouponId")));
        alternative.setCouponTemplateId(MarketingSupport.longValue(row.get("couponTemplateId")));
        alternative.setCouponName(MarketingSupport.stringValue(row.get("couponName")));

        CouponType couponType = CouponType.fromValue(MarketingSupport.stringValue(row.get("couponType")));
        if (couponType == null) {
            alternative.setApplicable(false);
            alternative.setReason("coupon_type_invalid");
            alternative.setDiscountAmount(ZERO);
            alternative.setNetPayAmount(orderAmount);
            return alternative;
        }

        BigDecimal thresholdAmount = MarketingSupport.decimalValue(row.get("thresholdAmount"));
        BigDecimal faceValue = MarketingSupport.decimalValue(row.get("faceValue"));
        BigDecimal discountRate = row.get("discountRate") == null ? null : MarketingSupport.decimalValue(row.get("discountRate"));
        String applicableContestIds = MarketingSupport.stringValue(row.get("applicableContestIds"));

        if (couponType == CouponType.FULL_REDUCE && orderAmount.compareTo(thresholdAmount) < 0) {
            alternative.setApplicable(false);
            alternative.setReason("threshold_not_met");
            alternative.setDiscountAmount(ZERO);
            alternative.setNetPayAmount(orderAmount);
            return alternative;
        }
        if (couponType == CouponType.CONTEST_SPECIFIC && !matchContest(applicableContestIds, req.getContestId())) {
            alternative.setApplicable(false);
            alternative.setReason("contest_not_match");
            alternative.setDiscountAmount(ZERO);
            alternative.setNetPayAmount(orderAmount);
            return alternative;
        }

        BigDecimal discountAmount;
        switch (couponType) {
            case FULL_REDUCE:
            case DIRECT_REDUCE:
            case CONTEST_SPECIFIC:
                discountAmount = faceValue;
                break;
            case DISCOUNT:
                if (discountRate == null) {
                    alternative.setApplicable(false);
                    alternative.setReason("discount_rate_missing");
                    alternative.setDiscountAmount(ZERO);
                    alternative.setNetPayAmount(orderAmount);
                    return alternative;
                }
                discountAmount = orderAmount.multiply(BigDecimal.ONE.subtract(discountRate))
                        .setScale(2, RoundingMode.HALF_UP);
                break;
            default:
                discountAmount = ZERO;
        }
        if (discountAmount.compareTo(orderAmount) > 0) {
            discountAmount = orderAmount;
        }
        BigDecimal netPayAmount = orderAmount.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
        alternative.setApplicable(true);
        alternative.setReason(null);
        alternative.setDiscountAmount(discountAmount);
        alternative.setNetPayAmount(netPayAmount);
        return alternative;
    }

    private boolean matchContest(String applicableContestIds, Long contestId) {
        if (applicableContestIds == null || applicableContestIds.isBlank()) {
            return true;
        }
        if (contestId == null) {
            return false;
        }
        for (Long id : MarketingSupport.parseIds(applicableContestIds)) {
            if (Objects.equals(id, contestId)) {
                return true;
            }
        }
        return false;
    }

    private int compareAlternative(CouponSelectResVO.AlternativeVO left, CouponSelectResVO.AlternativeVO right) {
        int discountCompare = right.getDiscountAmount().compareTo(left.getDiscountAmount());
        if (discountCompare != 0) {
            return discountCompare;
        }
        return Long.compare(left.getCouponId(), right.getCouponId());
    }
}
