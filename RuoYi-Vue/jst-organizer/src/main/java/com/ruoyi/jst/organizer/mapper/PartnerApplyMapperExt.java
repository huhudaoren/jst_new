package com.ruoyi.jst.organizer.mapper;

import com.ruoyi.jst.organizer.domain.JstEventPartnerApply;
import com.ruoyi.jst.organizer.dto.PartnerApplyQueryDTO;
import com.ruoyi.jst.organizer.vo.PartnerApplyListVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 赛事方入驻申请扩展 Mapper
 * <p>
 * 负责列表查询、状态推进和去重校验等扩展 SQL。
 *
 * @author jst
 * @since 1.0.0
 */
public interface PartnerApplyMapperExt {

    /**
     * 查询入驻申请详情实体。
     *
     * @param applyId 申请ID
     * @return 申请实体
     */
    JstEventPartnerApply selectByApplyId(@Param("applyId") Long applyId);

    /**
     * 按申请单号查询入驻申请。
     *
     * @param applyNo 申请单号
     * @return 申请实体
     */
    JstEventPartnerApply selectByApplyNo(@Param("applyNo") String applyNo);

    /**
     * 统计同手机号下待审核或已通过的申请数量。
     *
     * @param contactMobile 联系手机号
     * @return 数量
     */
    long countDuplicateActiveByMobile(@Param("contactMobile") String contactMobile);

    /**
     * 查询后台审核列表。
     *
     * @param query 查询条件
     * @return 列表结果
     */
    List<PartnerApplyListVO> selectPartnerApplyList(PartnerApplyQueryDTO query);

    /**
     * 按期望状态推进申请状态。
     *
     * @param applyId          申请ID
     * @param expectedStatus   期望当前状态
     * @param targetStatus     目标状态
     * @param auditRemark      审核备注
     * @param supplementRemark 补件备注
     * @param auditUserId      审核员ID
     * @param auditTime        审核时间
     * @param partnerId        审核通过后生成的赛事方ID
     * @param updateBy         更新人
     * @return 更新行数
     */
    int updateApplyStatusByExpectedStatus(@Param("applyId") Long applyId,
                                          @Param("expectedStatus") String expectedStatus,
                                          @Param("targetStatus") String targetStatus,
                                          @Param("auditRemark") String auditRemark,
                                          @Param("supplementRemark") String supplementRemark,
                                          @Param("auditUserId") Long auditUserId,
                                          @Param("auditTime") Date auditTime,
                                          @Param("partnerId") Long partnerId,
                                          @Param("updateBy") String updateBy);
}
