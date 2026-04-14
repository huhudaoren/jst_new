package com.ruoyi.jst.common.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.common.dto.BizNoRuleQueryReqDTO;
import com.ruoyi.jst.common.dto.BizNoRuleSaveReqDTO;
import com.ruoyi.jst.common.dto.BizNoRuleUpdateReqDTO;
import com.ruoyi.jst.common.service.JstBizNoRuleService;
import com.ruoyi.jst.common.vo.BizNoRuleResVO;
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

import java.util.List;

/**
 * 业务编号规则管理 Controller。
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/admin/biz-no-rule")
public class JstBizNoRuleController extends BaseController {

    @Autowired
    private JstBizNoRuleService jstBizNoRuleService;

    /**
     * 查询编号规则分页列表。
     *
     * @param query 查询条件
     * @return 分页结果
     * @关联表 jst_biz_no_rule
     * @关联权限 jst:admin:bizNoRule:list
     */
    @PreAuthorize("@ss.hasPermi('jst:admin:bizNoRule:list')")
    @GetMapping("/list")
    public TableDataInfo list(@Validated BizNoRuleQueryReqDTO query) {
        startPage();
        List<BizNoRuleResVO> rows = jstBizNoRuleService.selectBizNoRuleList(query);
        return getDataTable(rows);
    }

    /**
     * 查询编号规则详情。
     *
     * @param ruleId 规则ID
     * @return 规则详情
     * @关联表 jst_biz_no_rule
     * @关联权限 jst:admin:bizNoRule:query
     */
    @PreAuthorize("@ss.hasPermi('jst:admin:bizNoRule:query')")
    @GetMapping("/{ruleId}")
    public AjaxResult getInfo(@PathVariable("ruleId") Long ruleId) {
        return AjaxResult.success(jstBizNoRuleService.selectBizNoRuleById(ruleId));
    }

    /**
     * 新增编号规则。
     *
     * @param req 新增请求
     * @return 新规则ID
     * @关联表 jst_biz_no_rule
     * @关联权限 jst:admin:bizNoRule:add
     */
    @PreAuthorize("@ss.hasPermi('jst:admin:bizNoRule:add')")
    @PostMapping
    public AjaxResult add(@Valid @RequestBody BizNoRuleSaveReqDTO req) {
        return AjaxResult.success(jstBizNoRuleService.addBizNoRule(req));
    }

    /**
     * 修改编号规则。
     *
     * @param req 修改请求
     * @return 操作结果
     * @关联表 jst_biz_no_rule
     * @关联权限 jst:admin:bizNoRule:edit
     */
    @PreAuthorize("@ss.hasPermi('jst:admin:bizNoRule:edit')")
    @PutMapping
    public AjaxResult edit(@Valid @RequestBody BizNoRuleUpdateReqDTO req) {
        jstBizNoRuleService.editBizNoRule(req);
        return AjaxResult.success();
    }

    /**
     * 删除编号规则。
     *
     * @param ruleIds 规则ID数组
     * @return 操作结果
     * @关联表 jst_biz_no_rule
     * @关联权限 jst:admin:bizNoRule:remove
     */
    @PreAuthorize("@ss.hasPermi('jst:admin:bizNoRule:remove')")
    @DeleteMapping("/{ruleIds}")
    public AjaxResult remove(@PathVariable Long[] ruleIds) {
        jstBizNoRuleService.removeBizNoRule(ruleIds);
        return AjaxResult.success();
    }
}
