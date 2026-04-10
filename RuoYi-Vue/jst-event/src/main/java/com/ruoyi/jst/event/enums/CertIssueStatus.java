package com.ruoyi.jst.event.enums;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;

import java.util.Map;
import java.util.Set;

/**
 * 证书发放状态枚举，关联 SM-20。
 *
 * @author jst
 * @since 1.0.0
 */
public enum CertIssueStatus {

    /** 草稿，赛事方可重新生成。 */
    DRAFT("draft"),

    /** 待平台审核。 */
    PENDING("pending"),

    /** 已签发。 */
    ISSUED("issued"),

    /** 已作废。 */
    VOIDED("voided");

    private static final Map<CertIssueStatus, Set<CertIssueStatus>> ALLOWED = Map.of(
            DRAFT, Set.of(PENDING),
            PENDING, Set.of(ISSUED, VOIDED),
            ISSUED, Set.of(VOIDED)
    );

    private final String dbValue;

    CertIssueStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    /**
     * 返回数据库状态值。
     *
     * @return 数据库状态值
     */
    public String dbValue() {
        return dbValue;
    }

    /**
     * 从数据库状态值解析枚举。
     *
     * @param value 数据库状态值
     * @return 状态枚举
     */
    public static CertIssueStatus fromDb(String value) {
        for (CertIssueStatus status : values()) {
            if (status.dbValue.equals(value)) {
                return status;
            }
        }
        throw new ServiceException("未知证书状态: " + value,
                BizErrorCode.JST_EVENT_CERT_ILLEGAL_TRANSIT.code());
    }

    /**
     * 校验是否允许跃迁到目标状态。
     *
     * @param target 目标状态
     */
    public void assertCanTransitTo(CertIssueStatus target) {
        if (!ALLOWED.getOrDefault(this, Set.of()).contains(target)) {
            throw new ServiceException("非法证书状态跃迁: " + dbValue + " -> " + target.dbValue(),
                    BizErrorCode.JST_EVENT_CERT_ILLEGAL_TRANSIT.code());
        }
    }
}
