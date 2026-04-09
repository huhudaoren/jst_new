package com.ruoyi.jst.event.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.common.context.JstLoginContext;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.event.dto.TeamAppointmentApplyDTO;
import com.ruoyi.jst.event.dto.TeamAppointmentQueryDTO;
import com.ruoyi.jst.event.service.TeamAppointmentService;
import com.ruoyi.jst.event.vo.TeamAppointmentApplyResVO;
import com.ruoyi.jst.event.vo.TeamAppointmentDetailVO;
import com.ruoyi.jst.event.vo.TeamAppointmentListVO;
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
 * 小程序端团队预约 Controller。
 */
@Validated
@RestController
@RequestMapping("/jst/wx/team-appointment")
public class WxTeamAppointmentController extends BaseController {

    @Autowired
    private TeamAppointmentService teamAppointmentService;

    @PreAuthorize("@ss.hasRole('jst_channel')")
    @PostMapping("/apply")
    public AjaxResult apply(@Valid @RequestBody TeamAppointmentApplyDTO req) {
        TeamAppointmentApplyResVO resVO = teamAppointmentService.apply(currentChannelId(), currentUserId(), req);
        return AjaxResult.success(resVO);
    }

    @PreAuthorize("@ss.hasRole('jst_channel')")
    @GetMapping("/list")
    public TableDataInfo list(TeamAppointmentQueryDTO query) {
        startPage();
        List<TeamAppointmentListVO> list = teamAppointmentService.selectChannelList(currentChannelId(), query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasRole('jst_channel')")
    @GetMapping("/{teamAppointmentId}")
    public AjaxResult detail(@PathVariable("teamAppointmentId") Long teamAppointmentId) {
        TeamAppointmentDetailVO detail = teamAppointmentService.getChannelDetail(currentChannelId(), teamAppointmentId);
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

    private Long currentChannelId() {
        Long channelId = JstLoginContext.currentChannelId();
        if (channelId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        return channelId;
    }
}
