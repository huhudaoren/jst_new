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
import com.ruoyi.system.domain.JstMallGoods;
import com.ruoyi.system.service.IJstMallGoodsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 积分商城商品Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_mall_goods")
public class JstMallGoodsController extends BaseController
{
    @Autowired
    private IJstMallGoodsService jstMallGoodsService;

    /**
     * 查询积分商城商品列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_mall_goods:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstMallGoods jstMallGoods)
    {
        startPage();
        List<JstMallGoods> list = jstMallGoodsService.selectJstMallGoodsList(jstMallGoods);
        return getDataTable(list);
    }

    /**
     * 导出积分商城商品列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_mall_goods:export')")
    @Log(title = "积分商城商品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstMallGoods jstMallGoods)
    {
        List<JstMallGoods> list = jstMallGoodsService.selectJstMallGoodsList(jstMallGoods);
        ExcelUtil<JstMallGoods> util = new ExcelUtil<JstMallGoods>(JstMallGoods.class);
        util.exportExcel(response, list, "积分商城商品数据");
    }

    /**
     * 获取积分商城商品详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_mall_goods:query')")
    @GetMapping(value = "/{goodsId}")
    public AjaxResult getInfo(@PathVariable("goodsId") Long goodsId)
    {
        return success(jstMallGoodsService.selectJstMallGoodsByGoodsId(goodsId));
    }

    /**
     * 新增积分商城商品
     */
    @PreAuthorize("@ss.hasPermi('system:jst_mall_goods:add')")
    @Log(title = "积分商城商品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstMallGoods jstMallGoods)
    {
        return toAjax(jstMallGoodsService.insertJstMallGoods(jstMallGoods));
    }

    /**
     * 修改积分商城商品
     */
    @PreAuthorize("@ss.hasPermi('system:jst_mall_goods:edit')")
    @Log(title = "积分商城商品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstMallGoods jstMallGoods)
    {
        return toAjax(jstMallGoodsService.updateJstMallGoods(jstMallGoods));
    }

    /**
     * 删除积分商城商品
     */
    @PreAuthorize("@ss.hasPermi('system:jst_mall_goods:remove')")
    @Log(title = "积分商城商品", businessType = BusinessType.DELETE)
	@DeleteMapping("/{goodsIds}")
    public AjaxResult remove(@PathVariable Long[] goodsIds)
    {
        return toAjax(jstMallGoodsService.deleteJstMallGoodsByGoodsIds(goodsIds));
    }
}
