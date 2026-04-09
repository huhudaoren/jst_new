package com.ruoyi.jst.marketing.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Rights template custom mapper.
 */
@Mapper
public interface RightsTemplateMapperExt {

    List<Map<String, Object>> selectAdminList(@Param("rightsName") String rightsName,
                                              @Param("rightsType") String rightsType,
                                              @Param("status") Integer status);

    int updateStatus(@Param("rightsTemplateId") Long rightsTemplateId,
                     @Param("status") Integer status,
                     @Param("updateBy") String updateBy,
                     @Param("updateTime") Date updateTime);

    int markDeleted(@Param("rightsTemplateId") Long rightsTemplateId,
                    @Param("updateBy") String updateBy,
                    @Param("updateTime") Date updateTime);
}
