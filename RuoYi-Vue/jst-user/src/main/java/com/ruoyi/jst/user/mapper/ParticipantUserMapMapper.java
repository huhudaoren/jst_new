package com.ruoyi.jst.user.mapper;

import com.ruoyi.jst.user.domain.ParticipantUserMap;
import com.ruoyi.jst.user.dto.ParticipantUserMapQueryReqDTO;
import com.ruoyi.jst.user.vo.ParticipantUserMapAdminResVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 参赛档案-用户认领映射 Mapper
 *
 * @author jst
 * @since 1.0.0
 */
@Mapper
public interface ParticipantUserMapMapper {

    int insert(ParticipantUserMap map);

    /** 查询某个 participant 的活跃映射 */
    ParticipantUserMap selectActiveByParticipantId(@Param("participantId") Long participantId);

    /** 查询某个 user 已认领的全部档案 */
    List<ParticipantUserMap> selectActiveByUserId(@Param("userId") Long userId);

    /** 撤销映射(状态置 revoked) */
    int revoke(@Param("mapId") Long mapId, @Param("revokeReason") String revokeReason);

    /**
     * 管理端查询认领映射列表（支持 participantId / userId 过滤）。
     *
     * @param query 查询条件
     * @return 列表
     */
    List<ParticipantUserMapAdminResVO> selectAdminList(ParticipantUserMapQueryReqDTO query);
}
