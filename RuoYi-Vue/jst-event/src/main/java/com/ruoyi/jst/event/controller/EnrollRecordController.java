package com.ruoyi.jst.event.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.common.scope.PartnerScope;
import com.ruoyi.jst.event.dto.EnrollAuditReqDTO;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 报名记录后台 Controller。
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/event/enroll")
public class EnrollRecordController extends BaseController {

    @Autowired
    private EnrollRecordService enrollRecordService;

    /**
     * 查询报名列表。
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @PreAuthorize("@ss.hasPermi('jst:event:enrollRecord:list')")
    @PartnerScope(field = "partnerId")
    @GetMapping("/list")
    public TableDataInfo list(@Valid EnrollQueryReqDTO query) {
        startPage();
        List<EnrollListVO> list = enrollRecordService.selectAdminList(query);
        return getDataTable(list);
    }

    /**
     * 查询报名详情。
     *
     * @param enrollId 报名 ID
     * @return 详情
     */
    @PreAuthorize("@ss.hasPermi('jst:event:enrollRecord:query')")
    @GetMapping("/{enrollId}")
    public AjaxResult detail(@PathVariable("enrollId") Long enrollId) {
        EnrollDetailVO vo = enrollRecordService.getAdminDetail(enrollId);
        return AjaxResult.success(vo);
    }

    /**
     * 审核报名记录。
     *
     * @param enrollId 报名 ID
     * @param req      审核请求
     * @return 处理结果
     */
    @PreAuthorize("@ss.hasPermi('jst:event:enrollRecord:audit')")
    @PostMapping("/{enrollId}/audit")
    public AjaxResult audit(@PathVariable("enrollId") Long enrollId, @Valid @RequestBody EnrollAuditReqDTO req) {
        enrollRecordService.audit(enrollId, req);
        return AjaxResult.success();
    }
}
