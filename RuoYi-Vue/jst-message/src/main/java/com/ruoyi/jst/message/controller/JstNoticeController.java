package com.ruoyi.jst.message.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.message.domain.JstNotice;
import com.ruoyi.jst.message.dto.NoticeQueryReqDTO;
import com.ruoyi.jst.message.dto.NoticeSaveReqDTO;
import com.ruoyi.jst.message.service.NoticeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理后台公告 Controller。
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/message/notice")
public class JstNoticeController extends BaseController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 管理后台公告分页列表。
     *
     * @param query 查询条件
     * @return 公告分页结果
     * @关联表 jst_notice
     * @关联状态机 无
     * @关联权限 jst:message:notice:list
     */
    @PreAuthorize("@ss.hasPermi('jst:message:notice:list')")
    @GetMapping("/list")
    public TableDataInfo list(@Validated NoticeQueryReqDTO query) {
        startPage();
        List<JstNotice> list = noticeService.selectAdminNoticeList(query);
        return getDataTable(list);
    }

    /**
     * 平台运营新增公告。
     *
     * @param req 保存请求
     * @return 新公告ID
     * @关联表 jst_notice
     * @关联状态机 无
     * @关联权限 jst:message:notice:add
     */
    @PreAuthorize("@ss.hasPermi('jst:message:notice:add')")
    @PostMapping("/add")
    public AjaxResult add(@Valid @RequestBody NoticeSaveReqDTO req) {
        Long noticeId = noticeService.addNotice(req);
        return AjaxResult.success(noticeId);
    }

    /**
     * 平台运营编辑公告。
     *
     * @param req 编辑请求
     * @return 操作结果
     * @关联表 jst_notice
     * @关联状态机 无
     * @关联权限 jst:message:notice:edit
     */
    @PreAuthorize("@ss.hasPermi('jst:message:notice:edit')")
    @PutMapping("/edit")
    public AjaxResult edit(@Valid @RequestBody NoticeSaveReqDTO req) {
        if (req.getNoticeId() == null) {
            throw new ServiceException("noticeId 不能为空", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        noticeService.editNotice(req);
        return AjaxResult.success();
    }

    /**
     * 平台运营发布公告。
     *
     * @param noticeId 公告ID
     * @return 操作结果
     * @关联表 jst_notice
     * @关联状态机 无
     * @关联权限 jst:message:notice:publish
     */
    @PreAuthorize("@ss.hasPermi('jst:message:notice:publish')")
    @PostMapping("/{noticeId}/publish")
    public AjaxResult publish(@PathVariable("noticeId") @NotNull Long noticeId) {
        noticeService.publishNotice(noticeId);
        return AjaxResult.success();
    }

    /**
     * 平台运营下线公告。
     *
     * @param noticeId 公告ID
     * @return 操作结果
     * @关联表 jst_notice
     * @关联状态机 无
     * @关联权限 jst:message:notice:offline
     */
    @PreAuthorize("@ss.hasPermi('jst:message:notice:offline')")
    @PostMapping("/{noticeId}/offline")
    public AjaxResult offline(@PathVariable("noticeId") @NotNull Long noticeId) {
        noticeService.offlineNotice(noticeId);
        return AjaxResult.success();
    }
}
