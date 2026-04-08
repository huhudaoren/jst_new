package com.ruoyi.jst.event.service;

import com.ruoyi.jst.event.dto.AuditReqDTO;
import com.ruoyi.jst.event.dto.FormTemplateQueryReqDTO;
import com.ruoyi.jst.event.dto.FormTemplateSaveReqDTO;
import com.ruoyi.jst.event.vo.FormTemplateDetailVO;
import com.ruoyi.jst.event.vo.FormTemplateListVO;
import com.ruoyi.jst.event.vo.WxFormTemplateVO;

import java.util.List;

/**
 * 动态表单模板领域服务。
 * <p>
 * 关联表：jst_enroll_form_template / jst_contest
 * 关联状态机：SM-25
 *
 * @author jst
 * @since 1.0.0
 */
public interface FormTemplateService {

    /**
     * 创建或更新动态表单模板。
     *
     * @param req 保存请求
     * @return 模板ID
     */
    Long save(FormTemplateSaveReqDTO req);

    /**
     * 提交模板审核。
     *
     * @param templateId 模板ID
     */
    void submit(Long templateId);

    /**
     * 审核通过模板。
     *
     * @param templateId 模板ID
     * @param req        审核请求
     */
    void approve(Long templateId, AuditReqDTO req);

    /**
     * 审核驳回模板。
     *
     * @param templateId 模板ID
     * @param req        审核请求
     */
    void reject(Long templateId, AuditReqDTO req);

    /**
     * 查询后台模板列表。
     *
     * @param query 查询条件
     * @return 列表结果
     */
    List<FormTemplateListVO> selectList(FormTemplateQueryReqDTO query);

    /**
     * 查询后台模板详情。
     *
     * @param templateId 模板ID
     * @return 详情结果
     */
    FormTemplateDetailVO getDetail(Long templateId);

    /**
     * 小程序按赛事查询报名模板。
     *
     * @param contestId 赛事ID
     * @return 模板结果
     */
    WxFormTemplateVO getWxTemplate(Long contestId);
}
