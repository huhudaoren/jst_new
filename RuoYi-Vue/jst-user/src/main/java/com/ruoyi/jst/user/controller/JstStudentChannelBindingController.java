package com.ruoyi.jst.user.controller;

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
import com.ruoyi.jst.user.domain.JstStudentChannelBinding;
import com.ruoyi.jst.user.service.IJstStudentChannelBindingService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 学生-渠道方绑定关系（同一user同时仅1条active）Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/jst/user/jst_student_channel_binding")
public class JstStudentChannelBindingController extends BaseController
{
    @Autowired
    private IJstStudentChannelBindingService jstStudentChannelBindingService;

    /**
     * 查询学生-渠道方绑定关系（同一user同时仅1条active）列表
     */
    @PreAuthorize("@ss.hasPermi('jst:user:student_channel_binding:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstStudentChannelBinding jstStudentChannelBinding)
    {
        startPage();
        List<JstStudentChannelBinding> list = jstStudentChannelBindingService.selectJstStudentChannelBindingList(jstStudentChannelBinding);
        return getDataTable(list);
    }

    /**
     * 导出学生-渠道方绑定关系（同一user同时仅1条active）列表
     */
    @PreAuthorize("@ss.hasPermi('jst:user:student_channel_binding:export')")
    @Log(title = "学生-渠道方绑定关系（同一user同时仅1条active）", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstStudentChannelBinding jstStudentChannelBinding)
    {
        List<JstStudentChannelBinding> list = jstStudentChannelBindingService.selectJstStudentChannelBindingList(jstStudentChannelBinding);
        ExcelUtil<JstStudentChannelBinding> util = new ExcelUtil<JstStudentChannelBinding>(JstStudentChannelBinding.class);
        util.exportExcel(response, list, "学生-渠道方绑定关系（同一user同时仅1条active）数据");
    }

    /**
     * 获取学生-渠道方绑定关系（同一user同时仅1条active）详细信息
     */
    @PreAuthorize("@ss.hasPermi('jst:user:student_channel_binding:query')")
    @GetMapping(value = "/{bindingId}")
    public AjaxResult getInfo(@PathVariable("bindingId") Long bindingId)
    {
        return success(jstStudentChannelBindingService.selectJstStudentChannelBindingByBindingId(bindingId));
    }

    /**
     * 新增学生-渠道方绑定关系（同一user同时仅1条active）
     */
    @PreAuthorize("@ss.hasPermi('jst:user:student_channel_binding:add')")
    @Log(title = "学生-渠道方绑定关系（同一user同时仅1条active）", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstStudentChannelBinding jstStudentChannelBinding)
    {
        return toAjax(jstStudentChannelBindingService.insertJstStudentChannelBinding(jstStudentChannelBinding));
    }

    /**
     * 修改学生-渠道方绑定关系（同一user同时仅1条active）
     */
    @PreAuthorize("@ss.hasPermi('jst:user:student_channel_binding:edit')")
    @Log(title = "学生-渠道方绑定关系（同一user同时仅1条active）", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstStudentChannelBinding jstStudentChannelBinding)
    {
        return toAjax(jstStudentChannelBindingService.updateJstStudentChannelBinding(jstStudentChannelBinding));
    }

    /**
     * 删除学生-渠道方绑定关系（同一user同时仅1条active）
     */
    @PreAuthorize("@ss.hasPermi('jst:user:student_channel_binding:remove')")
    @Log(title = "学生-渠道方绑定关系（同一user同时仅1条active）", businessType = BusinessType.DELETE)
	@DeleteMapping("/{bindingIds}")
    public AjaxResult remove(@PathVariable Long[] bindingIds)
    {
        return toAjax(jstStudentChannelBindingService.deleteJstStudentChannelBindingByBindingIds(bindingIds));
    }
}
