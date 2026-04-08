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
import com.ruoyi.jst.marketing.domain.JstCouponIssueBatch;
import com.ruoyi.jst.marketing.service.IJstCouponIssueBatchService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 发券批次Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/jst/marketing/jst_coupon_issue_batch")
public class JstCouponIssueBatchController extends BaseController
{
    @Autowired
    private IJstCouponIssueBatchService jstCouponIssueBatchService;

    /**
     * 查询发券批次列表
     */
    @PreAuthorize("@ss.hasPermi('jst:marketing:coupon_issue_batch:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstCouponIssueBatch jstCouponIssueBatch)
    {
        startPage();
        List<JstCouponIssueBatch> list = jstCouponIssueBatchService.selectJstCouponIssueBatchList(jstCouponIssueBatch);
        return getDataTable(list);
    }

    /**
     * 导出发券批次列表
     */
    @PreAuthorize("@ss.hasPermi('jst:marketing:coupon_issue_batch:export')")
    @Log(title = "发券批次", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstCouponIssueBatch jstCouponIssueBatch)
    {
        List<JstCouponIssueBatch> list = jstCouponIssueBatchService.selectJstCouponIssueBatchList(jstCouponIssueBatch);
        ExcelUtil<JstCouponIssueBatch> util = new ExcelUtil<JstCouponIssueBatch>(JstCouponIssueBatch.class);
        util.exportExcel(response, list, "发券批次数据");
    }

    /**
     * 获取发券批次详细信息
     */
    @PreAuthorize("@ss.hasPermi('jst:marketing:coupon_issue_batch:query')")
    @GetMapping(value = "/{batchId}")
    public AjaxResult getInfo(@PathVariable("batchId") Long batchId)
    {
        return success(jstCouponIssueBatchService.selectJstCouponIssueBatchByBatchId(batchId));
    }

    /**
     * 新增发券批次
     */
    @PreAuthorize("@ss.hasPermi('jst:marketing:coupon_issue_batch:add')")
    @Log(title = "发券批次", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstCouponIssueBatch jstCouponIssueBatch)
    {
        return toAjax(jstCouponIssueBatchService.insertJstCouponIssueBatch(jstCouponIssueBatch));
    }

    /**
     * 修改发券批次
     */
    @PreAuthorize("@ss.hasPermi('jst:marketing:coupon_issue_batch:edit')")
    @Log(title = "发券批次", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstCouponIssueBatch jstCouponIssueBatch)
    {
        return toAjax(jstCouponIssueBatchService.updateJstCouponIssueBatch(jstCouponIssueBatch));
    }

    /**
     * 删除发券批次
     */
    @PreAuthorize("@ss.hasPermi('jst:marketing:coupon_issue_batch:remove')")
    @Log(title = "发券批次", businessType = BusinessType.DELETE)
	@DeleteMapping("/{batchIds}")
    public AjaxResult remove(@PathVariable Long[] batchIds)
    {
        return toAjax(jstCouponIssueBatchService.deleteJstCouponIssueBatchByBatchIds(batchIds));
    }
}
