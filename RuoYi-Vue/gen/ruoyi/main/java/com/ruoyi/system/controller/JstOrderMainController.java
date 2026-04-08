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
import com.ruoyi.system.domain.JstOrderMain;
import com.ruoyi.system.service.IJstOrderMainService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 订单主Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_order_main")
public class JstOrderMainController extends BaseController
{
    @Autowired
    private IJstOrderMainService jstOrderMainService;

    /**
     * 查询订单主列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_order_main:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstOrderMain jstOrderMain)
    {
        startPage();
        List<JstOrderMain> list = jstOrderMainService.selectJstOrderMainList(jstOrderMain);
        return getDataTable(list);
    }

    /**
     * 导出订单主列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_order_main:export')")
    @Log(title = "订单主", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstOrderMain jstOrderMain)
    {
        List<JstOrderMain> list = jstOrderMainService.selectJstOrderMainList(jstOrderMain);
        ExcelUtil<JstOrderMain> util = new ExcelUtil<JstOrderMain>(JstOrderMain.class);
        util.exportExcel(response, list, "订单主数据");
    }

    /**
     * 获取订单主详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_order_main:query')")
    @GetMapping(value = "/{orderId}")
    public AjaxResult getInfo(@PathVariable("orderId") Long orderId)
    {
        return success(jstOrderMainService.selectJstOrderMainByOrderId(orderId));
    }

    /**
     * 新增订单主
     */
    @PreAuthorize("@ss.hasPermi('system:jst_order_main:add')")
    @Log(title = "订单主", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstOrderMain jstOrderMain)
    {
        return toAjax(jstOrderMainService.insertJstOrderMain(jstOrderMain));
    }

    /**
     * 修改订单主
     */
    @PreAuthorize("@ss.hasPermi('system:jst_order_main:edit')")
    @Log(title = "订单主", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstOrderMain jstOrderMain)
    {
        return toAjax(jstOrderMainService.updateJstOrderMain(jstOrderMain));
    }

    /**
     * 删除订单主
     */
    @PreAuthorize("@ss.hasPermi('system:jst_order_main:remove')")
    @Log(title = "订单主", businessType = BusinessType.DELETE)
	@DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds)
    {
        return toAjax(jstOrderMainService.deleteJstOrderMainByOrderIds(orderIds));
    }
}
