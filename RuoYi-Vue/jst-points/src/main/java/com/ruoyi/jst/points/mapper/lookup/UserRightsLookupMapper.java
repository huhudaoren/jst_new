package com.ruoyi.jst.points.mapper.lookup;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 用户权益发放 lookup Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface UserRightsLookupMapper {

    Map<String, Object> selectRightsTemplate(@Param("rightsTemplateId") Long rightsTemplateId);

    int insertUserRights(@Param("rightsTemplateId") Long rightsTemplateId,
                         @Param("ownerType") String ownerType,
                         @Param("ownerId") Long ownerId,
                         @Param("sourceType") String sourceType,
                         @Param("sourceRefId") Long sourceRefId,
                         @Param("remainQuota") BigDecimal remainQuota,
                         @Param("validStart") Date validStart,
                         @Param("validEnd") Date validEnd,
                         @Param("status") String status,
                         @Param("createBy") String createBy,
                         @Param("createTime") Date createTime,
                         @Param("remark") String remark);

    int countConsumedRightsByExchangeId(@Param("exchangeId") Long exchangeId);

    int revokeRightsByExchangeId(@Param("exchangeId") Long exchangeId,
                                 @Param("updateBy") String updateBy,
                                 @Param("updateTime") Date updateTime);
}
