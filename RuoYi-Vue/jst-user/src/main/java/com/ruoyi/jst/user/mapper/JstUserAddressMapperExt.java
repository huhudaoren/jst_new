package com.ruoyi.jst.user.mapper;

import com.ruoyi.jst.user.domain.JstUserAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户地址扩展 Mapper
 */
@Mapper
public interface JstUserAddressMapperExt {

    List<JstUserAddress> selectListByUserId(@Param("userId") Long userId);

    JstUserAddress selectByIdAndUserId(@Param("addressId") Long addressId, @Param("userId") Long userId);

    int insertAddress(JstUserAddress address);

    int updateAddress(JstUserAddress address);

    int softDeleteByIdAndUserId(@Param("addressId") Long addressId,
                                @Param("userId") Long userId,
                                @Param("updateBy") String updateBy);

    int clearDefaultByUserId(@Param("userId") Long userId, @Param("updateBy") String updateBy);

    int setDefaultByIdAndUserId(@Param("addressId") Long addressId,
                                @Param("userId") Long userId,
                                @Param("updateBy") String updateBy);
}
