package com.ruoyi.jst.user.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.user.dto.SwitchBindingReqDTO;
import com.ruoyi.jst.user.dto.UnbindReqDTO;
import com.ruoyi.jst.user.service.BindingService;
import com.ruoyi.jst.user.vo.BindingVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 小程序端学生-渠道绑定 Controller。
 * <p>
 * 路径前缀：/jst/wx/user
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/wx/user")
public class WxBindingController extends BaseController {

    @Autowired
    private BindingService bindingService;

    /**
     * 查询我的绑定历史。
     *
     * @return 绑定历史列表
     * @关联表 jst_student_channel_binding / jst_channel
     * @关联状态机 SM-15
     * @关联权限 hasRole('jst_student')
     */
    @PreAuthorize("@ss.hasRole('jst_student')")
    @GetMapping("/binding")
    public AjaxResult listMyBindings() {
        Long userId = currentUserId();
        List<BindingVO> list = bindingService.selectMyBindings(userId);
        return AjaxResult.success(list);
    }

    /**
     * 切换或新建我的渠道绑定。
     *
     * @param req 切换请求
     * @return 新生成的 active 绑定
     * @关联表 jst_student_channel_binding / jst_channel / jst_user
     * @关联状态机 SM-15
     * @关联权限 hasRole('jst_student')
     */
    @PreAuthorize("@ss.hasRole('jst_student')")
    @PostMapping("/binding/switch")
    public AjaxResult switchBinding(@Valid @RequestBody SwitchBindingReqDTO req) {
        BindingVO bindingVO = bindingService.switchBinding(currentUserId(), req.getChannelId());
        return AjaxResult.success(bindingVO);
    }

    /**
     * 主动解绑当前有效绑定。
     *
     * @param req 解绑请求
     * @return 操作结果
     * @关联表 jst_student_channel_binding / jst_user
     * @关联状态机 SM-15
     * @关联权限 hasRole('jst_student')
     */
    @PreAuthorize("@ss.hasRole('jst_student')")
    @PostMapping("/binding/unbind")
    public AjaxResult unbind(@Valid @RequestBody UnbindReqDTO req) {
        bindingService.unbindCurrent(currentUserId(), req.getReason());
        return AjaxResult.success();
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
