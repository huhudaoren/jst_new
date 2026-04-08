package com.ruoyi.jst.message.mapper;

import com.ruoyi.common.core.domain.entity.SysDictData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 业务字典轻量查询 Mapper。
 * <p>
 * 用途：jst-message 不依赖 ruoyi-system，直接跨表只读查询 sys_dict_data。
 * 强约束：仅允许 SELECT，禁止任何写操作。
 *
 * @author jst
 * @since 1.0.0
 */
@Mapper
public interface DictLookupMapper {

    /**
     * 查询启用中的业务字典项。
     *
     * @param dictType 字典类型
     * @return 字典项列表
     */
    @Select("SELECT dict_code AS dictCode, dict_sort AS dictSort, dict_label AS dictLabel, " +
            "dict_value AS dictValue, dict_type AS dictType, css_class AS cssClass, " +
            "list_class AS listClass, is_default AS isDefault, status " +
            "FROM sys_dict_data " +
            "WHERE dict_type = #{dictType} AND status = '0' " +
            "ORDER BY dict_sort ASC, dict_code ASC")
    List<SysDictData> selectEnabledByType(@Param("dictType") String dictType);
}
