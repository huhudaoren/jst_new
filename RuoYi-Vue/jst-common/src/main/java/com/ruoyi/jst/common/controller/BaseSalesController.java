package com.ruoyi.jst.common.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.jst.common.util.SalesScopeUtils;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * 销售工作台 / admin 销售管理通用基类（复刻 BasePartnerController 模式）。
 * <p>
 * 类级权限：jst_sales / jst_sales_manager / admin / jst_operator。
 * 子类按需在方法上加细化权限点 {@code @PreAuthorize("@ss.hasPermi('jst:sales:xxx')")}。
 *
 * @author jst
 * @since 1.0.0
 */
@PreAuthorize("@ss.hasAnyRoles('jst_sales,jst_sales_manager,admin,jst_operator')")
public abstract class BaseSalesController extends BaseController {

    /**
     * 当前销售 ID。
     * <ul>
     *   <li>销售/销售主管：返回自己的 sales_id</li>
     *   <li>admin / jst_operator：返 null（业务层据此判断是否过滤）</li>
     * </ul>
     */
    protected Long currentSalesId() {
        return SalesScopeUtils.currentSalesId();
    }
}
