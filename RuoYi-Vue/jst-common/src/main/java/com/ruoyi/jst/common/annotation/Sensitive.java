package com.ruoyi.jst.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 敏感字段标记。配合 SensitiveMasker / MaskSalaryAspect 在响应序列化前置 null 或脱敏值。
 * <p>
 * 用例：
 * <pre>
 *   public class SalesLedgerVO {
 *       {@literal @}Sensitive(money = true)
 *       private BigDecimal amount;
 *
 *       {@literal @}Sensitive(phone = true)
 *       private String channelPhone;
 *   }
 * </pre>
 *
 * @author jst
 * @since 1.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sensitive {
    /** 金额（销售本人不可见，主管/admin 可见） */
    boolean money() default false;
    /** 手机号（所有销售看渠道详情时需点"查看完整"才显示，AC1） */
    boolean phone() default false;
    /** 身份证号（仅 admin 可见） */
    boolean idcard() default false;
    /** 销售费率（销售本人不可见） */
    boolean rate() default false;
}
