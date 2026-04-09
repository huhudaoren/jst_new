package com.ruoyi.jst.marketing.controller.admin;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.marketing.dto.RightsIssueDTO;
import com.ruoyi.jst.marketing.dto.RightsTemplateSaveDTO;
import com.ruoyi.jst.marketing.service.RightsTemplateService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rights template admin controller.
 */
@Validated
@RestController
@RequestMapping("/jst/marketing/rights/template")
public class RightsTemplateAdminController extends BaseController {

    @Autowired
    private RightsTemplateService rightsTemplateService;

    @PreAuthorize("@ss.hasPermi('jst:marketing:rights:list')")
    @GetMapping
    public TableDataInfo list(@RequestParam(value = "rightsName", required = false) String rightsName,
                              @RequestParam(value = "rightsType", required = false) String rightsType,
                              @RequestParam(value = "status", required = false) Integer status) {
        startPage();
        return getDataTable(rightsTemplateService.selectAdminList(rightsName, rightsType, status));
    }

    @PreAuthorize("@ss.hasPermi('jst:marketing:rights:query')")
    @GetMapping("/{rightsTemplateId}")
    public AjaxResult detail(@PathVariable("rightsTemplateId") Long rightsTemplateId) {
        return AjaxResult.success(rightsTemplateService.getAdminDetail(rightsTemplateId));
    }

    @PreAuthorize("@ss.hasPermi('jst:marketing:rights:add')")
    @PostMapping
    public AjaxResult create(@Valid @RequestBody RightsTemplateSaveDTO req) {
        return AjaxResult.success(rightsTemplateService.create(req));
    }

    @PreAuthorize("@ss.hasPermi('jst:marketing:rights:edit')")
    @PutMapping
    public AjaxResult update(@Valid @RequestBody RightsTemplateSaveDTO req) {
        rightsTemplateService.update(req);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('jst:marketing:rights:remove')")
    @DeleteMapping("/{rightsTemplateId}")
    public AjaxResult remove(@PathVariable("rightsTemplateId") Long rightsTemplateId) {
        rightsTemplateService.delete(rightsTemplateId);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('jst:marketing:rights:publish')")
    @PostMapping("/{rightsTemplateId}/publish")
    public AjaxResult publish(@PathVariable("rightsTemplateId") Long rightsTemplateId) {
        rightsTemplateService.publish(rightsTemplateId);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('jst:marketing:rights:offline')")
    @PostMapping("/{rightsTemplateId}/offline")
    public AjaxResult offline(@PathVariable("rightsTemplateId") Long rightsTemplateId) {
        rightsTemplateService.offline(rightsTemplateId);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('jst:marketing:rights:issue')")
    @PostMapping("/{rightsTemplateId}/issue")
    public AjaxResult issue(@PathVariable("rightsTemplateId") Long rightsTemplateId,
                            @Valid @RequestBody RightsIssueDTO req) {
        return AjaxResult.success(rightsTemplateService.issue(rightsTemplateId, req));
    }
}
