package com.ruoyi.jst.event.controller.partner;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.common.annotation.PartnerDataScope;
import com.ruoyi.jst.common.controller.BasePartnerController;
import com.ruoyi.jst.event.dto.CertBatchGrantReqDTO;
import com.ruoyi.jst.event.dto.CertQueryReqDTO;
import com.ruoyi.jst.event.dto.CertSubmitReviewReqDTO;
import com.ruoyi.jst.event.dto.CertTemplateReqDTO;
import com.ruoyi.jst.event.service.PartnerCertService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

/**
 * 赛事方证书管理 Controller。
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/partner/cert")
public class PartnerCertController extends BasePartnerController {

    @Autowired
    private PartnerCertService partnerCertService;

    /**
     * 以 JSON 登记证书模板。
     *
     * @param req 模板请求
     * @return 模板ID
     */
    @PreAuthorize("@ss.hasPermi('jst:event:cert_template:add')")
    @PostMapping(value = "/template", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AjaxResult saveTemplate(@Valid @RequestBody CertTemplateReqDTO req) {
        Long templateId = partnerCertService.saveTemplate(req, null);
        return AjaxResult.success(Collections.singletonMap("templateId", templateId));
    }

    /**
     * 上传模板底图并登记证书模板。
     *
     * @param templateName 模板名称
     * @param bgImage      已有底图地址
     * @param layoutJson   布局 JSON
     * @param file         模板底图文件
     * @return 模板ID
     */
    @PreAuthorize("@ss.hasPermi('jst:event:cert_template:add')")
    @PostMapping(value = "/template", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult uploadTemplate(@RequestParam("templateName") String templateName,
                                     @RequestParam(value = "bgImage", required = false) String bgImage,
                                     @RequestParam(value = "layoutJson", required = false) String layoutJson,
                                     @RequestParam(value = "file", required = false) MultipartFile file) {
        CertTemplateReqDTO req = new CertTemplateReqDTO();
        req.setTemplateName(templateName);
        req.setBgImage(bgImage);
        req.setLayoutJson(layoutJson);
        Long templateId = partnerCertService.saveTemplate(req, file);
        return AjaxResult.success(Collections.singletonMap("templateId", templateId));
    }

    /**
     * 查询证书模板列表。
     *
     * @return 模板列表
     */
    @PreAuthorize("@ss.hasPermi('jst:event:cert_template:list')")
    @GetMapping("/template/list")
    public AjaxResult templateList() {
        return AjaxResult.success(partnerCertService.listTemplates());
    }

    /**
     * 查询证书列表，SQL 层追加赛事方数据范围。
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @PreAuthorize("@ss.hasPermi('jst:event:cert_record:list')")
    @PartnerDataScope(deptAlias = "c")
    @GetMapping("/list")
    public TableDataInfo list(@Valid CertQueryReqDTO query) {
        startPage();
        return getDataTable(partnerCertService.listCerts(query));
    }

    /**
     * 按已发布成绩批量生成证书草稿。
     *
     * @param req 生成请求
     * @return 生成结果
     */
    @PreAuthorize("@ss.hasPermi('jst:event:cert_record:add')")
    @PostMapping("/batch-grant")
    public AjaxResult batchGrant(@Valid @RequestBody CertBatchGrantReqDTO req) {
        return AjaxResult.success(partnerCertService.batchGrant(req));
    }

    /**
     * 批量提交证书审核，遵循 SM-20。
     *
     * @param req 提审请求
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('jst:event:cert_record:edit')")
    @PostMapping("/submit-review")
    public AjaxResult submitReview(@Valid @RequestBody CertSubmitReviewReqDTO req) {
        partnerCertService.submitReview(req);
        return AjaxResult.success();
    }

    /**
     * 生成证书预览图。
     *
     * @param certId 证书ID
     * @return 预览图
     */
    @PreAuthorize("@ss.hasPermi('jst:event:cert_record:query')")
    @GetMapping("/{certId}/preview")
    public AjaxResult preview(@PathVariable("certId") Long certId) {
        return AjaxResult.success(partnerCertService.preview(certId));
    }
}
