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
import com.ruoyi.system.domain.JstAppointmentWriteoffItem;
import com.ruoyi.system.service.IJstAppointmentWriteoffItemService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 核销子项（团队部分核销/到场/礼品/仪式独立流转）Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_appointment_writeoff_item")
public class JstAppointmentWriteoffItemController extends BaseController
{
    @Autowired
    private IJstAppointmentWriteoffItemService jstAppointmentWriteoffItemService;

    /**
     * 查询核销子项（团队部分核销/到场/礼品/仪式独立流转）列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_appointment_writeoff_item:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstAppointmentWriteoffItem jstAppointmentWriteoffItem)
    {
        startPage();
        List<JstAppointmentWriteoffItem> list = jstAppointmentWriteoffItemService.selectJstAppointmentWriteoffItemList(jstAppointmentWriteoffItem);
        return getDataTable(list);
    }

    /**
     * 导出核销子项（团队部分核销/到场/礼品/仪式独立流转）列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_appointment_writeoff_item:export')")
    @Log(title = "核销子项（团队部分核销/到场/礼品/仪式独立流转）", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstAppointmentWriteoffItem jstAppointmentWriteoffItem)
    {
        List<JstAppointmentWriteoffItem> list = jstAppointmentWriteoffItemService.selectJstAppointmentWriteoffItemList(jstAppointmentWriteoffItem);
        ExcelUtil<JstAppointmentWriteoffItem> util = new ExcelUtil<JstAppointmentWriteoffItem>(JstAppointmentWriteoffItem.class);
        util.exportExcel(response, list, "核销子项（团队部分核销/到场/礼品/仪式独立流转）数据");
    }

    /**
     * 获取核销子项（团队部分核销/到场/礼品/仪式独立流转）详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_appointment_writeoff_item:query')")
    @GetMapping(value = "/{writeoffItemId}")
    public AjaxResult getInfo(@PathVariable("writeoffItemId") Long writeoffItemId)
    {
        return success(jstAppointmentWriteoffItemService.selectJstAppointmentWriteoffItemByWriteoffItemId(writeoffItemId));
    }

    /**
     * 新增核销子项（团队部分核销/到场/礼品/仪式独立流转）
     */
    @PreAuthorize("@ss.hasPermi('system:jst_appointment_writeoff_item:add')")
    @Log(title = "核销子项（团队部分核销/到场/礼品/仪式独立流转）", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstAppointmentWriteoffItem jstAppointmentWriteoffItem)
    {
        return toAjax(jstAppointmentWriteoffItemService.insertJstAppointmentWriteoffItem(jstAppointmentWriteoffItem));
    }

    /**
     * 修改核销子项（团队部分核销/到场/礼品/仪式独立流转）
     */
    @PreAuthorize("@ss.hasPermi('system:jst_appointment_writeoff_item:edit')")
    @Log(title = "核销子项（团队部分核销/到场/礼品/仪式独立流转）", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstAppointmentWriteoffItem jstAppointmentWriteoffItem)
    {
        return toAjax(jstAppointmentWriteoffItemService.updateJstAppointmentWriteoffItem(jstAppointmentWriteoffItem));
    }

    /**
     * 删除核销子项（团队部分核销/到场/礼品/仪式独立流转）
     */
    @PreAuthorize("@ss.hasPermi('system:jst_appointment_writeoff_item:remove')")
    @Log(title = "核销子项（团队部分核销/到场/礼品/仪式独立流转）", businessType = BusinessType.DELETE)
	@DeleteMapping("/{writeoffItemIds}")
    public AjaxResult remove(@PathVariable Long[] writeoffItemIds)
    {
        return toAjax(jstAppointmentWriteoffItemService.deleteJstAppointmentWriteoffItemByWriteoffItemIds(writeoffItemIds));
    }
}
