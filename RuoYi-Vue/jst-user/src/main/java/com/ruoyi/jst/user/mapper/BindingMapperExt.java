package com.ruoyi.jst.user.mapper;

import com.ruoyi.jst.user.domain.JstChannel;
import com.ruoyi.jst.user.domain.JstStudentChannelBinding;
import com.ruoyi.jst.user.dto.BindingQueryReqDTO;
import com.ruoyi.jst.user.vo.BindingVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学生-渠道绑定扩展 Mapper。
 * <p>
 * 负责 F4 业务所需的联表查询、状态更新和用户绑定回写。
 *
 * @author jst
 * @since 1.0.0
 */
@Mapper
public interface BindingMapperExt {

    /**
     * 查询当前用户的全部绑定历史。
     *
     * @param userId 当前学生用户ID
     * @return 绑定历史列表
     */
    List<BindingVO> selectMyBindingList(@Param("userId") Long userId);

    /**
     * 管理后台按条件查询绑定列表。
     *
     * @param query 查询条件
     * @return 绑定列表
     */
    List<BindingVO> selectBindingList(BindingQueryReqDTO query);

    /**
     * 按绑定ID查询展示 VO。
     *
     * @param bindingId 绑定ID
     * @return 绑定展示对象
     */
    BindingVO selectBindingVoById(@Param("bindingId") Long bindingId);

    /**
     * 查询用户当前 active 绑定。
     *
     * @param userId 学生用户ID
     * @return 当前有效绑定
     */
    JstStudentChannelBinding selectActiveBindingByUserId(@Param("userId") Long userId);

    /**
     * 按绑定ID查询绑定实体。
     *
     * @param bindingId 绑定ID
     * @return 绑定实体
     */
    JstStudentChannelBinding selectBindingById(@Param("bindingId") Long bindingId);

    /**
     * 查询可绑定的已启用且已审核通过渠道。
     *
     * @param channelId 渠道方ID
     * @return 渠道方实体
     */
    JstChannel selectApprovedChannelById(@Param("channelId") Long channelId);

    /**
     * 新增绑定记录。
     *
     * @param binding 绑定实体
     * @return 影响行数
     */
    int insertBinding(JstStudentChannelBinding binding);

    /**
     * 按预期状态更新绑定状态。
     *
     * @param bindingId      绑定ID
     * @param expectedStatus 预期旧状态
     * @param newStatus      新状态
     * @param operatorId     操作人ID
     * @param unbindReason   解绑原因
     * @param updateBy       更新人
     * @return 影响行数
     */
    int updateBindingStatus(@Param("bindingId") Long bindingId,
                            @Param("expectedStatus") String expectedStatus,
                            @Param("newStatus") String newStatus,
                            @Param("operatorId") Long operatorId,
                            @Param("unbindReason") String unbindReason,
                            @Param("updateBy") String updateBy);

    /**
     * 回写用户当前绑定渠道。
     *
     * @param userId         学生用户ID
     * @param boundChannelId 当前绑定渠道ID，可为 null
     * @param updateBy       更新人
     * @return 影响行数
     */
    int updateUserBoundChannelId(@Param("userId") Long userId,
                                 @Param("boundChannelId") Long boundChannelId,
                                 @Param("updateBy") String updateBy);
}
