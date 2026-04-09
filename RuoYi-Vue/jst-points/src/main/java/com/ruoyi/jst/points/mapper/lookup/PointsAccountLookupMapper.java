package com.ruoyi.jst.points.mapper.lookup;

import org.apache.ibatis.annotations.Param;
import com.ruoyi.jst.points.vo.PointsLedgerVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 积分账户轻量读写 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface PointsAccountLookupMapper {

    Map<String, Object> selectPointsSnapshot(@Param("userId") Long userId);

    int insertPointsAccount(@Param("userId") Long userId,
                            @Param("availablePoints") Long availablePoints,
                            @Param("frozenPoints") Long frozenPoints,
                            @Param("createBy") String createBy,
                            @Param("createTime") Date createTime);

    int freezePointsAccount(@Param("accountId") Long accountId,
                            @Param("version") Integer version,
                            @Param("points") Long points,
                            @Param("updateBy") String updateBy,
                            @Param("updateTime") Date updateTime);

    int consumePointsAccount(@Param("accountId") Long accountId,
                             @Param("version") Integer version,
                             @Param("points") Long points,
                             @Param("updateBy") String updateBy,
                             @Param("updateTime") Date updateTime);

    int unfreezePointsAccount(@Param("accountId") Long accountId,
                              @Param("version") Integer version,
                              @Param("points") Long points,
                              @Param("updateBy") String updateBy,
                              @Param("updateTime") Date updateTime);

    int refundPointsAccount(@Param("accountId") Long accountId,
                            @Param("version") Integer version,
                            @Param("points") Long points,
                            @Param("updateBy") String updateBy,
                            @Param("updateTime") Date updateTime);

    int freezeUserPoints(@Param("userId") Long userId,
                         @Param("expectedAvailable") Long expectedAvailable,
                         @Param("expectedFrozen") Long expectedFrozen,
                         @Param("points") Long points,
                         @Param("updateBy") String updateBy,
                         @Param("updateTime") Date updateTime);

    int consumeUserPoints(@Param("userId") Long userId,
                          @Param("expectedAvailable") Long expectedAvailable,
                          @Param("expectedFrozen") Long expectedFrozen,
                          @Param("points") Long points,
                          @Param("updateBy") String updateBy,
                          @Param("updateTime") Date updateTime);

    int unfreezeUserPoints(@Param("userId") Long userId,
                           @Param("expectedAvailable") Long expectedAvailable,
                           @Param("expectedFrozen") Long expectedFrozen,
                           @Param("points") Long points,
                           @Param("updateBy") String updateBy,
                           @Param("updateTime") Date updateTime);

    int refundUserPoints(@Param("userId") Long userId,
                         @Param("expectedAvailable") Long expectedAvailable,
                         @Param("expectedFrozen") Long expectedFrozen,
                         @Param("points") Long points,
                         @Param("updateBy") String updateBy,
                         @Param("updateTime") Date updateTime);

    int insertPointsLedger(@Param("accountId") Long accountId,
                           @Param("ownerType") String ownerType,
                           @Param("ownerId") Long ownerId,
                           @Param("changeType") String changeType,
                           @Param("sourceType") String sourceType,
                           @Param("sourceRefId") Long sourceRefId,
                           @Param("pointsChange") Long pointsChange,
                           @Param("balanceAfter") Long balanceAfter,
                           @Param("operatorId") Long operatorId,
                           @Param("createBy") String createBy,
                           @Param("createTime") Date createTime,
                           @Param("remark") String remark);

    int insertRefundLedger(@Param("accountId") Long accountId,
                           @Param("ownerType") String ownerType,
                           @Param("ownerId") Long ownerId,
                           @Param("sourceRefId") Long sourceRefId,
                           @Param("pointsChange") Long pointsChange,
                           @Param("balanceAfter") Long balanceAfter,
                           @Param("operatorId") Long operatorId,
                           @Param("createBy") String createBy,
                           @Param("createTime") Date createTime,
                           @Param("remark") String remark);

    List<PointsLedgerVO> selectMyPointsLedger(@Param("userId") Long userId,
                                              @Param("changeType") String changeType);
}
