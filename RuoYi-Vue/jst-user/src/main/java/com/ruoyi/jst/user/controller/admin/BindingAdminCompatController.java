package com.ruoyi.jst.user.controller.admin;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.user.dto.BindingQueryReqDTO;
import com.ruoyi.jst.user.service.BindingService;
import com.ruoyi.jst.user.vo.BindingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端学生-渠道绑定查询 Controller（admin 路径兼容入口）。
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/admin/binding")
public class BindingAdminCompatController extends BaseController {

    @Autowired
    private BindingService bindingService;

    /**
     * 管理端查询绑定列表（支持 channelId 过滤）。
     *
     * @param query 查询条件
     * @return 分页数据
     * @关联表 jst_student_channel_binding / jst_channel
     * @关联状态机 SM-15
     * @关联权限 jst:user:binding:list
     */
    @PreAuthorize("@ss.hasPermi('jst:user:binding:list')")
    @GetMapping("/list")
    public TableDataInfo list(@Validated BindingQueryReqDTO query) {
        startPage();
        List<BindingVO> rows = bindingService.selectBindingList(query);
        return getDataTable(rows);
    }
}
