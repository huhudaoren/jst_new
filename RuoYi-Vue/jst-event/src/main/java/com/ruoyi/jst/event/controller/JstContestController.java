package com.ruoyi.jst.event.controller;

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
import com.ruoyi.jst.event.domain.JstContest;
import com.ruoyi.jst.event.service.IJstContestService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 赛事主Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/jst/event/jst_contest")
public class JstContestController extends BaseController
{
    @Autowired
    private IJstContestService jstContestService;

    /**
     * 查询赛事主列表
     */
    @PreAuthorize("@ss.hasPermi('jst:event:contest:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstContest jstContest)
    {
        startPage();
        List<JstContest> list = jstContestService.selectJstContestList(jstContest);
        return getDataTable(list);
    }

    /**
     * 导出赛事主列表
     */
    @PreAuthorize("@ss.hasPermi('jst:event:contest:export')")
    @Log(title = "赛事主", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstContest jstContest)
    {
        List<JstContest> list = jstContestService.selectJstContestList(jstContest);
        ExcelUtil<JstContest> util = new ExcelUtil<JstContest>(JstContest.class);
        util.exportExcel(response, list, "赛事主数据");
    }

    /**
     * 获取赛事主详细信息
     */
    @PreAuthorize("@ss.hasPermi('jst:event:contest:query')")
    @GetMapping(value = "/{contestId}")
    public AjaxResult getInfo(@PathVariable("contestId") Long contestId)
    {
        return success(jstContestService.selectJstContestByContestId(contestId));
    }

    /**
     * 新增赛事主
     */
    @PreAuthorize("@ss.hasPermi('jst:event:contest:add')")
    @Log(title = "赛事主", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstContest jstContest)
    {
        return toAjax(jstContestService.insertJstContest(jstContest));
    }

    /**
     * 修改赛事主
     */
    @PreAuthorize("@ss.hasPermi('jst:event:contest:edit')")
    @Log(title = "赛事主", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstContest jstContest)
    {
        return toAjax(jstContestService.updateJstContest(jstContest));
    }

    /**
     * 删除赛事主
     */
    @PreAuthorize("@ss.hasPermi('jst:event:contest:remove')")
    @Log(title = "赛事主", businessType = BusinessType.DELETE)
	@DeleteMapping("/{contestIds}")
    public AjaxResult remove(@PathVariable Long[] contestIds)
    {
        return toAjax(jstContestService.deleteJstContestByContestIds(contestIds));
    }
}
