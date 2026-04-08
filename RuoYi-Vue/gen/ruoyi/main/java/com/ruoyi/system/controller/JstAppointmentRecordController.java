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
import com.ruoyi.system.domain.JstAppointmentRecord;
import com.ruoyi.system.service.IJstAppointmentRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 个人预约记录Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_appointment_record")
public class JstAppointmentRecordController extends BaseController
{
    @Autowired
    private IJstAppointmentRecordService jstAppointmentRecordService;

    /**
     * 查询个人预约记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_appointment_record:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstAppointmentRecord jstAppointmentRecord)
    {
        startPage();
        List<JstAppointmentRecord> list = jstAppointmentRecordService.selectJstAppointmentRecordList(jstAppointmentRecord);
        return getDataTable(list);
    }

    /**
     * 导出个人预约记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_appointment_record:export')")
    @Log(title = "个人预约记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstAppointmentRecord jstAppointmentRecord)
    {
        List<JstAppointmentRecord> list = jstAppointmentRecordService.selectJstAppointmentRecordList(jstAppointmentRecord);
        ExcelUtil<JstAppointmentRecord> util = new ExcelUtil<JstAppointmentRecord>(JstAppointmentRecord.class);
        util.exportExcel(response, list, "个人预约记录数据");
    }

    /**
     * 获取个人预约记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_appointment_record:query')")
    @GetMapping(value = "/{appointmentId}")
    public AjaxResult getInfo(@PathVariable("appointmentId") Long appointmentId)
    {
        return success(jstAppointmentRecordService.selectJstAppointmentRecordByAppointmentId(appointmentId));
    }

    /**
     * 新增个人预约记录
     */
    @PreAuthorize("@ss.hasPermi('system:jst_appointment_record:add')")
    @Log(title = "个人预约记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstAppointmentRecord jstAppointmentRecord)
    {
        return toAjax(jstAppointmentRecordService.insertJstAppointmentRecord(jstAppointmentRecord));
    }

    /**
     * 修改个人预约记录
     */
    @PreAuthorize("@ss.hasPermi('system:jst_appointment_record:edit')")
    @Log(title = "个人预约记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstAppointmentRecord jstAppointmentRecord)
    {
        return toAjax(jstAppointmentRecordService.updateJstAppointmentRecord(jstAppointmentRecord));
    }

    /**
     * 删除个人预约记录
     */
    @PreAuthorize("@ss.hasPermi('system:jst_appointment_record:remove')")
    @Log(title = "个人预约记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{appointmentIds}")
    public AjaxResult remove(@PathVariable Long[] appointmentIds)
    {
        return toAjax(jstAppointmentRecordService.deleteJstAppointmentRecordByAppointmentIds(appointmentIds));
    }
}
