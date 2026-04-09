package com.ruoyi.jst.marketing.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.lock.JstLockTemplate;
import com.ruoyi.jst.marketing.domain.JstRightsWriteoffRecord;
import com.ruoyi.jst.marketing.domain.JstUserRights;
import com.ruoyi.jst.marketing.dto.RightsWriteoffApplyDTO;
import com.ruoyi.jst.marketing.enums.QuotaMode;
import com.ruoyi.jst.marketing.mapper.JstRightsWriteoffRecordMapper;
import com.ruoyi.jst.marketing.mapper.JstUserRightsMapper;
import com.ruoyi.jst.marketing.mapper.UserRightsMapperExt;
import com.ruoyi.jst.marketing.mapper.lookup.MarketingUserLookupMapper;
import com.ruoyi.jst.marketing.service.RightsUserService;
import com.ruoyi.jst.marketing.vo.MyRightsVO;
import com.ruoyi.jst.marketing.vo.RightsDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
 * Rights user service implementation.
 */
@Service
public class RightsUserServiceImpl implements RightsUserService {

    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    @Autowired
    private UserRightsMapperExt userRightsMapperExt;

    @Autowired
    private MarketingUserLookupMapper marketingUserLookupMapper;

    @Autowired
    private JstUserRightsMapper jstUserRightsMapper;

    @Autowired
    private JstRightsWriteoffRecordMapper jstRightsWriteoffRecordMapper;

    @Autowired
    private JstLockTemplate jstLockTemplate;

