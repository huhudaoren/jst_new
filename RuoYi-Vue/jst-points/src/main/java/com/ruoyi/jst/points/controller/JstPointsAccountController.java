package com.ruoyi.jst.points.controller;

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
import com.ruoyi.jst.points.domain.JstPointsAccount;
import com.ruoyi.jst.points.service.IJstPointsAccountService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 积分账户Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/jst/points/jst_points_account")
public class JstPointsAccountController extends BaseController
{
    @Autowired
    private IJstPointsAccountService jstPointsAccountService;

    /**
     * 查询积分账户列表
     */
    @PreAuthorize("@ss.hasPermi('jst:points:points_account:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstPointsAccount jstPointsAccount)
    {
        startPage();
        List<JstPointsAccount> list = jstPointsAccountService.selectJstPointsAccountList(jstPointsAccount);
        return getDataTable(list);
    }

    /**
     * 导出积分账户列表
     */
    @PreAuthorize("@ss.hasPermi('jst:points:points_account:export')")
    @Log(title = "积分账户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstPointsAccount jstPointsAccount)
    {
        List<JstPointsAccount> list = jstPointsAccountService.selectJstPointsAccountList(jstPointsAccount);
        ExcelUtil<JstPointsAccount> util = new ExcelUtil<JstPointsAccount>(JstPointsAccount.class);
        util.exportExcel(response, list, "积分账户数据");
    }

    /**
     * 获取积分账户详细信息
     */
    @PreAuthorize("@ss.hasPermi('jst:points:points_account:query')")
    @GetMapping(value = "/{accountId}")
    public AjaxResult getInfo(@PathVariable("accountId") Long accountId)
    {
        return success(jstPointsAccountService.selectJstPointsAccountByAccountId(accountId));
    }

    /**
     * 新增积分账户
     */
    @PreAuthorize("@ss.hasPermi('jst:points:points_account:add')")
    @Log(title = "积分账户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstPointsAccount jstPointsAccount)
    {
        return toAjax(jstPointsAccountService.insertJstPointsAccount(jstPointsAccount));
    }

    /**
     * 修改积分账户
     */
    @PreAuthorize("@ss.hasPermi('jst:points:points_account:edit')")
    @Log(title = "积分账户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstPointsAccount jstPointsAccount)
    {
        return toAjax(jstPointsAccountService.updateJstPointsAccount(jstPointsAccount));
    }

    /**
     * 删除积分账户
     */
    @PreAuthorize("@ss.hasPermi('jst:points:points_account:remove')")
    @Log(title = "积分账户", businessType = BusinessType.DELETE)
	@DeleteMapping("/{accountIds}")
    public AjaxResult remove(@PathVariable Long[] accountIds)
    {
        return toAjax(jstPointsAccountService.deleteJstPointsAccountByAccountIds(accountIds));
    }
}
