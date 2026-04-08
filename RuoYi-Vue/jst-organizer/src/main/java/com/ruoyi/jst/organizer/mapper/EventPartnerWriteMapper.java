package com.ruoyi.jst.organizer.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.Map;

/**
 * 赛事方档案写入轻量 Mapper。
 * <p>
 * 仅负责 jst-organizer 模块内对 jst_event_partner 的最小写操作，
 * 避免跨模块依赖 jst-event 的 Entity / Service。
 *
 * @author jst
 * @since 1.0.0
 */
public interface EventPartnerWriteMapper {

    /**
     * 新增赛事方档案。
     *
     * @param partner 档案字段
     * @return 影响行数
     */
    int insertEventPartner(@Param("partner") Map<String, Object> partner);

    /**
     * 回填赛事方关联账号。
     *
     * @param partnerId 赛事方ID
     * @param userId 用户ID
     * @param updateBy 更新人
     * @param updateTime 更新时间
     * @return 影响行数
     */
    int updatePartnerUserId(@Param("partnerId") Long partnerId,
                            @Param("userId") Long userId,
                            @Param("updateBy") String updateBy,
                            @Param("updateTime") Date updateTime);
}
