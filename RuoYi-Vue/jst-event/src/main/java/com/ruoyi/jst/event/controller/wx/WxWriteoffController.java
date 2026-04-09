package com.ruoyi.jst.event.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.event.dto.WriteoffRecordQueryDTO;
import com.ruoyi.jst.event.dto.WriteoffScanDTO;
import com.ruoyi.jst.event.service.WriteoffService;
import com.ruoyi.jst.event.vo.WriteoffRecordVO;
import com.ruoyi.jst.event.vo.WriteoffScanResVO;
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
 * 小程序端核销 Controller。
 */
@Validated
@RestController
@RequestMapping("/jst/wx/writeoff")
public class WxWriteoffController extends BaseController {

    @Autowired
    private WriteoffService writeoffService;

    @PreAuthorize("@ss.hasAnyRoles('jst_partner,jst_platform_op') or @ss.hasPermi('jst:event:writeoff:scan')")
    @PostMapping("/scan")
    public AjaxResult scan(@Valid @RequestBody WriteoffScanDTO req) {
        WriteoffScanResVO resVO = writeoffService.scan(currentUserId(), req);
        return AjaxResult.success(resVO);
    }

    @PreAuthorize("@ss.hasAnyRoles('jst_partner,jst_platform_op') or @ss.hasPermi('jst:event:writeoff:list')")
    @GetMapping("/records")
    public TableDataInfo records(WriteoffRecordQueryDTO query) {
        startPage();
        List<WriteoffRecordVO> list = writeoffService.selectRecords(query);
        return getDataTable(list);
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
