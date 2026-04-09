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
}
