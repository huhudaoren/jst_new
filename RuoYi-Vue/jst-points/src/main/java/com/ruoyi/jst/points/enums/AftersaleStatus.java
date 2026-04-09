package com.ruoyi.jst.points.enums;

import com.ruoyi.common.utils.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 商城售后前端展示状态。
 */
public enum AftersaleStatus {

    APPLYING("applying", List.of("pending")),
    APPROVED("approved", List.of("approved", "refunding")),
    REJECTED("rejected", List.of("rejected")),
    COMPLETED("completed", List.of("completed")),
    CANCELLED("cancelled", List.of("closed"));

    private final String value;

    private final List<String> refundStatuses;

    AftersaleStatus(String value, List<String> refundStatuses) {
        this.value = value;
        this.refundStatuses = refundStatuses;
    }

    public List<String> refundStatuses() {
        return refundStatuses;
    }

    public static AftersaleStatus fromValue(String value) {
        return Arrays.stream(values())
                .filter(item -> item.value.equalsIgnoreCase(StringUtils.trim(value)))
                .findFirst()
                .orElse(null);
    }
}
