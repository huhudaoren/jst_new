package com.ruoyi.jst.event.controller.partner;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.jst.common.annotation.PartnerDataScope;
import com.ruoyi.jst.common.controller.BasePartnerController;
import com.ruoyi.jst.event.dto.ScoreCorrectionApplyReqDTO;
import com.ruoyi.jst.event.dto.ScoreQueryReqDTO;
import com.ruoyi.jst.event.dto.ScoreSaveReqDTO;
import com.ruoyi.jst.event.dto.ScoreSubmitReviewReqDTO;
import com.ruoyi.jst.event.service.PartnerScoreService;
import com.ruoyi.jst.event.vo.PartnerScoreResVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

/**
 * 赛事方成绩管理 Controller。
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/partner/score")
public class PartnerScoreController extends BasePartnerController {

    @Autowired
    private PartnerScoreService partnerScoreService;

    /**
     * 导入成绩 Excel，写入 jst_score_record 草稿。
     *
     * @param contestId 赛事ID
     * @param file      Excel 文件
     * @return 导入结果
     */
    @Log(title = "赛事方导入成绩", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('jst:event:score_record:add')")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult importScores(@RequestParam("contestId") Long contestId,
                                   @RequestParam("file") MultipartFile file) {
        return AjaxResult.success(partnerScoreService.importScores(contestId, file));
    }

    /**
     * 查询赛事方成绩列表，SQL 层追加赛事方数据范围。
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @PreAuthorize("@ss.hasPermi('jst:event:score_record:list')")
    @PartnerDataScope(deptAlias = "c")
    @GetMapping("/list")
    public TableDataInfo list(@Valid ScoreQueryReqDTO query) {
        startPage();
        List<PartnerScoreResVO> list = partnerScoreService.listScores(query);
        return getDataTable(list);
    }

    /**
     * 查询成绩管理统计。
     *
     * @param contestId 赛事ID
     * @return 统计信息
     */
    @PreAuthorize("@ss.hasPermi('jst:event:score_record:list')")
    @GetMapping("/stats")
    public AjaxResult stats(@RequestParam("contestId") Long contestId) {
        return AjaxResult.success(partnerScoreService.getStats(contestId));
    }

    /**
     * 新增或编辑草稿成绩；已发布成绩禁止直接编辑。
     *
     * @param id  成绩ID，0 表示新增
     * @param req 保存请求
     * @return 成绩ID
     */
    @Log(title = "赛事方编辑成绩", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('jst:event:score_record:edit')")
    @PutMapping("/{id}")
    public AjaxResult save(@PathVariable("id") Long id, @Valid @RequestBody ScoreSaveReqDTO req) {
        Long scoreId = partnerScoreService.saveScore(id, req);
        return AjaxResult.success(Collections.singletonMap("scoreId", scoreId));
    }

    /**
     * 批量提交成绩审核，遵循 SM-19。
     *
     * @param req 提审请求
     * @return 操作结果
     */
    @Log(title = "赛事方提交成绩审核", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('jst:event:score_record:edit')")
    @PutMapping("/submit-review")
    public AjaxResult submitReview(@Valid @RequestBody ScoreSubmitReviewReqDTO req) {
        partnerScoreService.submitReview(req);
        return AjaxResult.success();
    }

    /**
     * 查询成绩更正申请列表。
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @PreAuthorize("@ss.hasPermi('jst:event:score_record:list')")
    @PartnerDataScope(deptAlias = "c")
    @GetMapping("/correction/apply")
    public TableDataInfo correctionList(@Valid ScoreQueryReqDTO query) {
        startPage();
        return getDataTable(partnerScoreService.listCorrectionApplications(query));
    }

    /**
     * 对已发布成绩提交更正申请，不直接改动对外成绩。
     *
     * @param req 更正申请
     * @return 操作结果
     */
    @Log(title = "赛事方成绩更正申请", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('jst:event:score_record:edit')")
    @PostMapping("/correction/apply")
    public AjaxResult applyCorrection(@Valid @RequestBody ScoreCorrectionApplyReqDTO req) {
        partnerScoreService.applyCorrection(req);
        return AjaxResult.success();
    }
}
