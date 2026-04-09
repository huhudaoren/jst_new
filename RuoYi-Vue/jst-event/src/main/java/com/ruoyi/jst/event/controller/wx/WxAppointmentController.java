package com.ruoyi.jst.event.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.event.service.AppointmentService;
import com.ruoyi.jst.event.vo.AppointmentDetailVO;
import com.ruoyi.jst.event.vo.AppointmentListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PreAuthorize("@ss.hasRole('jst_student')")
    @GetMapping("/my")
    public AjaxResult my() {
        List<AppointmentListVO> list = appointmentService.selectMyList(currentUserId());
        return AjaxResult.success(list);
    }

    @PreAuthorize("@ss.hasRole('jst_student')")
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
