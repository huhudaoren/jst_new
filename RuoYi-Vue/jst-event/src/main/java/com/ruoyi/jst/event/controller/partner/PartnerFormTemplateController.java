package com.ruoyi.jst.event.controller.partner;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.common.controller.BasePartnerController;
import com.ruoyi.jst.event.dto.FormTemplateQueryReqDTO;
import com.ruoyi.jst.event.service.FormTemplateService;
import com.ruoyi.jst.event.vo.FormTemplateListVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 赛事方可用表单模板包装 Controller。
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/partner/form-template")
public class PartnerFormTemplateController extends BasePartnerController {

    @Autowired
    private FormTemplateService formTemplateService;

    /**
     * 查询赛事方可用表单模板列表（公共模板 + 当前赛事方模板）。
     *
     * @param query 查询条件
     * @return 分页结果
     * @关联表 jst_enroll_form_template
     * @关联状态机 SM-25
     * @关联权限 hasRole('jst_partner')
     */
    @PreAuthorize("@ss.hasRole('jst_partner')")
    @GetMapping("/list")
    public TableDataInfo list(@Valid FormTemplateQueryReqDTO query) {
        startPage();
        List<FormTemplateListVO> list = formTemplateService.selectPartnerAvailableList(query, currentPartnerId());
        return getDataTable(list);
    }
}
