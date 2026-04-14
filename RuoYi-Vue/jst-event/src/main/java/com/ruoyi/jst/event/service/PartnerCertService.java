package com.ruoyi.jst.event.service;

import com.ruoyi.jst.event.dto.CertBatchGrantReqDTO;
import com.ruoyi.jst.event.dto.CertGenerateReqDTO;
import com.ruoyi.jst.event.dto.CertQueryReqDTO;
import com.ruoyi.jst.event.dto.CertSubmitReviewReqDTO;
import com.ruoyi.jst.event.dto.CertTemplateReqDTO;
import com.ruoyi.jst.event.vo.CertBatchGrantResVO;
import com.ruoyi.jst.event.vo.CertPreviewResVO;
import com.ruoyi.jst.event.vo.CertTemplateResVO;
import com.ruoyi.jst.event.vo.PartnerCertResVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 赛事方证书领域服务。
 *
 * @author jst
 * @since 1.0.0
 */
public interface PartnerCertService {

    /**
     * 上传或登记证书模板。
     *
     * @param req 模板请求，关联表 jst_cert_template，权限 jst:event:cert_template:add
     * @param file 模板文件
     * @return 模板ID
     */
    Long saveTemplate(CertTemplateReqDTO req, MultipartFile file);

    /**
     * 查询模板列表。
     *
     * @return 模板列表，关联表 jst_cert_template
     */
    List<CertTemplateResVO> listTemplates();

    /**
     * 查询已发证记录。
     *
     * @param query 查询条件，关联表 jst_cert_record，权限 jst:event:cert_record:list
     * @return 证书列表
     */
    List<PartnerCertResVO> listCerts(CertQueryReqDTO query);

    /**
     * 按已发布成绩批量生成证书草稿。
     *
     * @param req 批量生成请求，关联 SM-19/SM-20
     * @return 生成结果
     */
    CertBatchGrantResVO batchGrant(CertBatchGrantReqDTO req);

    /**
     * 提交证书发放审核。
     *
     * @param req 提交请求，关联 SM-20
     */
    void submitReview(CertSubmitReviewReqDTO req);

    /**
     * 生成证书预览图。
     *
     * @param certId 证书ID，关联表 jst_cert_record
     * @return 预览图
     */
    CertPreviewResVO preview(Long certId);

    /**
     * 按报名ID列表逐个生成证书记录（简化版，仅创建记录+编号，不做 PDF 渲染）。
     *
     * @param req 生成请求，关联 jst_cert_record, jst_enroll_record
     * @return 生成结果
     */
    CertBatchGrantResVO generate(CertGenerateReqDTO req);
}
