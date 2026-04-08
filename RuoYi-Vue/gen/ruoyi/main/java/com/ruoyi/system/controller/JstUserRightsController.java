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
import com.ruoyi.system.domain.JstUserRights;
import com.ruoyi.system.service.IJstUserRightsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 用户权益持有Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_user_rights")
public class JstUserRightsController extends BaseController
{
    @Autowired
    private IJstUserRightsService jstUserRightsService;

    /**
     * 查询用户权益持有列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_user_rights:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstUserRights jstUserRights)
    {
        startPage();
        List<JstUserRights> list = jstUserRightsService.selectJstUserRightsList(jstUserRights);
        return getDataTable(list);
    }

    /**
     * 导出用户权益持有列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_user_rights:export')")
    @Log(title = "用户权益持有", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstUserRights jstUserRights)
    {
        List<JstUserRights> list = jstUserRightsService.selectJstUserRightsList(jstUserRights);
        ExcelUtil<JstUserRights> util = new ExcelUtil<JstUserRights>(JstUserRights.class);
        util.exportExcel(response, list, "用户权益持有数据");
    }

    /**
     * 获取用户权益持有详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_user_rights:query')")
    @GetMapping(value = "/{userRightsId}")
    public AjaxResult getInfo(@PathVariable("userRightsId") Long userRightsId)
    {
        return success(jstUserRightsService.selectJstUserRightsByUserRightsId(userRightsId));
    }

    /**
     * 新增用户权益持有
     */
    @PreAuthorize("@ss.hasPermi('system:jst_user_rights:add')")
    @Log(title = "用户权益持有", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstUserRights jstUserRights)
    {
        return toAjax(jstUserRightsService.insertJstUserRights(jstUserRights));
    }

    /**
     * 修改用户权益持有
     */
    @PreAuthorize("@ss.hasPermi('system:jst_user_rights:edit')")
    @Log(title = "用户权益持有", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstUserRights jstUserRights)
    {
        return toAjax(jstUserRightsService.updateJstUserRights(jstUserRights));
    }

    /**
     * 删除用户权益持有
     */
    @PreAuthorize("@ss.hasPermi('system:jst_user_rights:remove')")
    @Log(title = "用户权益持有", businessType = BusinessType.DELETE)
	@DeleteMapping("/{userRightsIds}")
    public AjaxResult remove(@PathVariable Long[] userRightsIds)
    {
        return toAjax(jstUserRightsService.deleteJstUserRightsByUserRightsIds(userRightsIds));
    }
}
