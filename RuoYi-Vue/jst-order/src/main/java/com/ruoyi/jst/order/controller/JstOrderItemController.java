package com.ruoyi.jst.order.controller;

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
import com.ruoyi.jst.order.domain.JstOrderItem;
import com.ruoyi.jst.order.service.IJstOrderItemService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 订单明细（最小核算单元，承载分摊与回滚）Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/jst/order/jst_order_item")
public class JstOrderItemController extends BaseController
{
    @Autowired
    private IJstOrderItemService jstOrderItemService;

    /**
     * 查询订单明细（最小核算单元，承载分摊与回滚）列表
     */
    @PreAuthorize("@ss.hasPermi('jst:order:order_item:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstOrderItem jstOrderItem)
    {
        startPage();
        List<JstOrderItem> list = jstOrderItemService.selectJstOrderItemList(jstOrderItem);
        return getDataTable(list);
    }

    /**
     * 导出订单明细（最小核算单元，承载分摊与回滚）列表
     */
    @PreAuthorize("@ss.hasPermi('jst:order:order_item:export')")
    @Log(title = "订单明细（最小核算单元，承载分摊与回滚）", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstOrderItem jstOrderItem)
    {
        List<JstOrderItem> list = jstOrderItemService.selectJstOrderItemList(jstOrderItem);
        ExcelUtil<JstOrderItem> util = new ExcelUtil<JstOrderItem>(JstOrderItem.class);
        util.exportExcel(response, list, "订单明细（最小核算单元，承载分摊与回滚）数据");
    }

    /**
     * 获取订单明细（最小核算单元，承载分摊与回滚）详细信息
     */
    @PreAuthorize("@ss.hasPermi('jst:order:order_item:query')")
    @GetMapping(value = "/{itemId}")
    public AjaxResult getInfo(@PathVariable("itemId") Long itemId)
    {
        return success(jstOrderItemService.selectJstOrderItemByItemId(itemId));
    }

    /**
     * 新增订单明细（最小核算单元，承载分摊与回滚）
     */
    @PreAuthorize("@ss.hasPermi('jst:order:order_item:add')")
    @Log(title = "订单明细（最小核算单元，承载分摊与回滚）", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstOrderItem jstOrderItem)
    {
        return toAjax(jstOrderItemService.insertJstOrderItem(jstOrderItem));
    }

    /**
     * 修改订单明细（最小核算单元，承载分摊与回滚）
     */
    @PreAuthorize("@ss.hasPermi('jst:order:order_item:edit')")
    @Log(title = "订单明细（最小核算单元，承载分摊与回滚）", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstOrderItem jstOrderItem)
    {
        return toAjax(jstOrderItemService.updateJstOrderItem(jstOrderItem));
    }

    /**
     * 删除订单明细（最小核算单元，承载分摊与回滚）
     */
    @PreAuthorize("@ss.hasPermi('jst:order:order_item:remove')")
    @Log(title = "订单明细（最小核算单元，承载分摊与回滚）", businessType = BusinessType.DELETE)
	@DeleteMapping("/{itemIds}")
    public AjaxResult remove(@PathVariable Long[] itemIds)
    {
        return toAjax(jstOrderItemService.deleteJstOrderItemByItemIds(itemIds));
    }
}
