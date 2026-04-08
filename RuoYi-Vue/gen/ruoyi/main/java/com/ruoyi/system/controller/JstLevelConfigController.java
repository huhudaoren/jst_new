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
import com.ruoyi.system.domain.JstLevelConfig;
import com.ruoyi.system.service.IJstLevelConfigService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 等级配置Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_level_config")
public class JstLevelConfigController extends BaseController
{
    @Autowired
    private IJstLevelConfigService jstLevelConfigService;

    /**
     * 查询等级配置列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_level_config:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstLevelConfig jstLevelConfig)
    {
        startPage();
        List<JstLevelConfig> list = jstLevelConfigService.selectJstLevelConfigList(jstLevelConfig);
        return getDataTable(list);
    }

    /**
     * 导出等级配置列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_level_config:export')")
    @Log(title = "等级配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstLevelConfig jstLevelConfig)
    {
        List<JstLevelConfig> list = jstLevelConfigService.selectJstLevelConfigList(jstLevelConfig);
        ExcelUtil<JstLevelConfig> util = new ExcelUtil<JstLevelConfig>(JstLevelConfig.class);
        util.exportExcel(response, list, "等级配置数据");
    }

    /**
     * 获取等级配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_level_config:query')")
    @GetMapping(value = "/{levelId}")
    public AjaxResult getInfo(@PathVariable("levelId") Long levelId)
    {
        return success(jstLevelConfigService.selectJstLevelConfigByLevelId(levelId));
    }

    /**
     * 新增等级配置
     */
    @PreAuthorize("@ss.hasPermi('system:jst_level_config:add')")
    @Log(title = "等级配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstLevelConfig jstLevelConfig)
    {
        return toAjax(jstLevelConfigService.insertJstLevelConfig(jstLevelConfig));
    }

    /**
     * 修改等级配置
     */
    @PreAuthorize("@ss.hasPermi('system:jst_level_config:edit')")
    @Log(title = "等级配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstLevelConfig jstLevelConfig)
    {
        return toAjax(jstLevelConfigService.updateJstLevelConfig(jstLevelConfig));
    }

    /**
     * 删除等级配置
     */
    @PreAuthorize("@ss.hasPermi('system:jst_level_config:remove')")
    @Log(title = "等级配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{levelIds}")
    public AjaxResult remove(@PathVariable Long[] levelIds)
    {
        return toAjax(jstLevelConfigService.deleteJstLevelConfigByLevelIds(levelIds));
    }
}
