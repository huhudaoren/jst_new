package com.ruoyi.jst.points.mapper.lookup;

import com.ruoyi.jst.points.vo.LevelVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 等级配置 lookup Mapper。
 */
public interface LevelConfigLookupMapper {

    List<LevelVO> selectEnabledLevels(@Param("role") String role);
}
