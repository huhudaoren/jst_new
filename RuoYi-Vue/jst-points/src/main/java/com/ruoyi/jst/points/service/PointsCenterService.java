package com.ruoyi.jst.points.service;

import com.ruoyi.jst.points.vo.GrowthLedgerVO;
import com.ruoyi.jst.points.vo.LevelVO;
import com.ruoyi.jst.points.vo.PointsCenterSummaryVO;
import com.ruoyi.jst.points.vo.PointsLedgerVO;
import com.ruoyi.jst.points.vo.PointsTaskVO;

import java.util.List;

/**
 * 积分中心服务。
 */
public interface PointsCenterService {

    PointsCenterSummaryVO getSummary(String role);

    List<LevelVO> getLevels(String role);

    List<PointsLedgerVO> getPointsLedger(String changeType);

    List<GrowthLedgerVO> getGrowthLedger();

    List<PointsTaskVO> getTasks();
}
