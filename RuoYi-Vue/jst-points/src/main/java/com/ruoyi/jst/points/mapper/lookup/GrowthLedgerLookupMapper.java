package com.ruoyi.jst.points.mapper.lookup;

import com.ruoyi.jst.points.vo.GrowthLedgerVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 成长值流水 lookup Mapper。
 */
public interface GrowthLedgerLookupMapper {

    List<GrowthLedgerVO> selectMyGrowthLedger(@Param("userId") Long userId);
}
