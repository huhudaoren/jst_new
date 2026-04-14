package com.ruoyi.jst.event.controller.partner;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.jst.common.annotation.PartnerDataScope;
import com.ruoyi.jst.common.controller.BasePartnerController;
import com.ruoyi.jst.event.dto.ContestQueryReqDTO;
import com.ruoyi.jst.event.dto.ContestSaveReqDTO;
import com.ruoyi.jst.event.dto.ReviewerSaveReqDTO;
import com.ruoyi.jst.event.service.ContestReviewerService;
import com.ruoyi.jst.event.service.ContestService;
import com.ruoyi.jst.event.vo.ContestDetailVO;
import com.ruoyi.jst.event.vo.ContestListVO;
import com.ruoyi.jst.event.vo.ReviewerVO;
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

import java.util.Collections;
import java.util.List;

/**
 * 赛事方赛事管理包装 Controller。
 * <p>
 * 对外提供任务卡约定的 `/jst/partner/contest/*` 路由，
 * 内部复用 ContestService 领域实现。
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/partner/contest")
public class PartnerContestController extends BasePartnerController {

    @Autowired
    private ContestService contestService;

    @Autowired
    private ContestReviewerService contestReviewerService;

    /**
     * 查询赛事方赛事列表。
     *
     * @param query 查询条件
     * @return 分页列表
     * @关联表 jst_contest
     * @关联状态机 SM-5
     * @关联权限 hasRole('jst_partner')
     */
    @PreAuthorize("@ss.hasRole('jst_partner')")
    @PartnerDataScope(deptAlias = "c")
    @GetMapping("/list")
    public TableDataInfo list(@Valid ContestQueryReqDTO query) {
        startPage();
        List<ContestListVO> list = contestService.selectAdminList(query);
        return getDataTable(list);
    }

    /**
     * 查询赛事方赛事详情。
     *
     * @param contestId 赛事ID
     * @return 赛事详情
     * @关联表 jst_contest
     * @关联状态机 SM-5
     * @关联权限 hasRole('jst_partner')
     */
    @PreAuthorize("@ss.hasRole('jst_partner')")
    @GetMapping("/{contestId}")
    public AjaxResult detail(@PathVariable("contestId") Long contestId) {
        ContestDetailVO vo = contestService.getAdminDetail(contestId);
        return AjaxResult.success(vo);
    }

    /**
     * 赛事方新增赛事草稿。
     *
     * @param req 保存请求
     * @return 新赛事ID
     * @关联表 jst_contest
     * @关联状态机 SM-5
     * @关联权限 hasRole('jst_partner')
     */
    @Log(title = "赛事方新增赛事", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasRole('jst_partner')")
    @PostMapping
    public AjaxResult create(@Valid @RequestBody ContestSaveReqDTO req) {
        req.setPartnerId(currentPartnerId());
        Long contestId = contestService.addContest(req);
        return AjaxResult.success(Collections.singletonMap("contestId", contestId));
    }

    /**
     * 赛事方编辑赛事。
     *
     * @param contestId 赛事ID
     * @param req       保存请求
     * @return 操作结果
     * @关联表 jst_contest
     * @关联状态机 SM-5
     * @关联权限 hasRole('jst_partner')
     */
    @Log(title = "赛事方编辑赛事", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasRole('jst_partner')")
    @PutMapping("/{contestId}")
    public AjaxResult update(@PathVariable("contestId") Long contestId,
                             @Valid @RequestBody ContestSaveReqDTO req) {
        req.setContestId(contestId);
        req.setPartnerId(currentPartnerId());
        contestService.editContest(req);
        return AjaxResult.success();
    }

    /**
     * 赛事方提交赛事审核。
     *
     * @param contestId 赛事ID
     * @return 操作结果
     * @关联表 jst_contest
     * @关联状态机 SM-5
     * @关联权限 hasRole('jst_partner')
     */
    @Log(title = "赛事方提交审核", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasRole('jst_partner')")
    @PutMapping("/{contestId}/submit")
    public AjaxResult submit(@PathVariable("contestId") Long contestId) {
        contestService.submitContest(contestId);
        return AjaxResult.success();
    }

    /**
     * 赛事方下线赛事。
     *
     * @param contestId 赛事ID
     * @return 操作结果
     * @关联表 jst_contest
     * @关联状态机 SM-5
     * @关联权限 hasRole('jst_partner')
     */
    @Log(title = "赛事方下线赛事", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasRole('jst_partner')")
    @PutMapping("/{contestId}/offline")
    public AjaxResult offline(@PathVariable("contestId") Long contestId) {
        contestService.offlineContest(contestId);
        return AjaxResult.success();
    }

    /**
     * 赛事方删除草稿赛事。
     *
     * @param contestId 赛事ID
     * @return 操作结果
     * @关联表 jst_contest
     * @关联状态机 SM-5
     * @关联权限 hasRole('jst_partner')
     */
    @Log(title = "赛事方删除赛事", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasRole('jst_partner')")
    @DeleteMapping("/{contestId}")
    public AjaxResult delete(@PathVariable("contestId") Long contestId) {
        contestService.deleteContest(contestId);
        return AjaxResult.success();
    }

    /**
     * 赛事方复制赛事。
     *
     * @param contestId 原赛事ID
     * @return 新赛事ID
     * @关联表 jst_contest, jst_contest_schedule, jst_contest_award, jst_contest_faq, jst_score_item
     * @关联状态机 SM-5
     * @关联权限 hasRole('jst_partner')
     */
    @Log(title = "赛事方复制赛事", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasRole('jst_partner')")
    @PostMapping("/copy/{contestId}")
    public AjaxResult copy(@PathVariable("contestId") Long contestId) {
        Long newContestId = contestService.copyContest(contestId);
        return AjaxResult.success(Collections.singletonMap("contestId", newContestId));
    }

    // ========== 评审老师管理 ==========

    /**
     * 获取赛事评审老师列表。
     *
     * @param contestId 赛事ID
     * @return 评审老师列表
     * @关联表 jst_contest_reviewer
     * @关联权限 hasRole('jst_partner')
     */
    @PreAuthorize("@ss.hasRole('jst_partner')")
    @GetMapping("/{contestId}/reviewers")
    public AjaxResult listReviewers(@PathVariable("contestId") Long contestId) {
        List<ReviewerVO> list = contestReviewerService.listByContestId(contestId);
        return AjaxResult.success(list);
    }

    /**
     * 配置赛事评审老师（全量替换）。
     *
     * @param contestId 赛事ID
     * @param req       评审老师配置请求
     * @return 操作结果
     * @关联表 jst_contest_reviewer
     * @关联权限 hasRole('jst_partner')
     */
    @Log(title = "赛事方配置评审老师", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasRole('jst_partner')")
    @PostMapping("/{contestId}/reviewers")
    public AjaxResult saveReviewers(@PathVariable("contestId") Long contestId,
                                    @Valid @RequestBody ReviewerSaveReqDTO req) {
        contestReviewerService.saveReviewers(contestId, req);
        return AjaxResult.success();
    }
}
