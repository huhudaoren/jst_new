package com.ruoyi.jst.channel.mapper.lookup;

import com.ruoyi.jst.channel.dto.ChannelDashboardQueryDTO;
import com.ruoyi.jst.channel.vo.DashboardStudentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Read-only lookup for student-channel bindings.
 */
@Mapper
public interface UserChannelBindingLookupMapper {

    Long countNewStudentByMonth(@Param("channelId") Long channelId,
                                @Param("startTime") Date startTime,
                                @Param("endTime") Date endTime);

    /**
     * 统计当前渠道全部活跃（active）且未删除的绑定学生数（Round 2A A1）。
     *
     * @param channelId 渠道ID
     * @return 累计绑定学生数
     */
    Long countTotalStudentByChannel(@Param("channelId") Long channelId);

    List<DashboardStudentVO> selectStudentList(ChannelDashboardQueryDTO query);
}
