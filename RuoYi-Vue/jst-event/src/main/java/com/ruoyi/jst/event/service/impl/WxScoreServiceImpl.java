package com.ruoyi.jst.event.service.impl;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.support.SchemaInspector;
import com.ruoyi.jst.event.dto.PublicScoreQueryDTO;
import com.ruoyi.jst.event.dto.WxScoreQueryDTO;
import com.ruoyi.jst.event.mapper.WxScoreMapper;
import com.ruoyi.jst.event.service.WxScoreService;
import com.ruoyi.jst.event.vo.PublicScoreVO;
import com.ruoyi.jst.event.vo.WxScoreVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mini-program score service implementation.
 */
@Service
public class WxScoreServiceImpl implements WxScoreService {

    private static final String SCORE_TABLE = "jst_score_record";
    private static final String CERT_TABLE = "jst_cert_record";
    private static final String ENROLL_TABLE = "jst_enroll_record";
    private static final String CONTEST_TABLE = "jst_contest";
    private static final String PARTICIPANT_TABLE = "jst_participant";

    @Autowired
    private WxScoreMapper wxScoreMapper;

    @Autowired
    private SchemaInspector schemaInspector;

    @Override
    public List<WxScoreVO> selectMyScores(Long userId, WxScoreQueryDTO query) {
        if (userId == null) {
            return Collections.emptyList();
        }
        Long contestId = query == null ? null : query.getContestId();
        List<WxScoreVO> list;
        if (canUseScoreTable()) {
            list = wxScoreMapper.selectMyScoreList(userId, contestId);
            fillCertFlags(list);
        } else if (canUseScoreFallback()) {
            list = wxScoreMapper.selectMyScoreFallbackList(userId, contestId);
            fillDefaults(list);
        } else {
            list = Collections.emptyList();
        }
        return list == null ? Collections.emptyList() : list;
    }

    @Override
    public List<PublicScoreVO> queryPublicScores(PublicScoreQueryDTO query) {
        PublicScoreQueryDTO normalized = normalizePublicQuery(query);
        if (isPublicQueryEmpty(normalized)) {
            return Collections.emptyList();
        }
        List<PublicScoreVO> list;
        if (canUsePublicScoreTable()) {
            list = wxScoreMapper.selectPublicScoreList(normalized);
        } else if (canUsePublicScoreFallback()) {
            list = wxScoreMapper.selectPublicScoreFallbackList(normalized);
        } else {
            list = Collections.emptyList();
        }
        return list == null ? Collections.emptyList() : list;
    }

    private void fillCertFlags(List<WxScoreVO> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        fillDefaults(list);
        if (!schemaInspector.hasColumns(CERT_TABLE, "score_id", "issue_status", "del_flag")) {
            return;
        }
        List<Long> scoreIds = list.stream()
                .map(WxScoreVO::getScoreId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (scoreIds.isEmpty()) {
            return;
        }
        Set<Long> issuedScoreIds = new HashSet<>(wxScoreMapper.selectIssuedCertScoreIds(scoreIds));
        for (WxScoreVO vo : list) {
            vo.setHasCert(vo.getScoreId() != null && issuedScoreIds.contains(vo.getScoreId()));
        }
    }

    private void fillDefaults(List<WxScoreVO> list) {
        for (WxScoreVO vo : list) {
            if (vo.getScore() == null) {
                vo.setScore(vo.getScoreValue());
            }
            if (vo.getRank() == null) {
                vo.setRank(vo.getRankNo());
            }
            if (vo.getHasCert() == null) {
                vo.setHasCert(Boolean.FALSE);
            }
            if (StringUtils.isBlank(vo.getScoreLabel())) {
                vo.setScoreLabel("总分");
            }
        }
    }

    private PublicScoreQueryDTO normalizePublicQuery(PublicScoreQueryDTO source) {
        PublicScoreQueryDTO target = source == null ? new PublicScoreQueryDTO() : source;
        target.setName(trimToNull(target.getName()));
        target.setMobile(trimToNull(target.getMobile()));
        target.setIdNo(trimToNull(target.getIdNo()));
        target.setIdCard4(trimToNull(target.getIdCard4()));
        target.setEnrollNo(trimToNull(target.getEnrollNo()));
        target.setContestId(trimToNull(target.getContestId()));
        if (StringUtils.isBlank(target.getIdCard4())
                && StringUtils.isNotBlank(target.getIdNo())
                && target.getIdNo().length() >= 4) {
            target.setIdCard4(target.getIdNo().substring(target.getIdNo().length() - 4));
        }
        if (StringUtils.isNotBlank(target.getContestId())) {
            if (StringUtils.isNumeric(target.getContestId())) {
                target.setContestIdNum(Long.valueOf(target.getContestId()));
                target.setContestKeyword(null);
            } else {
                target.setContestIdNum(null);
                target.setContestKeyword(target.getContestId());
            }
        }
        if (StringUtils.isBlank(target.getMobile())
                && StringUtils.isBlank(target.getIdCard4())
                && StringUtils.isBlank(target.getEnrollNo())
                && StringUtils.isNotBlank(target.getName())) {
            target.setKeyword(target.getName());
            target.setName(null);
        } else if (StringUtils.isNotBlank(target.getEnrollNo())) {
            target.setKeyword(target.getEnrollNo());
        } else {
            target.setKeyword(null);
        }
        return target;
    }

    private boolean isPublicQueryEmpty(PublicScoreQueryDTO query) {
        return query == null || (StringUtils.isBlank(query.getName())
                && StringUtils.isBlank(query.getMobile())
                && StringUtils.isBlank(query.getIdCard4())
                && StringUtils.isBlank(query.getKeyword()));
    }

    private boolean canUseScoreTable() {
        return schemaInspector.hasColumns(SCORE_TABLE,
                "score_id", "enroll_id", "contest_id", "user_id", "score_value",
                "award_level", "rank_no", "publish_status", "publish_time", "remark", "del_flag")
                && schemaInspector.hasColumns(CONTEST_TABLE, "contest_id", "contest_name", "category", "del_flag");
    }

    private boolean canUseScoreFallback() {
        return schemaInspector.hasColumns(ENROLL_TABLE,
                "enroll_id", "contest_id", "user_id", "score_value", "award", "publish_time", "remark", "del_flag")
                && schemaInspector.hasColumns(CONTEST_TABLE, "contest_id", "contest_name", "category", "del_flag");
    }

    private boolean canUsePublicScoreTable() {
        return canUseScoreTable()
                && schemaInspector.hasColumns(PARTICIPANT_TABLE,
                "participant_id", "name", "guardian_mobile", "id_card_no", "del_flag")
                && schemaInspector.hasColumns(ENROLL_TABLE, "enroll_id", "enroll_no", "del_flag");
    }

    private boolean canUsePublicScoreFallback() {
        return canUseScoreFallback()
                && schemaInspector.hasColumns(PARTICIPANT_TABLE,
                "participant_id", "name", "guardian_mobile", "id_card_no", "del_flag")
                && schemaInspector.hasColumns(ENROLL_TABLE,
                "enroll_id", "enroll_no", "participant_id", "contest_id", "user_id",
                "score_value", "award", "publish_time", "remark", "del_flag");
    }

    private String trimToNull(String value) {
        String trimmed = StringUtils.trim(value);
        return StringUtils.isBlank(trimmed) ? null : trimmed;
    }
}
