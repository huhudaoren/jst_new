package com.ruoyi.jst.points.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.context.JstLoginContext;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.points.mapper.lookup.GrowthLedgerLookupMapper;
import com.ruoyi.jst.points.mapper.lookup.LevelConfigLookupMapper;
import com.ruoyi.jst.points.mapper.lookup.PointsAccountLookupMapper;
import com.ruoyi.jst.points.service.PointsCenterService;
import com.ruoyi.jst.points.vo.GrowthLedgerVO;
import com.ruoyi.jst.points.vo.LevelVO;
import com.ruoyi.jst.points.vo.PointsCenterSummaryVO;
import com.ruoyi.jst.points.vo.PointsLedgerVO;
import com.ruoyi.jst.points.vo.PointsTaskVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 积分中心服务实现。
 */
@Service
public class PointsCenterServiceImpl implements PointsCenterService {

    @Autowired
    private PointsAccountLookupMapper pointsAccountLookupMapper;
    @Autowired
    private LevelConfigLookupMapper levelConfigLookupMapper;
    @Autowired
    private GrowthLedgerLookupMapper growthLedgerLookupMapper;

    @Override
    public PointsCenterSummaryVO getSummary(String role) {
        Long userId = currentUserId();
        Map<String, Object> snapshot = pointsAccountLookupMapper.selectPointsSnapshot(userId);
        String resolvedRole = normalizeRole(role);
        List<LevelVO> levels = markUnlocked(levelConfigLookupMapper.selectEnabledLevels(resolvedRole),
                longValue(snapshot == null ? null : snapshot.get("growthValue")));
        LevelVO currentLevel = levels.stream()
                .filter(level -> Boolean.TRUE.equals(level.getUnlocked()))
                .reduce((first, second) -> second)
                .orElse(levels.isEmpty() ? null : levels.get(0));
        LevelVO nextLevel = levels.stream()
                .filter(level -> currentLevel == null || level.getLevelNo() > currentLevel.getLevelNo())
                .filter(level -> level.getGrowthThreshold() != null
                        && level.getGrowthThreshold() > longValue(snapshot == null ? null : snapshot.get("growthValue")))
                .findFirst()
                .orElse(null);

        PointsCenterSummaryVO vo = new PointsCenterSummaryVO();
        vo.setAvailablePoints(longValue(snapshot == null ? null : snapshot.get("userAvailablePoints")));
        vo.setFrozenPoints(longValue(snapshot == null ? null : snapshot.get("userFrozenPoints")));
        vo.setTotalEarn(longValue(snapshot == null ? null : snapshot.get("totalEarn")));
        vo.setTotalSpend(longValue(snapshot == null ? null : snapshot.get("totalSpend")));
        vo.setGrowthValue(longValue(snapshot == null ? null : snapshot.get("growthValue")));
        vo.setCurrentLevel(currentLevel);
        vo.setNextLevel(nextLevel);
        vo.setPointsToNextLevel(nextLevel == null ? 0L :
                Math.max(0L, nextLevel.getGrowthThreshold() - vo.getGrowthValue()));
        return vo;
    }

    @Override
    public List<LevelVO> getLevels(String role) {
        Long userId = currentUserId();
        Map<String, Object> snapshot = pointsAccountLookupMapper.selectPointsSnapshot(userId);
        return markUnlocked(levelConfigLookupMapper.selectEnabledLevels(normalizeRole(role)),
                longValue(snapshot == null ? null : snapshot.get("growthValue")));
    }

    @Override
    public List<PointsLedgerVO> getPointsLedger(String changeType) {
        return pointsAccountLookupMapper.selectMyPointsLedger(currentUserId(), changeType);
    }

    @Override
    public List<GrowthLedgerVO> getGrowthLedger() {
        return growthLedgerLookupMapper.selectMyGrowthLedger(currentUserId());
    }

    @Override
    public List<PointsTaskVO> getTasks() {
        List<PointsTaskVO> tasks = new ArrayList<>();
        tasks.add(buildTask("daily_sign", "每日签到", "连续签到有惊喜奖励", "📅", "已完成", true, null));
        tasks.add(buildTask("contest_enroll", "报名参赛", "每成功报名一场赛事可获积分", "🏆", "+100~500", false, "/pages/contest/list"));
        tasks.add(buildTask("course_buy", "购买课程", "购买课程获取积分与成长值", "📚", "+50起", false, "/pages/course/list"));
        tasks.add(buildTask("ai_class", "完成AI课堂学习", "完成一门AI课程后获取积分", "🤖", "+80", false, "/pages/course/my"));
        tasks.add(buildTask("award_bonus", "获奖奖励", "赛事获奖后自动发放额外积分", "🥇", "+200~1000", false, null));
        return tasks;
    }

    private PointsTaskVO buildTask(String taskCode, String taskName, String taskDesc, String icon,
                                   String rewardText, boolean finished, String actionPath) {
        PointsTaskVO task = new PointsTaskVO();
        task.setTaskCode(taskCode);
        task.setTaskName(taskName);
        task.setTaskDesc(taskDesc);
        task.setIcon(icon);
        task.setRewardText(rewardText);
        task.setFinished(finished);
        task.setActionPath(actionPath);
        return task;
    }

    private List<LevelVO> markUnlocked(List<LevelVO> levels, Long growthValue) {
        if (levels == null || levels.isEmpty()) {
            return Collections.emptyList();
        }
        long currentGrowth = growthValue == null ? 0L : growthValue;
        for (LevelVO level : levels) {
            level.setUnlocked(level.getGrowthThreshold() == null || currentGrowth >= level.getGrowthThreshold());
        }
        return levels;
    }

    private String normalizeRole(String role) {
        if ("teacher".equalsIgnoreCase(StringUtils.trim(role))) {
            return "channel";
        }
        if ("student".equalsIgnoreCase(StringUtils.trim(role))
                || "channel".equalsIgnoreCase(StringUtils.trim(role))
                || "both".equalsIgnoreCase(StringUtils.trim(role))) {
            return StringUtils.trim(role);
        }
        if (JstLoginContext.hasRole("jst_channel")) {
            return "channel";
        }
        return "student";
    }

    private Long currentUserId() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        return userId;
    }

    private Long longValue(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        String text = String.valueOf(value);
        return StringUtils.isBlank(text) ? 0L : Long.valueOf(text);
    }
}
