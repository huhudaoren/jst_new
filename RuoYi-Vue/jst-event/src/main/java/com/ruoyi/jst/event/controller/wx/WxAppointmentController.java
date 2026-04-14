package com.ruoyi.jst.event.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.event.domain.JstAppointmentSlot;
import com.ruoyi.jst.event.dto.AppointmentApplyDTO;
import com.ruoyi.jst.event.mapper.JstAppointmentSlotMapper;
import com.ruoyi.jst.event.service.AppointmentService;
import com.ruoyi.jst.event.vo.AppointmentApplyResVO;
import com.ruoyi.jst.event.vo.AppointmentDetailVO;
import com.ruoyi.jst.event.vo.AppointmentListVO;
import com.ruoyi.jst.event.vo.AppointmentRemainingVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * 小程序端个人预约 Controller。
 */
@Validated
@RestController
@RequestMapping("/jst/wx/appointment")
public class WxAppointmentController extends BaseController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private JstAppointmentSlotMapper appointmentSlotMapper;

    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasPermi('jst:event:appointment:apply')")
    @PostMapping("/apply")
    public AjaxResult apply(@Valid @RequestBody AppointmentApplyDTO req) {
        AppointmentApplyResVO resVO = appointmentService.applyIndividual(currentUserId(), req);
        return AjaxResult.success(resVO);
    }

    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasPermi('jst:event:appointment:cancel')")
    @PostMapping("/{appointmentId}/cancel")
    public AjaxResult cancel(@PathVariable("appointmentId") Long appointmentId) {
        return AjaxResult.success(appointmentService.cancelIndividual(currentUserId(), appointmentId));
    }

    /**
     * 查询赛事所有启用预约时间段列表，按 slot_date ASC, start_time ASC 排序。
     *
     * @param contestId 赛事ID
     * @return 启用时间段列表
     * @关联表 jst_appointment_slot
     */
    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasPermi('jst:event:appointment:my')")
    @GetMapping("/contest/{contestId}/slots")
    public AjaxResult slots(@PathVariable("contestId") Long contestId) {
        List<JstAppointmentSlot> list = appointmentSlotMapper.selectEnabledByContestId(contestId);
        return AjaxResult.success(list);
    }

    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasPermi('jst:event:appointment:my')")
    @GetMapping("/contest/{contestId}/remaining")
    public AjaxResult remaining(@PathVariable("contestId") Long contestId,
                                @RequestParam("appointmentDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date appointmentDate,
                                @RequestParam("sessionCode") String sessionCode) {
        AppointmentRemainingVO detail = appointmentService.getRemaining(contestId, appointmentDate, sessionCode);
        return AjaxResult.success(detail);
    }

    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasPermi('jst:event:appointment:my')")
    @GetMapping("/my")
    public AjaxResult my() {
        List<AppointmentListVO> list = appointmentService.selectMyList(currentUserId());
        return AjaxResult.success(list);
    }

    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasPermi('jst:event:appointment:my')")
    @GetMapping("/{appointmentId}")
    public AjaxResult detail(@PathVariable("appointmentId") Long appointmentId) {
        AppointmentDetailVO detail = appointmentService.getMyDetail(currentUserId(), appointmentId);
        return AjaxResult.success(detail);
    }

    private Long currentUserId() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        return userId;
    }
}
