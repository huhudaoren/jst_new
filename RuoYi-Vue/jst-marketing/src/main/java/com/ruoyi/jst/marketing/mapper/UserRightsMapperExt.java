package com.ruoyi.jst.marketing.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User rights custom mapper.
 */
@Mapper
public interface UserRightsMapperExt {

    List<Map<String, Object>> selectMyRights(@Param("ownerType") String ownerType,
                                             @Param("ownerId") Long ownerId,
                                             @Param("status") String status,
                                             @Param("now") Date now);

    Map<String, Object> selectDetailOwned(@Param("userRightsId") Long userRightsId,
                                          @Param("ownerType") String ownerType,
                                          @Param("ownerId") Long ownerId,
                                          @Param("now") Date now);

    Map<String, Object> selectDetailOwnedForUpdate(@Param("userRightsId") Long userRightsId,
                                                   @Param("ownerType") String ownerType,
                                                   @Param("ownerId") Long ownerId,
                                                   @Param("now") Date now);

    List<Map<String, Object>> selectWriteoffHistory(@Param("userRightsId") Long userRightsId);

    /**
     * 查询当前用户的核销记录列表（跨权益）。解决前端 N+1 问题。
     *
     * @param ownerType   持有者类型（student / partner / channel）
     * @param ownerId     持有者 ID（= userId）
     * @param userRightsId 可选过滤具体权益
     * @param status      状态过滤（pending/approved/rejected/rolled_back/used/expired/invalid=used+expired/null 不过滤）
     * @return 核销记录列表（含关联权益名称 / 类型）
     */
    List<Map<String, Object>> selectMyWriteoffRecords(@Param("ownerType") String ownerType,
                                                       @Param("ownerId") Long ownerId,
                                                       @Param("userRightsId") Long userRightsId,
                                                       @Param("status") String status);
}
