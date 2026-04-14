package com.ruoyi.jst.event.controller.partner;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.jst.common.annotation.PartnerDataScope;
import com.ruoyi.jst.common.controller.BasePartnerController;
import com.ruoyi.jst.event.dto.EnrollAuditReqDTO;
import com.ruoyi.jst.event.dto.EnrollBatchAuditReqDTO;
import com.ruoyi.jst.event.dto.EnrollQueryReqDTO;
import com.ruoyi.jst.event.service.EnrollRecordService;
import com.ruoyi.jst.event.vo.EnrollDetailVO;
import com.ruoyi.jst.event.vo.EnrollListVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * 赛事方报名审核包装 Controller。
 * <p>
 * 对外提供任务卡约定的 `/jst/partner/enroll/*` 路由，
 * 内部复用 EnrollRecordService 领域实现。
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/partner/enroll")
public class PartnerEnrollController extends BasePartnerController {

    @Autowired
    private EnrollRecordService enrollRecordService;

    /**
     * 查询赛事方报名列表。
     *
     * @param query 查询条件
     * @return 分页列表
     * @关联表 jst_enroll_record
     * @关联状态机 SM-6
     * @关联权限 hasRole('jst_partner')
     */
    @PreAuthorize("@ss.hasRole('jst_partner')")
    @PartnerDataScope(deptAlias = "c")
    @GetMapping("/list")
    public TableDataInfo list(@Valid EnrollQueryReqDTO query) {
        startPage();
        List<EnrollListVO> list = enrollRecordService.selectAdminList(query);
        return getDataTable(list);
    }

    /**
     * 查询赛事方报名详情。
     *
     * @param enrollId 报名ID
     * @return 详情
     * @关联表 jst_enroll_record
     * @关联状态机 SM-6
     * @关联权限 hasRole('jst_partner')
     */
    @PreAuthorize("@ss.hasRole('jst_partner')")
    @GetMapping("/{enrollId}")
    public AjaxResult detail(@PathVariable("enrollId") Long enrollId) {
        EnrollDetailVO vo = enrollRecordService.getAdminDetail(enrollId);
        return AjaxResult.success(vo);
    }

    /**
     * 单条审核报名记录。
     *
     * @param enrollId 报名ID
     * @param req      审核请求
     * @return 操作结果
     * @关联表 jst_enroll_record
     * @关联状态机 SM-6
     * @关联权限 hasRole('jst_partner')
     */
    @Log(title = "赛事方报名审核", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasRole('jst_partner')")
    @PutMapping("/{enrollId}/audit")
    public AjaxResult audit(@PathVariable("enrollId") Long enrollId,
                            @Valid @RequestBody EnrollAuditReqDTO req) {
        enrollRecordService.audit(enrollId, req);
        return AjaxResult.success();
    }

    /**
     * 批量审核报名记录。
     *
     * @param req 批量审核请求
     * @return 成功处理数量
     * @关联表 jst_enroll_record
     * @关联状态机 SM-6
     * @关联权限 hasRole('jst_partner')
     */
    @Log(title = "赛事方批量审核报名", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasRole('jst_partner')")
    @PutMapping("/batch-audit")
    public AjaxResult batchAudit(@Valid @RequestBody EnrollBatchAuditReqDTO req) {
        int successCount = enrollRecordService.batchAudit(req.getEnrollIds(), req.getResult(), req.getAuditRemark(), req.getScores());
        return AjaxResult.success(Collections.singletonMap("successCount", successCount));
    }
}