    @Override
    public List<MyRightsVO> selectMyRights(Long userId, String status) {
        Map<String, Object> currentUser = requireCurrentUser(userId);
        List<Map<String, Object>> rows = userRightsMapperExt.selectMyRights(
                MarketingSupport.normalizeOwnerType(MarketingSupport.stringValue(currentUser.get("userType"))),
                userId,
                status,
                DateUtils.getNowDate());
        List<MyRightsVO> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            MyRightsVO vo = new MyRightsVO();
            vo.setUserRightsId(MarketingSupport.longValue(row.get("userRightsId")));
            vo.setRightsTemplateId(MarketingSupport.longValue(row.get("rightsTemplateId")));
            vo.setRightsName(MarketingSupport.stringValue(row.get("rightsName")));
            vo.setRightsType(MarketingSupport.stringValue(row.get("rightsType")));
            vo.setQuotaMode(MarketingSupport.stringValue(row.get("quotaMode")));
            vo.setQuotaValue((BigDecimal) row.get("quotaValue"));
            vo.setRemainQuota(normalizeRemainQuota(MarketingSupport.stringValue(row.get("quotaMode")),
                    (BigDecimal) row.get("remainQuota")));
            vo.setWriteoffMode(MarketingSupport.stringValue(row.get("writeoffMode")));
            vo.setStatus(MarketingSupport.stringValue(row.get("status")));
            vo.setExpired(Objects.equals(MarketingSupport.intValue(row.get("expired")), 1));
            vo.setValidStart(MarketingSupport.dateValue(row.get("validStart")));
            vo.setValidEnd(MarketingSupport.dateValue(row.get("validEnd")));
            list.add(vo);
        }
        return list;
    }

    @Override
    public RightsDetailVO getDetail(Long userId, Long userRightsId) {
        Map<String, Object> currentUser = requireCurrentUser(userId);
        Map<String, Object> row = userRightsMapperExt.selectDetailOwned(
                userRightsId,
                MarketingSupport.normalizeOwnerType(MarketingSupport.stringValue(currentUser.get("userType"))),
                userId,
                DateUtils.getNowDate());
        if (row == null || row.isEmpty()) {
            throw new ServiceException(BizErrorCode.JST_MARKETING_RIGHTS_NOT_OWNED.message(),
                    BizErrorCode.JST_MARKETING_RIGHTS_NOT_OWNED.code());
        }
        RightsDetailVO detail = buildRightsDetail(row);
        detail.setWriteoffHistory(buildWriteoffHistory(userRightsMapperExt.selectWriteoffHistory(userRightsId)));
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> applyWriteoff(Long userId, Long userRightsId, RightsWriteoffApplyDTO req) {
        Map<String, Object> currentUser = requireCurrentUser(userId);
        String ownerType = MarketingSupport.normalizeOwnerType(MarketingSupport.stringValue(currentUser.get("userType")));
        return jstLockTemplate.execute("lock:rights:writeoff:" + userRightsId, 3, 10, () -> {
            Map<String, Object> row = userRightsMapperExt.selectDetailOwnedForUpdate(
                    userRightsId, ownerType, userId, DateUtils.getNowDate());
            if (row == null || row.isEmpty()) {
                throw new ServiceException(BizErrorCode.JST_MARKETING_RIGHTS_NOT_OWNED.message(),
                        BizErrorCode.JST_MARKETING_RIGHTS_NOT_OWNED.code());
            }
            if (Objects.equals(MarketingSupport.intValue(row.get("expired")), 1)
                    || "expired".equalsIgnoreCase(MarketingSupport.stringValue(row.get("status")))) {
                throw new ServiceException(BizErrorCode.JST_MARKETING_RIGHTS_EXPIRED.message(),
                        BizErrorCode.JST_MARKETING_RIGHTS_EXPIRED.code());
            }
            String status = MarketingSupport.stringValue(row.get("status"));
            if (!"available".equalsIgnoreCase(status) && !"approved".equalsIgnoreCase(status)) {
                throw new ServiceException(BizErrorCode.JST_MARKETING_RIGHTS_STATUS_INVALID.message(),
                        BizErrorCode.JST_MARKETING_RIGHTS_STATUS_INVALID.code());
            }
            String writeoffMode = MarketingSupport.stringValue(row.get("writeoffMode"));
            if (com.ruoyi.jst.marketing.enums.WriteoffMode.isScan(writeoffMode)) {
                throw new ServiceException(BizErrorCode.JST_MARKETING_RIGHTS_REQUIRE_SCAN.message(),
                        BizErrorCode.JST_MARKETING_RIGHTS_REQUIRE_SCAN.code());
            }

            String quotaMode = MarketingSupport.stringValue(row.get("quotaMode"));
            BigDecimal writeoffAmount = resolveWriteoffAmount(quotaMode, req.getWriteoffAmount());
            BigDecimal remainQuota = MarketingSupport.decimalValue(row.get("remainQuota"));
            JstUserRights userRights = jstUserRightsMapper.selectJstUserRightsByUserRightsId(userRightsId);
            if (userRights == null || "2".equals(userRights.getDelFlag())) {
                throw new ServiceException(BizErrorCode.JST_MARKETING_RIGHTS_NOT_OWNED.message(),
                        BizErrorCode.JST_MARKETING_RIGHTS_NOT_OWNED.code());
            }

            if (!QuotaMode.isUnlimited(quotaMode) && remainQuota.compareTo(writeoffAmount) < 0) {
                throw new ServiceException(BizErrorCode.JST_MARKETING_RIGHTS_NO_QUOTA.message(),
                        BizErrorCode.JST_MARKETING_RIGHTS_NO_QUOTA.code());
            }
            BigDecimal newRemainQuota = remainQuota;
            String newStatus = "available";
            if (!QuotaMode.isUnlimited(quotaMode)) {
                newRemainQuota = remainQuota.subtract(writeoffAmount).setScale(2, RoundingMode.HALF_UP);
                if (newRemainQuota.compareTo(ZERO) <= 0) {
                    newRemainQuota = ZERO;
                    newStatus = "used";
                }
            }
            userRights.setRemainQuota(newRemainQuota);
            userRights.setStatus(newStatus);
            userRights.setUpdateBy(MarketingSupport.currentOperator());
            userRights.setUpdateTime(DateUtils.getNowDate());
            jstUserRightsMapper.updateJstUserRights(userRights);

            Date now = DateUtils.getNowDate();
            JstRightsWriteoffRecord record = new JstRightsWriteoffRecord();
            record.setWriteoffNo(generateWriteoffNo(userRightsId, now));
            record.setUserRightsId(userRightsId);
            record.setUseAmount(writeoffAmount);
            record.setApplyRemark(req.getRemark());
            record.setStatus("approved");
            record.setAuditUserId(userId);
            record.setAuditRemark("self_apply_auto_approved");
            record.setWriteoffTime(now);
            record.setCreateBy(MarketingSupport.currentOperator());
            record.setCreateTime(now);
            record.setUpdateBy(MarketingSupport.currentOperator());
            record.setUpdateTime(now);
            record.setDelFlag("0");
            jstRightsWriteoffRecordMapper.insertJstRightsWriteoffRecord(record);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("recordId", record.getRecordId());
            result.put("writeoffNo", record.getWriteoffNo());
            result.put("remainQuota", QuotaMode.isUnlimited(quotaMode) ? null : newRemainQuota);
            result.put("status", newStatus);
            return result;
        });
    }

    private Map<String, Object> requireCurrentUser(Long userId) {
        Map<String, Object> user = marketingUserLookupMapper.selectByUserId(userId);
        if (user == null || user.isEmpty() || !Objects.equals(MarketingSupport.intValue(user.get("status")), 1)) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        return user;
    }

    private RightsDetailVO buildRightsDetail(Map<String, Object> row) {
        RightsDetailVO detail = new RightsDetailVO();
        detail.setUserRightsId(MarketingSupport.longValue(row.get("userRightsId")));
        detail.setRightsTemplateId(MarketingSupport.longValue(row.get("rightsTemplateId")));
        detail.setOwnerType(MarketingSupport.stringValue(row.get("ownerType")));
        detail.setOwnerId(MarketingSupport.longValue(row.get("ownerId")));
        detail.setSourceType(MarketingSupport.stringValue(row.get("sourceType")));
        detail.setSourceRefId(MarketingSupport.longValue(row.get("sourceRefId")));
        detail.setRightsName(MarketingSupport.stringValue(row.get("rightsName")));
        detail.setRightsType(MarketingSupport.stringValue(row.get("rightsType")));
        detail.setQuotaMode(MarketingSupport.stringValue(row.get("quotaMode")));
        detail.setQuotaValue((BigDecimal) row.get("quotaValue"));
        detail.setRemainQuota(normalizeRemainQuota(MarketingSupport.stringValue(row.get("quotaMode")),
                (BigDecimal) row.get("remainQuota")));
        detail.setWriteoffMode(MarketingSupport.stringValue(row.get("writeoffMode")));
        detail.setStatus(MarketingSupport.stringValue(row.get("status")));
        detail.setExpired(Objects.equals(MarketingSupport.intValue(row.get("expired")), 1));
        detail.setRemark(MarketingSupport.stringValue(row.get("remark")));
        detail.setValidStart(MarketingSupport.dateValue(row.get("validStart")));
        detail.setValidEnd(MarketingSupport.dateValue(row.get("validEnd")));
        return detail;
    }

    private List<RightsDetailVO.WriteoffRecordVO> buildWriteoffHistory(List<Map<String, Object>> rows) {
        List<RightsDetailVO.WriteoffRecordVO> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            RightsDetailVO.WriteoffRecordVO vo = new RightsDetailVO.WriteoffRecordVO();
            vo.setRecordId(MarketingSupport.longValue(row.get("recordId")));
            vo.setWriteoffNo(MarketingSupport.stringValue(row.get("writeoffNo")));
            vo.setUseAmount((BigDecimal) row.get("useAmount"));
            vo.setApplyRemark(MarketingSupport.stringValue(row.get("applyRemark")));
            vo.setStatus(MarketingSupport.stringValue(row.get("status")));
            vo.setAuditRemark(MarketingSupport.stringValue(row.get("auditRemark")));
            vo.setWriteoffTime(MarketingSupport.dateValue(row.get("writeoffTime")));
            vo.setCreateTime(MarketingSupport.dateValue(row.get("createTime")));
            list.add(vo);
        }
        return list;
    }

    private BigDecimal normalizeRemainQuota(String quotaMode, BigDecimal remainQuota) {
        if (QuotaMode.isUnlimited(quotaMode)) {
            return null;
        }
        return remainQuota == null ? ZERO : remainQuota.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal resolveWriteoffAmount(String quotaMode, BigDecimal requestAmount) {
        if (requestAmount != null) {
            return requestAmount.setScale(2, RoundingMode.HALF_UP);
        }
        if (QuotaMode.isUnlimited(quotaMode)) {
            return BigDecimal.ONE.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ONE.setScale(2, RoundingMode.HALF_UP);
    }

    private String generateWriteoffNo(Long userRightsId, Date now) {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(now);
        int random = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "RW" + userRightsId + timestamp + random;
    }
}
