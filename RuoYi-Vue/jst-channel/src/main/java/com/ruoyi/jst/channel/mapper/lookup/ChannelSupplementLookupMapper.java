package com.ruoyi.jst.channel.mapper.lookup;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 渠道方补充查询 Mapper（E0-1 新增）
 * <p>
 * 跨模块只读查询 + 解绑写操作
 * 关联表：jst_user_rights / jst_student_channel_binding / jst_enroll_record / jst_cert_record
 *
 * @author jst
 * @since 1.0.0
 */
@Mapper
public interface ChannelSupplementLookupMapper {

    /**
     * 查询用户权益列表
     */
    List<Map<String, Object>> selectRightsByUserId(@Param("userId") Long userId);

    /**
     * 校验学生是否绑定到指定渠道
     */
    int countBindingForChannel(@Param("channelId") Long channelId, @Param("studentUserId") Long studentUserId);

    /**
     * 查询绑定学生的成绩
     */
    List<Map<String, Object>> selectStudentScores(@Param("studentUserId") Long studentUserId);

    /**
     * 查询绑定学生的证书
     */
    List<Map<String, Object>> selectStudentCerts(@Param("studentUserId") Long studentUserId);

    /**
     * 按 bindingId 查询绑定记录
     */
    Map<String, Object> selectBindingById(@Param("bindingId") Long bindingId);

    /**
     * 解绑：更新 binding 状态为 unbound（乐观锁）
     */
    int unbindByBindingId(@Param("bindingId") Long bindingId,
                          @Param("expectedStatus") String expectedStatus,
                          @Param("updateBy") String updateBy,
                          @Param("updateTime") Date updateTime);
}
