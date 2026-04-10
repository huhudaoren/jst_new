package com.ruoyi.jst.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SQL level partner data scope for partner-side queries.
 * <p>
 * The aspect appends a validated SQL fragment into
 * {@code BaseEntity.params.dataScope}, which must then be consumed by Mapper XML.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PartnerDataScope {

    /**
     * Table alias used by the protected SQL fragment.
     */
    String deptAlias() default "";

    /**
     * Repository-native column name for partner ownership.
     */
    String partnerIdColumn() default "partner_id";

    /**
     * Compatibility hook for earlier task-card wording.
     * <p>
     * When set, this value overrides {@link #partnerIdColumn()}.
     */
    String organizerIdColumn() default "";
}
