package com.ruoyi.jst.user.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.user.dto.UserAddressSaveDTO;
import com.ruoyi.jst.user.service.IJstUserAddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序用户收货地址 Controller
 */
@Validated
@RestController
@RequestMapping("/jst/wx/user/address")
@PreAuthorize("@ss.hasRole('jst_student') or @ss.hasPermi('jst:user:address:my')")
public class WxUserAddressController extends BaseController {

    @Autowired
    private IJstUserAddressService userAddressService;

    @GetMapping("/list")
    public AjaxResult list() {
        return AjaxResult.success(userAddressService.selectMyList(currentUserId()));
    }

    @GetMapping("/{id}")
    public AjaxResult detail(@PathVariable("id") Long addressId) {
        return AjaxResult.success(userAddressService.getMyDetail(currentUserId(), addressId));
    }

    @PostMapping
    public AjaxResult create(@Valid @RequestBody UserAddressSaveDTO req) {
        return AjaxResult.success(userAddressService.create(currentUserId(), req));
    }

    @PutMapping("/{id}")
    public AjaxResult update(@PathVariable("id") Long addressId, @Valid @RequestBody UserAddressSaveDTO req) {
        return AjaxResult.success(userAddressService.update(currentUserId(), addressId, req));
    }

    @DeleteMapping("/{id}")
    public AjaxResult delete(@PathVariable("id") Long addressId) {
        userAddressService.delete(currentUserId(), addressId);
        return AjaxResult.success();
    }

    @PostMapping("/{id}/default")
    public AjaxResult setDefault(@PathVariable("id") Long addressId) {
        return AjaxResult.success(userAddressService.setDefault(currentUserId(), addressId));
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
