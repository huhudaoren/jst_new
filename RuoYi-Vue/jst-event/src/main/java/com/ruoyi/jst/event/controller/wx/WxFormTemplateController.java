package com.ruoyi.jst.event.controller.wx;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.jst.event.service.FormTemplateService;
import com.ruoyi.jst.event.vo.WxFormTemplateVO;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序端报名模板 Controller。
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/wx/enroll")
public class WxFormTemplateController extends BaseController {

    @Autowired
    private FormTemplateService formTemplateService;

    /**
     * 根据赛事ID读取报名表单模板。
     *
     * @param contestId 赛事ID
     * @return 模板结果
     * @关联表 jst_contest / jst_enroll_form_template
     * @关联状态机 SM-25
     * @关联权限 @Anonymous
     */
    @Anonymous
    @GetMapping("/template")
    public AjaxResult getTemplate(@NotNull(message = "contestId 不能为空") @RequestParam("contestId") Long contestId) {
        WxFormTemplateVO vo = formTemplateService.getWxTemplate(contestId);
        return AjaxResult.success(vo);
    }
}
