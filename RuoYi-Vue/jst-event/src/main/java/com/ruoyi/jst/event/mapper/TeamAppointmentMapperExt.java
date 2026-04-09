package com.ruoyi.jst.event.mapper;

import com.ruoyi.jst.event.domain.EventTeamAppointment;
import com.ruoyi.jst.event.domain.EventTeamAppointmentMember;
import com.ruoyi.jst.event.dto.TeamAppointmentQueryDTO;
import com.ruoyi.jst.event.vo.TeamAppointmentDetailVO;
import com.ruoyi.jst.event.vo.TeamAppointmentListVO;
import com.ruoyi.jst.event.vo.TeamAppointmentMemberVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 团队预约扩展 Mapper。
 */
public interface TeamAppointmentMapperExt {

    int insertTeamAppointment(@Param("team") EventTeamAppointment team);

    int insertTeamMember(@Param("member") EventTeamAppointmentMember member);

    Map<String, Object> selectParticipantById(@Param("participantId") Long participantId);

    Map<String, Object> selectParticipantByUserId(@Param("userId") Long userId);

    int countBookedPersons(@Param("contestId") Long contestId,
                           @Param("appointmentDate") Date appointmentDate,
                           @Param("sessionCode") String sessionCode);

    int countDuplicateAppointment(@Param("contestId") Long contestId,
                                  @Param("participantId") Long participantId);

    List<TeamAppointmentListVO> selectChannelList(@Param("channelId") Long channelId,
                                                  @Param("query") TeamAppointmentQueryDTO query);

    TeamAppointmentDetailVO selectChannelDetail(@Param("channelId") Long channelId,
                                                @Param("teamAppointmentId") Long teamAppointmentId);

    List<TeamAppointmentMemberVO> selectChannelMembers(@Param("teamAppointmentId") Long teamAppointmentId);

    EventTeamAppointment selectTeamById(@Param("teamAppointmentId") Long teamAppointmentId);

    int updateWriteoffProgress(@Param("teamAppointmentId") Long teamAppointmentId,
                               @Param("expectedStatus") String expectedStatus,
                               @Param("expectedWriteoffPersons") Long expectedWriteoffPersons,
                               @Param("targetStatus") String targetStatus,
                               @Param("targetWriteoffPersons") Long targetWriteoffPersons,
                               @Param("updateBy") String updateBy,
                               @Param("updateTime") Date updateTime);

    int updateMemberWriteoffStatus(@Param("teamAppointmentId") Long teamAppointmentId,
                                   @Param("participantId") Long participantId,
                                   @Param("expectedStatus") String expectedStatus,
                                   @Param("targetStatus") String targetStatus,
                                   @Param("updateBy") String updateBy,
                                   @Param("updateTime") Date updateTime);
}
