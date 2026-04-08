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
import com.ruoyi.system.domain.JstUser;
import com.ruoyi.system.service.IJstUserService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 用户主-学生/家长/渠道方业务账号Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_user")
public class JstUserController extends BaseController
{
    @Autowired
    private IJstUserService jstUserService;

    /**
     * 查询用户主-学生/家长/渠道方业务账号列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_user:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstUser jstUser)
    {
        startPage();
        List<JstUser> list = jstUserService.selectJstUserList(jstUser);
        return getDataTable(list);
    }

    /**
     * 导出用户主-学生/家长/渠道方业务账号列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_user:export')")
    @Log(title = "用户主-学生/家长/渠道方业务账号", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstUser jstUser)
    {
        List<JstUser> list = jstUserService.selectJstUserList(jstUser);
        ExcelUtil<JstUser> util = new ExcelUtil<JstUser>(JstUser.class);
        util.exportExcel(response, list, "用户主-学生/家长/渠道方业务账号数据");
    }

    /**
     * 获取用户主-学生/家长/渠道方业务账号详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_user:query')")
    @GetMapping(value = "/{userId}")
    public AjaxResult getInfo(@PathVariable("userId") Long userId)
    {
        return success(jstUserService.selectJstUserByUserId(userId));
    }

    /**
     * 新增用户主-学生/家长/渠道方业务账号
     */
    @PreAuthorize("@ss.hasPermi('system:jst_user:add')")
    @Log(title = "用户主-学生/家长/渠道方业务账号", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstUser jstUser)
    {
        return toAjax(jstUserService.insertJstUser(jstUser));
    }

    /**
     * 修改用户主-学生/家长/渠道方业务账号
     */
    @PreAuthorize("@ss.hasPermi('system:jst_user:edit')")
    @Log(title = "用户主-学生/家长/渠道方业务账号", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstUser jstUser)
    {
        return toAjax(jstUserService.updateJstUser(jstUser));
    }

    /**
     * 删除用户主-学生/家长/渠道方业务账号
     */
    @PreAuthorize("@ss.hasPermi('system:jst_user:remove')")
    @Log(title = "用户主-学生/家长/渠道方业务账号", businessType = BusinessType.DELETE)
	@DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds)
    {
        return toAjax(jstUserService.deleteJstUserByUserIds(userIds));
    }
}
