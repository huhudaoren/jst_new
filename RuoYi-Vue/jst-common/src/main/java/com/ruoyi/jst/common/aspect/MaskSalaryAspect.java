package com.ruoyi.jst.common.aspect;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.common.util.SalesScopeUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 金额脱敏切面。
 * <p>
 * 触发条件：当前用户仅有 jst_sales 角色（非主管/非 admin/非平台运营）。
 * 范围：所有继承 BaseSalesController 的接口的返回值。
 * 行为：
 * <ul>
 *   <li>{@code @Sensitive(money=true 或 rate=true)} 字段反射置 null</li>
 *   <li>{@code @Sensitive(phone=true)} 字段走脱敏字符串（AC1：所有销售常态脱敏，不限制 plainSales）</li>
 * </ul>
 *
 * @author jst
 * @since 1.0.0
 */
@Aspect
@Component
public class MaskSalaryAspect {

    @Pointcut("execution(* com.ruoyi.jst..controller..*Controller.*(..)) " +
              "&& target(com.ruoyi.jst.common.controller.BaseSalesController)")
    public void salesEndpoints() {}

    @AfterReturning(pointcut = "salesEndpoints()", returning = "result")
    public void mask(JoinPoint pjp, Object result) {
        LoginUser loginUser = SalesScopeUtils.getLoginUserQuietly();
        boolean maskMoney = SalesScopeUtils.isPlainSalesOnly(loginUser);
        boolean maskPhone = SalesScopeUtils.isSalesRole(loginUser);  // AC1: 所有销售常态脱敏
        if (!maskMoney && !maskPhone) {
            return;
        }

        Object payload = unwrap(result);
        if (payload != null) {
            SensitiveMasker.maskFields(payload, maskMoney, maskPhone, false);
        }
    }

    /** 拆 AjaxResult / TableDataInfo 拿到真实业务对象 */
    private Object unwrap(Object result) {
        if (result instanceof AjaxResult) {
            return ((AjaxResult) result).get(AjaxResult.DATA_TAG);
        }
        if (result instanceof TableDataInfo) {
            return ((TableDataInfo) result).getRows();
        }
        return result;
    }
}
