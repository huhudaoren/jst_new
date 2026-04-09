package com.ruoyi.jst.points.controller.wx;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.points.dto.GoodsQueryReqDTO;
import com.ruoyi.jst.points.service.MallGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序商城商品 Controller。
 */
@RestController
@RequestMapping("/jst/wx/mall/goods")
public class WxMallGoodsController extends BaseController {

    @Autowired
    private MallGoodsService mallGoodsService;

    @Anonymous
    @GetMapping("/list")
    public TableDataInfo list(GoodsQueryReqDTO query) {
        startPage();
        return getDataTable(mallGoodsService.selectWxList(query));
    }

    @Anonymous
    @GetMapping("/{goodsId}")
    public AjaxResult detail(@PathVariable("goodsId") Long goodsId) {
        return AjaxResult.success(mallGoodsService.getDetail(goodsId));
    }
}
