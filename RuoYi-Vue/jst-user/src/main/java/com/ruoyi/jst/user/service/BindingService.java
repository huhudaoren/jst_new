package com.ruoyi.jst.user.service;

import com.ruoyi.jst.user.dto.BindingQueryReqDTO;
import com.ruoyi.jst.user.vo.BindingVO;

import java.util.List;

/**
 * 学生-渠道绑定领域服务。
 * <p>
 * 关联表：jst_student_channel_binding / jst_channel / jst_user
 * 关联状态机：SM-15
 *
 * @author jst
 * @since 1.0.0
 */
public interface BindingService {

    /**
     * 查询当前用户绑定历史。
     *
     * @param userId 当前学生用户ID
     * @return 绑定历史列表
     */
    List<BindingVO> selectMyBindings(Long userId);

    /**
     * 切换或新建当前用户绑定。
     *
     * @param userId 当前学生用户ID
     * @param channelId 目标渠道方ID
     * @return 新生成的 active 绑定
     */
    BindingVO switchBinding(Long userId, Long channelId);

    /**
     * 主动解绑当前 active 绑定。
     *
     * @param userId 当前学生用户ID
     * @param reason 解绑原因
     */
    void unbindCurrent(Long userId, String reason);

    /**
     * 管理后台查询绑定列表。
     *
     * @param query 查询条件
     * @return 绑定列表
     */
    List<BindingVO> selectBindingList(BindingQueryReqDTO query);

    /**
     * 平台强制解绑指定 binding。
     *
     * @param bindingId 绑定ID
     * @param reason 强制解绑原因
     */
    void forceUnbind(Long bindingId, String reason);
}
