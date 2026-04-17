package com.ruoyi.jst.common.aspect;

import com.ruoyi.jst.common.annotation.Sensitive;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * 反射工具：根据 @Sensitive 注解掩码字段值。
 * <p>
 * 支持：单对象 / Collection / Map.values。不递归嵌套对象（避免循环引用），
 * 嵌套 VO 需在外层 VO 字段上自行处理。
 *
 * @author jst
 * @since 1.0.0
 */
public final class SensitiveMasker {

    private SensitiveMasker() {}

    /**
     * 掩码 target 上所有 @Sensitive 字段。
     *
     * @param target     目标对象 / Collection / Map（null 安全）
     * @param maskMoney  是否置 null money 和 rate 字段
     * @param maskPhone  是否脱敏 phone 字段为 139****1234 格式
     * @param maskIdcard 是否脱敏 idcard 字段为 110101********1234 格式
     */
    public static void maskFields(Object target, boolean maskMoney, boolean maskPhone, boolean maskIdcard) {
        if (target == null) return;
        if (target instanceof Collection) {
            for (Object item : (Collection<?>) target) {
                maskOne(item, maskMoney, maskPhone, maskIdcard);
            }
            return;
        }
        if (target instanceof Map) {
            for (Object v : ((Map<?, ?>) target).values()) {
                maskOne(v, maskMoney, maskPhone, maskIdcard);
            }
            return;
        }
        maskOne(target, maskMoney, maskPhone, maskIdcard);
    }

    private static void maskOne(Object target, boolean maskMoney, boolean maskPhone, boolean maskIdcard) {
        if (target == null) return;
        Class<?> clazz = target.getClass();
        if (clazz.getName().startsWith("java.")) return;  // 不处理 jdk 类型
        for (Field field : clazz.getDeclaredFields()) {
            Sensitive ann = field.getAnnotation(Sensitive.class);
            if (ann == null) continue;
            try {
                field.setAccessible(true);
                Object value = field.get(target);
                if (value == null) continue;
                if (maskMoney && (ann.money() || ann.rate())) {
                    field.set(target, null);
                }
                if (maskPhone && ann.phone() && value instanceof String) {
                    field.set(target, maskMobile((String) value));
                }
                if (maskIdcard && ann.idcard() && value instanceof String) {
                    field.set(target, maskIdcardStr((String) value));
                }
            } catch (IllegalAccessException ignored) {
                // skip
            }
        }
    }

    private static String maskMobile(String s) {
        if (s == null || s.length() < 11) return s;
        return s.substring(0, 3) + "****" + s.substring(7);
    }

    private static String maskIdcardStr(String s) {
        if (s == null || s.length() < 18) return s;
        return s.substring(0, 6) + "********" + s.substring(14);
    }
}
