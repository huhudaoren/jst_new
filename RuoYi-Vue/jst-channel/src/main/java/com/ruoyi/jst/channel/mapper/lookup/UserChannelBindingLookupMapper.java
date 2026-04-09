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

    List<DashboardStudentVO> selectStudentList(ChannelDashboardQueryDTO query);
}
