package com.ruoyi.jst.event.mapper;

import com.ruoyi.jst.event.domain.JstCertRecord;
import com.ruoyi.jst.event.domain.JstCertTemplate;
import com.ruoyi.jst.event.dto.CertQueryReqDTO;
import com.ruoyi.jst.event.vo.CertScoreRefVO;
import com.ruoyi.jst.event.vo.CertTemplateResVO;
import com.ruoyi.jst.event.vo.PartnerCertResVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 赛事方证书 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface PartnerCertMapper {

    /**
     * 新增证书模板。
     *
     * @param template 模板实体
     * @return 写入行数
     */
    int insertTemplate(JstCertTemplate template);

    /**
     * 查询模板详情。
     *
     * @param templateId 模板ID
     * @return 模板实体
     */
    JstCertTemplate selectTemplateById(@Param("templateId") Long templateId);

    /**
     * 查询赛事方可用模板列表。
     *
     * @param partnerId 当前赛事方ID
     * @return 模板列表
     */
    List<CertTemplateResVO> selectTemplateList(@Param("partnerId") Long partnerId);

    /**
     * 查询证书列表。
     *
     * @param query 查询条件，承载 PartnerDataScope
     * @return 证书列表
     */
    List<PartnerCertResVO> selectCertList(CertQueryReqDTO query);

    /**
     * 按ID查询证书实体。
     *
     * @param certId 证书ID
     * @return 证书实体
     */
    JstCertRecord selectCertById(@Param("certId") Long certId);

    /**
     * 查询证书详情。
     *
     * @param certId 证书ID
     * @return 证书详情
     */
    PartnerCertResVO selectCertDetail(@Param("certId") Long certId);

    /**
     * 查询可生成证书的已发布成绩。
     *
     * @param contestId 赛事ID
     * @param scoreIds 成绩ID列表
     * @return 成绩引用列表
     */
    List<CertScoreRefVO> selectPublishedScoresForBatch(@Param("contestId") Long contestId,
                                                       @Param("scoreIds") List<Long> scoreIds);

    /**
     * 统计成绩已生成证书数量。
     *
     * @param scoreId 成绩ID
     * @return 数量
     */
    int countCertByScoreId(@Param("scoreId") Long scoreId);

    /**
     * 新增证书记录。
     *
     * @param cert 证书实体
     * @return 写入行数
     */
    int insertCert(JstCertRecord cert);

    /**
     * 按期望状态更新证书发放状态。
     *
     * @param certId 证书ID
     * @param expectedIssueStatus 期望状态
     * @param targetIssueStatus 目标状态
     * @param updateBy 更新人
     * @param updateTime 更新时间
     * @return 更新行数
     */
    /**
     * 按报名ID统计已有证书数量。
     *
     * @param enrollId 报名ID
     * @return 数量
     */
    int countCertByEnrollId(@Param("enrollId") Long enrollId);

    /**
     * 按报名ID列表查询已审核通过的报名记录引用信息。
     *
     * @param contestId 赛事ID
     * @param enrollIds 报名ID列表
     * @return 报名引用列表
     */
    List<CertScoreRefVO> selectApprovedEnrollsForCert(@Param("contestId") Long contestId,
                                                       @Param("enrollIds") List<Long> enrollIds);

    int updateIssueStatusByExpected(@Param("certId") Long certId,
                                    @Param("expectedIssueStatus") String expectedIssueStatus,
                                    @Param("targetIssueStatus") String targetIssueStatus,
                                    @Param("updateBy") String updateBy,
                                    @Param("updateTime") Date updateTime);
}
