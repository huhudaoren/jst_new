package com.ruoyi.jst.organizer.service;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.system.service.ISysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 省级行政区字典校验服务（PATCH-5）
 * <p>
 * 用于 admin 编辑 region 等入口，确保写入值在 {@code jst_region_province} 字典内，
 * 防止绕过前端注入脏数据。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class RegionDictService {

    public static final String DICT_TYPE = "jst_region_province";

    @Autowired
    private ISysDictTypeService dictTypeService;

    /**
     * 校验 region 是否在省级行政区字典内。
     *
     * @param value region dict_value
     * @return true 在字典内
     */
    public boolean isValid(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        List<SysDictData> list = dictTypeService.selectDictDataByType(DICT_TYPE);
        if (list == null || list.isEmpty()) {
            return false;
        }
        return list.stream().anyMatch(d -> value.equals(d.getDictValue()));
    }
}
