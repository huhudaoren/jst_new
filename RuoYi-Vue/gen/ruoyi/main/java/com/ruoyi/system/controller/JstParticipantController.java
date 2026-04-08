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
import com.ruoyi.system.domain.JstParticipant;
import com.ruoyi.system.service.IJstParticipantService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 临时参赛档案-允许无正式账号存在Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_participant")
public class JstParticipantController extends BaseController
{
    @Autowired
    private IJstParticipantService jstParticipantService;

    /**
     * 查询临时参赛档案-允许无正式账号存在列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_participant:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstParticipant jstParticipant)
    {
        startPage();
        List<JstParticipant> list = jstParticipantService.selectJstParticipantList(jstParticipant);
        return getDataTable(list);
    }

    /**
     * 导出临时参赛档案-允许无正式账号存在列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_participant:export')")
    @Log(title = "临时参赛档案-允许无正式账号存在", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstParticipant jstParticipant)
    {
        List<JstParticipant> list = jstParticipantService.selectJstParticipantList(jstParticipant);
        ExcelUtil<JstParticipant> util = new ExcelUtil<JstParticipant>(JstParticipant.class);
        util.exportExcel(response, list, "临时参赛档案-允许无正式账号存在数据");
    }

    /**
     * 获取临时参赛档案-允许无正式账号存在详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_participant:query')")
    @GetMapping(value = "/{participantId}")
    public AjaxResult getInfo(@PathVariable("participantId") Long participantId)
    {
        return success(jstParticipantService.selectJstParticipantByParticipantId(participantId));
    }

    /**
     * 新增临时参赛档案-允许无正式账号存在
     */
    @PreAuthorize("@ss.hasPermi('system:jst_participant:add')")
    @Log(title = "临时参赛档案-允许无正式账号存在", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstParticipant jstParticipant)
    {
        return toAjax(jstParticipantService.insertJstParticipant(jstParticipant));
    }

    /**
     * 修改临时参赛档案-允许无正式账号存在
     */
    @PreAuthorize("@ss.hasPermi('system:jst_participant:edit')")
    @Log(title = "临时参赛档案-允许无正式账号存在", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstParticipant jstParticipant)
    {
        return toAjax(jstParticipantService.updateJstParticipant(jstParticipant));
    }

    /**
     * 删除临时参赛档案-允许无正式账号存在
     */
    @PreAuthorize("@ss.hasPermi('system:jst_participant:remove')")
    @Log(title = "临时参赛档案-允许无正式账号存在", businessType = BusinessType.DELETE)
	@DeleteMapping("/{participantIds}")
    public AjaxResult remove(@PathVariable Long[] participantIds)
    {
        return toAjax(jstParticipantService.deleteJstParticipantByParticipantIds(participantIds));
    }
}
