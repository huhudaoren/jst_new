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
import com.ruoyi.jst.order.domain.JstTeamAppointment;
import com.ruoyi.jst.order.service.IJstTeamAppointmentService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 团队预约主记录Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/jst/order/jst_team_appointment")
public class JstTeamAppointmentController extends BaseController
{
    @Autowired
    private IJstTeamAppointmentService jstTeamAppointmentService;

    /**
     * 查询团队预约主记录列表
     */
    @PreAuthorize("@ss.hasPermi('jst:order:team_appointment:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstTeamAppointment jstTeamAppointment)
    {
        startPage();
        List<JstTeamAppointment> list = jstTeamAppointmentService.selectJstTeamAppointmentList(jstTeamAppointment);
        return getDataTable(list);
    }

    /**
     * 导出团队预约主记录列表
     */
    @PreAuthorize("@ss.hasPermi('jst:order:team_appointment:export')")
    @Log(title = "团队预约主记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstTeamAppointment jstTeamAppointment)
    {
        List<JstTeamAppointment> list = jstTeamAppointmentService.selectJstTeamAppointmentList(jstTeamAppointment);
        ExcelUtil<JstTeamAppointment> util = new ExcelUtil<JstTeamAppointment>(JstTeamAppointment.class);
        util.exportExcel(response, list, "团队预约主记录数据");
    }

    /**
     * 获取团队预约主记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('jst:order:team_appointment:query')")
    @GetMapping(value = "/{teamAppointmentId}")
    public AjaxResult getInfo(@PathVariable("teamAppointmentId") Long teamAppointmentId)
    {
        return success(jstTeamAppointmentService.selectJstTeamAppointmentByTeamAppointmentId(teamAppointmentId));
    }

    /**
     * 新增团队预约主记录
     */
    @PreAuthorize("@ss.hasPermi('jst:order:team_appointment:add')")
    @Log(title = "团队预约主记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstTeamAppointment jstTeamAppointment)
    {
        return toAjax(jstTeamAppointmentService.insertJstTeamAppointment(jstTeamAppointment));
    }

    /**
     * 修改团队预约主记录
     */
    @PreAuthorize("@ss.hasPermi('jst:order:team_appointment:edit')")
    @Log(title = "团队预约主记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstTeamAppointment jstTeamAppointment)
    {
        return toAjax(jstTeamAppointmentService.updateJstTeamAppointment(jstTeamAppointment));
    }

    /**
     * 删除团队预约主记录
     */
    @PreAuthorize("@ss.hasPermi('jst:order:team_appointment:remove')")
    @Log(title = "团队预约主记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{teamAppointmentIds}")
    public AjaxResult remove(@PathVariable Long[] teamAppointmentIds)
    {
        return toAjax(jstTeamAppointmentService.deleteJstTeamAppointmentByTeamAppointmentIds(teamAppointmentIds));
    }
}
