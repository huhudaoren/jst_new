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
import com.ruoyi.system.domain.JstContractRecord;
import com.ruoyi.system.service.IJstContractRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 合同记录Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_contract_record")
public class JstContractRecordController extends BaseController
{
    @Autowired
    private IJstContractRecordService jstContractRecordService;

    /**
     * 查询合同记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_contract_record:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstContractRecord jstContractRecord)
    {
        startPage();
        List<JstContractRecord> list = jstContractRecordService.selectJstContractRecordList(jstContractRecord);
        return getDataTable(list);
    }

    /**
     * 导出合同记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_contract_record:export')")
    @Log(title = "合同记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstContractRecord jstContractRecord)
    {
        List<JstContractRecord> list = jstContractRecordService.selectJstContractRecordList(jstContractRecord);
        ExcelUtil<JstContractRecord> util = new ExcelUtil<JstContractRecord>(JstContractRecord.class);
        util.exportExcel(response, list, "合同记录数据");
    }

    /**
     * 获取合同记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_contract_record:query')")
    @GetMapping(value = "/{contractId}")
    public AjaxResult getInfo(@PathVariable("contractId") Long contractId)
    {
        return success(jstContractRecordService.selectJstContractRecordByContractId(contractId));
    }

    /**
     * 新增合同记录
     */
    @PreAuthorize("@ss.hasPermi('system:jst_contract_record:add')")
    @Log(title = "合同记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstContractRecord jstContractRecord)
    {
        return toAjax(jstContractRecordService.insertJstContractRecord(jstContractRecord));
    }

    /**
     * 修改合同记录
     */
    @PreAuthorize("@ss.hasPermi('system:jst_contract_record:edit')")
    @Log(title = "合同记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstContractRecord jstContractRecord)
    {
        return toAjax(jstContractRecordService.updateJstContractRecord(jstContractRecord));
    }

    /**
     * 删除合同记录
     */
    @PreAuthorize("@ss.hasPermi('system:jst_contract_record:remove')")
    @Log(title = "合同记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{contractIds}")
    public AjaxResult remove(@PathVariable Long[] contractIds)
    {
        return toAjax(jstContractRecordService.deleteJstContractRecordByContractIds(contractIds));
    }
}
