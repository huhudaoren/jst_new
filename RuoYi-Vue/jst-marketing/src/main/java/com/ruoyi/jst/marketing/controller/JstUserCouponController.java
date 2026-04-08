package com.ruoyi.jst.marketing.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.jst.marketing.domain.JstUserCoupon;
import com.ruoyi.jst.marketing.service.IJstUserCouponService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 用户持有优惠券Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/jst/marketing/jst_user_coupon")
public class JstUserCouponController extends BaseController
{
    @Autowired
    private IJstUserCouponService jstUserCouponService;

    /**
     * 查询用户持有优惠券列表
     */
    @PreAuthorize("@ss.hasPermi('jst:marketing:user_coupon:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstUserCoupon jstUserCoupon)
    {
        startPage();
        List<JstUserCoupon> list = jstUserCouponService.selectJstUserCouponList(jstUserCoupon);
        return getDataTable(list);
    }

    /**
     * 导出用户持有优惠券列表
     */
    @PreAuthorize("@ss.hasPermi('jst:marketing:user_coupon:export')")
    @Log(title = "用户持有优惠券", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstUserCoupon jstUserCoupon)
    {
        List<JstUserCoupon> list = jstUserCouponService.selectJstUserCouponList(jstUserCoupon);
        ExcelUtil<JstUserCoupon> util = new ExcelUtil<JstUserCoupon>(JstUserCoupon.class);
        util.exportExcel(response, list, "用户持有优惠券数据");
    }

    /**
     * 获取用户持有优惠券详细信息
     */
    @PreAuthorize("@ss.hasPermi('jst:marketing:user_coupon:query')")
    @GetMapping(value = "/{userCouponId}")
    public AjaxResult getInfo(@PathVariable("userCouponId") Long userCouponId)
    {
        return success(jstUserCouponService.selectJstUserCouponByUserCouponId(userCouponId));
    }

    /**
     * 新增用户持有优惠券
     */
    @PreAuthorize("@ss.hasPermi('jst:marketing:user_coupon:add')")
    @Log(title = "用户持有优惠券", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstUserCoupon jstUserCoupon)
    {
        return toAjax(jstUserCouponService.insertJstUserCoupon(jstUserCoupon));
    }

    /**
     * 修改用户持有优惠券
     */
    @PreAuthorize("@ss.hasPermi('jst:marketing:user_coupon:edit')")
    @Log(title = "用户持有优惠券", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstUserCoupon jstUserCoupon)
    {
        return toAjax(jstUserCouponService.updateJstUserCoupon(jstUserCoupon));
    }

    /**
     * 删除用户持有优惠券
     */
    @PreAuthorize("@ss.hasPermi('jst:marketing:user_coupon:remove')")
    @Log(title = "用户持有优惠券", businessType = BusinessType.DELETE)
	@DeleteMapping("/{userCouponIds}")
    public AjaxResult remove(@PathVariable Long[] userCouponIds)
    {
        return toAjax(jstUserCouponService.deleteJstUserCouponByUserCouponIds(userCouponIds));
    }
}
