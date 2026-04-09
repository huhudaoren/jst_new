package com.ruoyi.jst.points.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.points.dto.GoodsQueryReqDTO;
import com.ruoyi.jst.points.dto.GoodsSaveReqDTO;
import com.ruoyi.jst.points.service.MallGoodsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台商城商品 Controller。
 */
@Validated
@RestController
@RequestMapping("/jst/points/mall/goods")
public class MallGoodsAdminController extends BaseController {

    @Autowired
    private MallGoodsService mallGoodsService;

    @PreAuthorize("@ss.hasPermi('jst:points:mall:goods:list')")
    @GetMapping
    public TableDataInfo list(GoodsQueryReqDTO query) {
        startPage();
        return getDataTable(mallGoodsService.selectAdminList(query));
    }

    @PreAuthorize("@ss.hasPermi('jst:points:mall:goods:list')")
    @GetMapping("/{goodsId}")
    public AjaxResult detail(@PathVariable("goodsId") Long goodsId) {
        return AjaxResult.success(mallGoodsService.getDetail(goodsId));
    }

    @PreAuthorize("@ss.hasPermi('jst:points:mall:goods:add')")
    @PostMapping
    public AjaxResult add(@Valid @RequestBody GoodsSaveReqDTO req) {
        return AjaxResult.success(mallGoodsService.save(req));
    }

    @PreAuthorize("@ss.hasPermi('jst:points:mall:goods:edit')")
    @PutMapping
    public AjaxResult edit(@Valid @RequestBody GoodsSaveReqDTO req) {
        return AjaxResult.success(mallGoodsService.save(req));
    }

    @PreAuthorize("@ss.hasPermi('jst:points:mall:goods:publish')")
    @PostMapping("/{goodsId}/publish")
    public AjaxResult publish(@PathVariable("goodsId") Long goodsId) {
        mallGoodsService.publish(goodsId);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('jst:points:mall:goods:publish')")
    @PostMapping("/{goodsId}/offline")
    public AjaxResult offline(@PathVariable("goodsId") Long goodsId) {
        mallGoodsService.offline(goodsId);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('jst:points:mall:goods:remove')")
    @DeleteMapping("/{goodsId}")
    public AjaxResult remove(@PathVariable("goodsId") Long goodsId) {
        mallGoodsService.remove(goodsId);
        return AjaxResult.success();
    }
}
