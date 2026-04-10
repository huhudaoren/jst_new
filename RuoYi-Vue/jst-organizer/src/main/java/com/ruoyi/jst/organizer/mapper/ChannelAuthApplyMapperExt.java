package com.ruoyi.jst.organizer.mapper;

import com.ruoyi.jst.organizer.domain.JstChannelAuthApply;
import com.ruoyi.jst.organizer.dto.ChannelAuthApplyQueryDTO;
import com.ruoyi.jst.organizer.vo.ChannelAuthApplyVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 渠道认证申请扩展 Mapper
 * <p>
 * 关联表：jst_channel_auth_apply
 * 关联状态机：SM-3
 *
 * @author jst
 * @since 1.0.0
 */
public interface ChannelAuthApplyMapperExt {

    /**
     * 统计用户待处理/已通过的申请数
     *
     * @param userId 用户ID
     * @return 申请数量
     */
    int countActiveApplyByUserId(@Param("userId") Long userId);

    /**
     * 按主键查询申请实体
     *
     * @param applyId 申请ID
     * @return 申请实体
     */
    JstChannelAuthApply selectApplyById(@Param("applyId") Long applyId);

    /**
     * 查询当前用户申请历史
     *
     * @param userId 当前用户ID
     * @return 申请列表
     */
    List<ChannelAuthApplyVO> selectMyApplyList(@Param("userId") Long userId);

    /**
     * 管理端分页查询申请列表
     *
     * @param query 查询条件
     * @return 申请列表
     */
    List<ChannelAuthApplyVO> selectAdminApplyList(ChannelAuthApplyQueryDTO query);

    /**
     * 查询申请详情
     *
     * @param applyId 申请ID
     * @return 申请详情
     */
    ChannelAuthApplyVO selectApplyDetail(@Param("applyId") Long applyId);

    /**
     * 新增申请
     *
     * @param apply 申请实体
     * @return 影响行数
     */
    int insertApply(JstChannelAuthApply apply);

    /**
     * 查询当前用户最新一条申请
     *
     * @param userId 当前用户ID
     * @return 最新申请（含 rejectCount/lockedForManual）
     */
    ChannelAuthApplyVO selectLatestByUserId(@Param("userId") Long userId);

    /**
     * 驳回时递增 reject_count 并可能锁定
     *
     * @param applyId          申请ID
     * @param newRejectCount   新的驳回次数
     * @param lockedForManual  是否锁定
     * @return 影响行数
     */
    int updateRejectCount(@Param("applyId") Long applyId,
                          @Param("newRejectCount") int newRejectCount,
                          @Param("lockedForManual") int lockedForManual);

    /**
     * 按预期状态推进申请
     *
     * @param applyId        申请ID
     * @param expectedStatus 预期当前状态
     * @param targetStatus   目标状态
     * @param channelId      审核通过后关联渠道ID
     * @param auditRemark    审核备注
     * @param auditUserId    审核员ID
     * @param auditTime      审核时间
     * @param updateBy       更新人
     * @return 影响行数
     */
    int updateApplyStatus(@Param("applyId") Long applyId,
                          @Param("expectedStatus") String expectedStatus,
                          @Param("targetStatus") String targetStatus,
                          @Param("channelId") Long channelId,
                          @Param("auditRemark") String auditRemark,
                          @Param("auditUserId") Long auditUserId,
                          @Param("auditTime") Date auditTime,
                          @Param("updateBy") String updateBy);
}
