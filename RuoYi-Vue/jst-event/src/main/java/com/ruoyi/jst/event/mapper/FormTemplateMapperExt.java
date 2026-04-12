package com.ruoyi.jst.event.mapper;

import com.ruoyi.jst.event.domain.JstEnrollFormTemplate;
import com.ruoyi.jst.event.dto.FormTemplateQueryReqDTO;
import com.ruoyi.jst.event.vo.FormTemplateListVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 动态表单模板扩展 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface FormTemplateMapperExt {

    /**
     * 查询动态表单模板列表。
     *
     * @param query 查询条件
     * @return 列表结果
     */
    List<FormTemplateListVO> selectFormTemplateList(@Param("query") FormTemplateQueryReqDTO query);

    /**
     * 查询赛事方可用模板（公共模板 + 当前赛事方模板）。
     *
     * @param partnerId 当前赛事方ID
     * @param query     查询条件
     * @return 模板列表
     */
    List<FormTemplateListVO> selectPartnerAvailableList(@Param("partnerId") Long partnerId,
                                                        @Param("query") FormTemplateQueryReqDTO query);

    /**
     * 保存编辑后的模板内容，并做版本自增与乐观锁控制。
     *
     * @param template             模板实体
     * @param expectedAuditStatus 期望当前审核状态
     * @return 更新行数
     */
    int updateAfterSave(@Param("template") JstEnrollFormTemplate template,
                        @Param("expectedAuditStatus") String expectedAuditStatus);

    /**
     * 按期望审核状态推进状态机。
     *
     * @param templateId          模板ID
     * @param expectedAuditStatus 期望当前审核状态
     * @param targetAuditStatus   目标审核状态
     * @param effectiveTime       生效时间
     * @param updateBy            更新人
     * @param updateTime          更新时间
     * @return 更新行数
     */
    int updateAuditStatusByExpected(@Param("templateId") Long templateId,
                                    @Param("expectedAuditStatus") String expectedAuditStatus,
                                    @Param("targetAuditStatus") String targetAuditStatus,
                                    @Param("effectiveTime") Date effectiveTime,
                                    @Param("updateBy") String updateBy,
                                    @Param("updateTime") Date updateTime);
}
