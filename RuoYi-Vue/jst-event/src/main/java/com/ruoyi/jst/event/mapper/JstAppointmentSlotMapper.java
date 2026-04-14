package com.ruoyi.jst.event.mapper;

import com.ruoyi.jst.event.domain.JstAppointmentSlot;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 赛事预约时段 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface JstAppointmentSlotMapper {

    JstAppointmentSlot selectBySlotId(@Param("slotId") Long slotId);

    List<JstAppointmentSlot> selectByContestId(@Param("contestId") Long contestId);

    int insertJstAppointmentSlot(@Param("entity") JstAppointmentSlot entity);

    int batchInsert(@Param("list") List<JstAppointmentSlot> list);

    int updateJstAppointmentSlot(@Param("entity") JstAppointmentSlot entity);

    int deleteBySlotId(@Param("slotId") Long slotId);

    int deleteByContestId(@Param("contestId") Long contestId);

    /**
     * 乐观锁增加已预约人数。
     *
     * @param slotId    时段ID
     * @param increment 增量
     * @return 更新行数（0=容量满或已删除）
     */
    int incrementBookedCount(@Param("slotId") Long slotId, @Param("increment") int increment);

    /**
     * 查询赛事所有启用时间段列表。
     *
     * @param contestId 赛事ID
     * @return 启用时间段列表，按日期+开始时间排序
     */
    List<JstAppointmentSlot> selectEnabledByContestId(@Param("contestId") Long contestId);
}
