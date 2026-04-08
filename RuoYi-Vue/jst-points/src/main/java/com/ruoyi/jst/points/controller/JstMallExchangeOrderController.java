package com.ruoyi.jst.points.controller;

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
import com.ruoyi.jst.points.domain.JstMallExchangeOrder;
import com.ruoyi.jst.points.service.IJstMallExchangeOrderService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 积分商城兑换订单Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/jst/points/jst_mall_exchange_order")
public class JstMallExchangeOrderController extends BaseController
{
    @Autowired
    private IJstMallExchangeOrderService jstMallExchangeOrderService;

    /**
     * 查询积分商城兑换订单列表
     */
    @PreAuthorize("@ss.hasPermi('jst:points:mall_exchange_order:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstMallExchangeOrder jstMallExchangeOrder)
    {
        startPage();
        List<JstMallExchangeOrder> list = jstMallExchangeOrderService.selectJstMallExchangeOrderList(jstMallExchangeOrder);
        return getDataTable(list);
    }

    /**
     * 导出积分商城兑换订单列表
     */
    @PreAuthorize("@ss.hasPermi('jst:points:mall_exchange_order:export')")
    @Log(title = "积分商城兑换订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstMallExchangeOrder jstMallExchangeOrder)
    {
        List<JstMallExchangeOrder> list = jstMallExchangeOrderService.selectJstMallExchangeOrderList(jstMallExchangeOrder);
        ExcelUtil<JstMallExchangeOrder> util = new ExcelUtil<JstMallExchangeOrder>(JstMallExchangeOrder.class);
        util.exportExcel(response, list, "积分商城兑换订单数据");
    }

    /**
     * 获取积分商城兑换订单详细信息
     */
    @PreAuthorize("@ss.hasPermi('jst:points:mall_exchange_order:query')")
    @GetMapping(value = "/{exchangeId}")
    public AjaxResult getInfo(@PathVariable("exchangeId") Long exchangeId)
    {
        return success(jstMallExchangeOrderService.selectJstMallExchangeOrderByExchangeId(exchangeId));
    }

    /**
     * 新增积分商城兑换订单
     */
    @PreAuthorize("@ss.hasPermi('jst:points:mall_exchange_order:add')")
    @Log(title = "积分商城兑换订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstMallExchangeOrder jstMallExchangeOrder)
    {
        return toAjax(jstMallExchangeOrderService.insertJstMallExchangeOrder(jstMallExchangeOrder));
    }

    /**
     * 修改积分商城兑换订单
     */
    @PreAuthorize("@ss.hasPermi('jst:points:mall_exchange_order:edit')")
    @Log(title = "积分商城兑换订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstMallExchangeOrder jstMallExchangeOrder)
    {
        return toAjax(jstMallExchangeOrderService.updateJstMallExchangeOrder(jstMallExchangeOrder));
    }

    /**
     * 删除积分商城兑换订单
     */
    @PreAuthorize("@ss.hasPermi('jst:points:mall_exchange_order:remove')")
    @Log(title = "积分商城兑换订单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{exchangeIds}")
    public AjaxResult remove(@PathVariable Long[] exchangeIds)
    {
        return toAjax(jstMallExchangeOrderService.deleteJstMallExchangeOrderByExchangeIds(exchangeIds));
    }
}
