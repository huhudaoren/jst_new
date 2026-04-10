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

    /**
     * 查询可自动发放的权益模板（Q-05）
     * <p>
     * 条件：status=1 + del_flag='0' + applicable_role 包含指定角色
     *
     * @param applicableRole 适用角色，如 "channel"
     * @return 权益模板列表（含 rightsTemplateId, quotaValue 等）
     */
    List<Map<String, Object>> selectAutoGrantTemplates(@Param("applicableRole") String applicableRole);
}
