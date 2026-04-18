package com.ruoyi.jst.common.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.jst.common.util.PartnerScopeUtils;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Shared controller base for partner workbench endpoints.
 * <p>
 * Subclasses should place {@code @PartnerDataScope} on list/query methods whose
 * request objects extend {@code BaseEntity}.
 */
@PreAuthorize("@ss.hasAnyRoles('jst_partner,admin,jst_operator')")
public abstract class BasePartnerController extends BaseController {

    /**
     * Returns the current authenticated partner id.
     * For admin / jst_operator (platform op), returns {@code null} — endpoints that need it
     * must either gate via {@code @PartnerDataScope} or handle null (e.g. show all data).
     */
    protected Long currentPartnerId() {
        return PartnerScopeUtils.currentPartnerIdAllowPlatformOp();
    }
}
