package com.ruoyi.jst.marketing.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.marketing.dto.RightsWriteoffApplyDTO;
import com.ruoyi.jst.marketing.service.RightsUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Wx rights controller.
 */
@Validated
@RestController
@RequestMapping("/jst/wx/rights")
public class WxRightsController extends BaseController {

    @Autowired
    private RightsUserService rightsUserService;

    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasPermi('jst:marketing:rights:my')")
    @GetMapping("/my")
    public TableDataInfo my(@RequestParam(value = "status", required = false) String status) {
        startPage();
        return getDataTable(rightsUserService.selectMyRights(currentUserId(), status));
    }

    /**
     * 当前用户核销记录列表（跨权益）。解决前端 N+1 聚合问题。
     *
     * <p>状态语义：
     * <ul>
     *   <li>pending / approved / rejected / rolled_back / used / expired：单值</li>
     *   <li>invalid：等价于 used + expired + rolled_back 合集</li>
     * </ul>
     *
     * @关联表 jst_rights_writeoff_record / jst_user_rights / jst_rights_template
     * @关联权限 hasRole('jst_student') or hasPermi('jst:marketing:rights:my')
     */
    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasPermi('jst:marketing:rights:my')")
    @GetMapping("/writeoff-records")
    public TableDataInfo listMyWriteoffRecords(
            @RequestParam(value = "rightsId", required = false) Long rightsId,
            @RequestParam(value = "status", required = false) String status) {
        startPage();
        return getDataTable(rightsUserService.selectMyWriteoffRecords(currentUserId(), rightsId, status));
    }

    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasPermi('jst:marketing:rights:my')")
    @GetMapping("/{userRightsId}")
    public AjaxResult detail(@PathVariable("userRightsId") Long userRightsId) {
        return AjaxResult.success(rightsUserService.getDetail(currentUserId(), userRightsId));
    }

    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasPermi('jst:marketing:rights:my')")
    @PostMapping("/{userRightsId}/apply-writeoff")
    public AjaxResult applyWriteoff(@PathVariable("userRightsId") Long userRightsId,
                                    @Valid @RequestBody RightsWriteoffApplyDTO req) {
        return AjaxResult.success(rightsUserService.applyWriteoff(currentUserId(), userRightsId, req));
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
