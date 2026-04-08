package com.ruoyi.system.controller;

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
import com.ruoyi.system.domain.JstCouponTemplate;
import com.ruoyi.system.service.IJstCouponTemplateService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 优惠券模板Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_coupon_template")
public class JstCouponTemplateController extends BaseController
{
    @Autowired
    private IJstCouponTemplateService jstCouponTemplateService;

    /**
     * 查询优惠券模板列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_coupon_template:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstCouponTemplate jstCouponTemplate)
    {
        startPage();
        List<JstCouponTemplate> list = jstCouponTemplateService.selectJstCouponTemplateList(jstCouponTemplate);
        return getDataTable(list);
    }

    /**
     * 导出优惠券模板列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_coupon_template:export')")
    @Log(title = "优惠券模板", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstCouponTemplate jstCouponTemplate)
    {
        List<JstCouponTemplate> list = jstCouponTemplateService.selectJstCouponTemplateList(jstCouponTemplate);
        ExcelUtil<JstCouponTemplate> util = new ExcelUtil<JstCouponTemplate>(JstCouponTemplate.class);
        util.exportExcel(response, list, "优惠券模板数据");
    }

    /**
     * 获取优惠券模板详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_coupon_template:query')")
    @GetMapping(value = "/{couponTemplateId}")
    public AjaxResult getInfo(@PathVariable("couponTemplateId") Long couponTemplateId)
    {
        return success(jstCouponTemplateService.selectJstCouponTemplateByCouponTemplateId(couponTemplateId));
    }

    /**
     * 新增优惠券模板
     */
    @PreAuthorize("@ss.hasPermi('system:jst_coupon_template:add')")
    @Log(title = "优惠券模板", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstCouponTemplate jstCouponTemplate)
    {
        return toAjax(jstCouponTemplateService.insertJstCouponTemplate(jstCouponTemplate));
    }

    /**
     * 修改优惠券模板
     */
    @PreAuthorize("@ss.hasPermi('system:jst_coupon_template:edit')")
    @Log(title = "优惠券模板", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstCouponTemplate jstCouponTemplate)
    {
        return toAjax(jstCouponTemplateService.updateJstCouponTemplate(jstCouponTemplate));
    }

    /**
     * 删除优惠券模板
     */
    @PreAuthorize("@ss.hasPermi('system:jst_coupon_template:remove')")
    @Log(title = "优惠券模板", businessType = BusinessType.DELETE)
	@DeleteMapping("/{couponTemplateIds}")
    public AjaxResult remove(@PathVariable Long[] couponTemplateIds)
    {
        return toAjax(jstCouponTemplateService.deleteJstCouponTemplateByCouponTemplateIds(couponTemplateIds));
    }
}
