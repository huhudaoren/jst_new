package com.ruoyi.jst.user.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.user.dto.BindingQueryReqDTO;
import com.ruoyi.jst.user.service.BindingService;
import com.ruoyi.jst.user.vo.BindingVO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理后台学生-渠道绑定 Controller。
 * <p>
 * 路径前缀：/jst/user/binding
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/user/binding")
public class BindingAdminController extends BaseController {

    @Autowired
    private BindingService bindingService;

    /**
     * 管理后台查询绑定列表。
     *
     * @param query 查询条件
     * @return 分页绑定列表
     * @关联表 jst_student_channel_binding / jst_channel
     * @关联状态机 SM-15
     * @关联权限 jst:user:binding:list
     */
    @PreAuthorize("@ss.hasPermi('jst:user:binding:list')")
    @GetMapping("/list")
    public TableDataInfo list(@Validated BindingQueryReqDTO query) {
        startPage();
        List<BindingVO> list = bindingService.selectBindingList(query);
        return getDataTable(list);
    }

    /**
     * 平台强制解绑指定 binding。
     *
     * @param bindingId 绑定ID
     * @param reason 强制解绑原因
     * @return 操作结果
     * @关联表 jst_student_channel_binding / jst_user
     * @关联状态机 SM-15
     * @关联权限 jst:user:binding:forceUnbind
     */
    @PreAuthorize("@ss.hasPermi('jst:user:binding:forceUnbind')")
    @PostMapping("/{bindingId}/force-unbind")
    public AjaxResult forceUnbind(@PathVariable Long bindingId,
                                  @RequestParam @NotBlank(message = "reason 不能为空") @Size(max = 255, message = "reason 长度不能超过 255") String reason) {
        bindingService.forceUnbind(bindingId, reason);
        return AjaxResult.success();
    }
}
