package com.ruoyi.jst.event.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.jst.event.dto.EnrollDraftDTO;
import com.ruoyi.jst.event.dto.EnrollSubmitDTO;
import com.ruoyi.jst.event.dto.EnrollSupplementDTO;
import com.ruoyi.jst.event.service.EnrollRecordService;
import com.ruoyi.jst.event.vo.EnrollDetailVO;
import com.ruoyi.jst.event.vo.EnrollSubmitResVO;
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

/**
 * 小程序报名记录 Controller。
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/wx/enroll")
public class WxEnrollController extends BaseController {

    @Autowired
    private EnrollRecordService enrollRecordService;

    /**
     * 保存报名草稿。
     *
     * @param req 草稿请求
     * @return 保存结果
     */
    @PreAuthorize("@ss.hasRole('jst_student')")
    @PostMapping("/draft")
    public AjaxResult draft(@Valid @RequestBody EnrollDraftDTO req) {
        EnrollSubmitResVO vo = enrollRecordService.saveDraft(req);
        return AjaxResult.success(vo);
    }

    /**
     * 提交报名。
     *
     * @param req 提交请求
     * @return 提交结果
     */
    @PreAuthorize("@ss.hasRole('jst_student')")
    @PostMapping("/submit")
    public AjaxResult submit(@Valid @RequestBody EnrollSubmitDTO req) {
        EnrollSubmitResVO vo = enrollRecordService.submit(req);
        return AjaxResult.success(vo);
    }

    /**
     * 查询报名详情。
     *
     * @param enrollId 报名 ID
     * @return 详情
     */
    @PreAuthorize("@ss.hasRole('jst_student')")
    @GetMapping("/{enrollId}")
    public AjaxResult detail(@PathVariable("enrollId") Long enrollId) {
        EnrollDetailVO vo = enrollRecordService.getWxDetail(enrollId);
        return AjaxResult.success(vo);
    }

    /**
     * 我的报名列表 (按当前 user_id 过滤)
     * @param auditStatus 可选,审核状态过滤 pending/approved/rejected/supplement
     */
    @PreAuthorize("@ss.hasRole('jst_student')")
    @GetMapping("/my")
    public AjaxResult myList(@org.springframework.web.bind.annotation.RequestParam(required = false) String auditStatus) {
        return AjaxResult.success(enrollRecordService.selectMyList(auditStatus));
    }

    /**
     * 提交补件。
     *
     * @param enrollId 报名 ID
     * @param req      补件请求
     * @return 处理结果
     */
    @PreAuthorize("@ss.hasRole('jst_student')")
    @PostMapping("/{enrollId}/supplement")
    public AjaxResult supplement(@PathVariable("enrollId") Long enrollId,
                                 @Valid @RequestBody EnrollSupplementDTO req) {
        enrollRecordService.supplement(enrollId, req);
        return AjaxResult.success();
    }
}
