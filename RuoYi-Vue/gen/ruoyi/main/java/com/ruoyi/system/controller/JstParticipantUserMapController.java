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
import com.ruoyi.system.domain.JstParticipantUserMap;
import com.ruoyi.system.service.IJstParticipantUserMapService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 参赛档案-正式用户认领映射Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_participant_user_map")
public class JstParticipantUserMapController extends BaseController
{
    @Autowired
    private IJstParticipantUserMapService jstParticipantUserMapService;

    /**
     * 查询参赛档案-正式用户认领映射列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_participant_user_map:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstParticipantUserMap jstParticipantUserMap)
    {
        startPage();
        List<JstParticipantUserMap> list = jstParticipantUserMapService.selectJstParticipantUserMapList(jstParticipantUserMap);
        return getDataTable(list);
    }

    /**
     * 导出参赛档案-正式用户认领映射列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_participant_user_map:export')")
    @Log(title = "参赛档案-正式用户认领映射", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstParticipantUserMap jstParticipantUserMap)
    {
        List<JstParticipantUserMap> list = jstParticipantUserMapService.selectJstParticipantUserMapList(jstParticipantUserMap);
        ExcelUtil<JstParticipantUserMap> util = new ExcelUtil<JstParticipantUserMap>(JstParticipantUserMap.class);
        util.exportExcel(response, list, "参赛档案-正式用户认领映射数据");
    }

    /**
     * 获取参赛档案-正式用户认领映射详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_participant_user_map:query')")
    @GetMapping(value = "/{mapId}")
    public AjaxResult getInfo(@PathVariable("mapId") Long mapId)
    {
        return success(jstParticipantUserMapService.selectJstParticipantUserMapByMapId(mapId));
    }

    /**
     * 新增参赛档案-正式用户认领映射
     */
    @PreAuthorize("@ss.hasPermi('system:jst_participant_user_map:add')")
    @Log(title = "参赛档案-正式用户认领映射", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstParticipantUserMap jstParticipantUserMap)
    {
        return toAjax(jstParticipantUserMapService.insertJstParticipantUserMap(jstParticipantUserMap));
    }

    /**
     * 修改参赛档案-正式用户认领映射
     */
    @PreAuthorize("@ss.hasPermi('system:jst_participant_user_map:edit')")
    @Log(title = "参赛档案-正式用户认领映射", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstParticipantUserMap jstParticipantUserMap)
    {
        return toAjax(jstParticipantUserMapService.updateJstParticipantUserMap(jstParticipantUserMap));
    }

    /**
     * 删除参赛档案-正式用户认领映射
     */
    @PreAuthorize("@ss.hasPermi('system:jst_participant_user_map:remove')")
    @Log(title = "参赛档案-正式用户认领映射", businessType = BusinessType.DELETE)
	@DeleteMapping("/{mapIds}")
    public AjaxResult remove(@PathVariable Long[] mapIds)
    {
        return toAjax(jstParticipantUserMapService.deleteJstParticipantUserMapByMapIds(mapIds));
    }
}
