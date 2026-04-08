package com.ruoyi.jst.event.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.common.scope.PartnerScope;
import com.ruoyi.jst.event.dto.AuditReqDTO;
import com.ruoyi.jst.event.dto.ContestQueryReqDTO;
import com.ruoyi.jst.event.dto.ContestSaveReqDTO;
import com.ruoyi.jst.event.service.ContestService;
import com.ruoyi.jst.event.vo.ContestDetailVO;
import com.ruoyi.jst.event.vo.ContestListVO;
import jakarta.validation.Valid;
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

import java.util.Collections;
import java.util.List;

/**
 * 赛事后台 Controller。
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/event/contest")
public class ContestController extends BaseController {

    @Autowired
    private ContestService contestService;

    /**
     * 赛事方创建赛事草稿。
     *
     * @param req 请求体
     * @return 新赛事ID
     */
    @PreAuthorize("@ss.hasPermi('jst:event:contest:add')")
    @PartnerScope(field = "partnerId")
    @PostMapping("/add")
    public AjaxResult add(@Valid @RequestBody ContestSaveReqDTO req) {
        Long contestId = contestService.addContest(req);
        return AjaxResult.success(Collections.singletonMap("contestId", contestId));
    }

    /**
     * 编辑赛事。
     *
     * @param req 请求体
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('jst:event:contest:edit')")
    @PartnerScope(field = "partnerId")
    @PutMapping("/edit")
    public AjaxResult edit(@Valid @RequestBody ContestSaveReqDTO req) {
        contestService.editContest(req);
        return AjaxResult.success();
    }

    /**
     * 提交赛事审核。
     *
     * @param contestId 赛事ID
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('jst:event:contest:submit')")
    @PostMapping("/{contestId}/submit")
    public AjaxResult submit(@PathVariable("contestId") Long contestId) {
        contestService.submitContest(contestId);
        return AjaxResult.success();
    }

    /**
     * 审核通过赛事。
     *
     * @param contestId 赛事ID
     * @param req       审核请求
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('jst:event:contest:audit')")
    @PostMapping("/{contestId}/audit/approve")
    public AjaxResult approve(@PathVariable("contestId") Long contestId, @Valid @RequestBody AuditReqDTO req) {
        contestService.approveContest(contestId, req);
        return AjaxResult.success();
    }

    /**
     * 驳回赛事审核。
     *
     * @param contestId 赛事ID
     * @param req       审核请求
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('jst:event:contest:audit')")
    @PostMapping("/{contestId}/audit/reject")
    public AjaxResult reject(@PathVariable("contestId") Long contestId, @Valid @RequestBody AuditReqDTO req) {
        contestService.rejectContest(contestId, req);
        return AjaxResult.success();
    }

    /**
     * 上线赛事。
     *
     * @param contestId 赛事ID
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('jst:event:contest:online')")
    @PostMapping("/{contestId}/online")
    public AjaxResult online(@PathVariable("contestId") Long contestId) {
        contestService.onlineContest(contestId);
        return AjaxResult.success();
    }

    /**
     * 下线赛事。
     *
     * @param contestId 赛事ID
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('jst:event:contest:offline')")
    @PostMapping("/{contestId}/offline")
    public AjaxResult offline(@PathVariable("contestId") Long contestId) {
        contestService.offlineContest(contestId);
        return AjaxResult.success();
    }

    /**
     * 查询后台赛事列表。
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @PreAuthorize("@ss.hasPermi('jst:event:contest:list')")
    @PartnerScope(field = "partnerId")
    @GetMapping("/list")
    public TableDataInfo list(ContestQueryReqDTO query) {
        startPage();
        List<ContestListVO> list = contestService.selectAdminList(query);
        return getDataTable(list);
    }

    /**
     * 查询后台赛事详情。
     *
     * @param contestId 赛事ID
     * @return 详情
     */
    @PreAuthorize("@ss.hasPermi('jst:event:contest:list')")
    @GetMapping("/{contestId}")
    public AjaxResult detail(@PathVariable("contestId") Long contestId) {
        ContestDetailVO vo = contestService.getAdminDetail(contestId);
        return AjaxResult.success(vo);
    }
}
