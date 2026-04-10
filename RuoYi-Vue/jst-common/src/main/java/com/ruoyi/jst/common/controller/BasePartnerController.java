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
@PreAuthorize("@ss.hasRole('jst_partner')")
public abstract class BasePartnerController extends BaseController {

    /**
     * Returns the current authenticated partner id.
     */
    protected Long currentPartnerId() {
        return PartnerScopeUtils.requireCurrentPartnerId();
    }
}
