package com.ruoyi.jst.marketing.service.impl;

import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Shared helpers for jst-marketing custom services.
 */
final class MarketingSupport {

    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    private MarketingSupport() {
    }

    static String currentOperator() {
        String username = SecurityUtils.getUsername();
        if (StringUtils.isNotBlank(username)) {
            return username;
        }
        Long userId = SecurityUtils.getUserId();
        return userId == null ? "system" : String.valueOf(userId);
    }

    static String normalizeOwnerType(String userType) {
        return "channel".equalsIgnoreCase(userType) ? "channel" : "student";
    }

    static String joinIds(List<Long> ids) {
        List<Long> normalized = normalizeIds(ids);
        if (normalized.isEmpty()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < normalized.size(); i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(normalized.get(i));
        }
        return builder.toString();
    }

    static List<Long> parseIds(String raw) {
        List<Long> result = new ArrayList<>();
        if (StringUtils.isBlank(raw)) {
            return result;
        }
        for (String item : raw.split(",")) {
            if (StringUtils.isBlank(item)) {
                continue;
            }
            result.add(Long.valueOf(item.trim()));
        }
        return result;
    }

    static List<Long> normalizeIds(List<Long> ids) {
        Set<Long> ordered = new LinkedHashSet<>();
        if (ids != null) {
            for (Long id : ids) {
                if (id != null) {
                    ordered.add(id);
                }
            }
        }
        return new ArrayList<>(ordered);
    }

    static Long longValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(String.valueOf(value));
    }

    static Integer intValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.valueOf(String.valueOf(value));
    }

    static BigDecimal decimalValue(Object value) {
        if (value == null) {
            return ZERO;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal.setScale(2, RoundingMode.HALF_UP);
        }
        return new BigDecimal(String.valueOf(value)).setScale(2, RoundingMode.HALF_UP);
    }

    static String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    static Date dateValue(Object value) {
        return value instanceof Date ? (Date) value : null;
    }
}
