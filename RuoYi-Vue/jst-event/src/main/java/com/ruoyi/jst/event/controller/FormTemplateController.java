package com.ruoyi.jst.event.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.common.scope.PartnerScope;
import com.ruoyi.jst.event.dto.AuditReqDTO;
import com.ruoyi.jst.event.dto.FormTemplateQueryReqDTO;
import com.ruoyi.jst.event.dto.FormTemplateSaveReqDTO;
import com.ruoyi.jst.event.service.FormTemplateService;
import com.ruoyi.jst.event.vo.FormTemplateDetailVO;
import com.ruoyi.jst.event.vo.FormTemplateListVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * 动态表单模板后台 Controller。
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/event/form/template")
public class FormTemplateController extends BaseController {

    @Autowired
    private FormTemplateService formTemplateService;

    /**
     * 创建或更新动态表单模板。
     *
     * @param req 保存请求
     * @return 模板ID
     * @关联表 jst_enroll_form_template
     * @关联状态机 SM-25
     * @关联权限 jst:event:formTemplate:edit
     */
    @PreAuthorize("@ss.hasPermi('jst:event:formTemplate:edit')")
    @PostMapping("/save")
    public AjaxResult save(@Valid @RequestBody FormTemplateSaveReqDTO req) {
        Long templateId = formTemplateService.save(req);
        return AjaxResult.success(Collections.singletonMap("templateId", templateId));
    }

    /**
     * 提交动态表单模板审核。
     *
     * @param templateId 模板ID
     * @return 操作结果
     * @关联表 jst_enroll_form_template
     * @关联状态机 SM-25
     * @关联权限 jst:event:formTemplate:edit
     */
    @PreAuthorize("@ss.hasPermi('jst:event:formTemplate:edit')")
    @PostMapping("/{templateId}/submit")
    public AjaxResult submit(@PathVariable("templateId") Long templateId) {
        formTemplateService.submit(templateId);
        return AjaxResult.success();
    }

    /**
     * 审核通过动态表单模板。
     *
     * @param templateId 模板ID
     * @param req        审核请求
     * @return 操作结果
     * @关联表 jst_enroll_form_template
     * @关联状态机 SM-25
     * @关联权限 jst:event:formTemplate:audit
     */
    @PreAuthorize("@ss.hasPermi('jst:event:formTemplate:audit')")
    @PostMapping("/{templateId}/audit/approve")
    public AjaxResult approve(@PathVariable("templateId") Long templateId, @Valid @RequestBody AuditReqDTO req) {
        formTemplateService.approve(templateId, req);
        return AjaxResult.success();
    }

    /**
     * 审核驳回动态表单模板。
     *
     * @param templateId 模板ID
     * @param req        审核请求
     * @return 操作结果
     * @关联表 jst_enroll_form_template
     * @关联状态机 SM-25
     * @关联权限 jst:event:formTemplate:audit
     */
    @PreAuthorize("@ss.hasPermi('jst:event:formTemplate:audit')")
    @PostMapping("/{templateId}/audit/reject")
    public AjaxResult reject(@PathVariable("templateId") Long templateId,
                             @Validated(AuditReqDTO.Reject.class) @RequestBody AuditReqDTO req) {
        formTemplateService.reject(templateId, req);
        return AjaxResult.success();
    }

    /**
     * 查询动态表单模板列表。
     *
     * @param query 查询条件
     * @return 分页结果
     * @关联表 jst_enroll_form_template
     * @关联状态机 SM-25
     * @关联权限 jst:event:formTemplate:edit / jst:event:formTemplate:audit
     */
    @PreAuthorize("@ss.hasAnyPermi('jst:event:formTemplate:edit,jst:event:formTemplate:audit')")
    @PartnerScope(field = "ownerId")
    @GetMapping("/list")
    public TableDataInfo list(@Valid FormTemplateQueryReqDTO query) {
        startPage();
        List<FormTemplateListVO> list = formTemplateService.selectList(query);
        return getDataTable(list);
    }

    /**
     * 查询动态表单模板详情。
     *
     * @param templateId 模板ID
     * @return 详情结果
     * @关联表 jst_enroll_form_template
     * @关联状态机 SM-25
     * @关联权限 jst:event:formTemplate:edit / jst:event:formTemplate:audit
     */
    @PreAuthorize("@ss.hasAnyPermi('jst:event:formTemplate:edit,jst:event:formTemplate:audit')")
    @GetMapping("/{templateId}")
    public AjaxResult detail(@PathVariable("templateId") Long templateId) {
        FormTemplateDetailVO vo = formTemplateService.getDetail(templateId);
        return AjaxResult.success(vo);
    }
}
