package com.ruoyi.jst.event.service.impl;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.cache.JstCacheService;
import com.ruoyi.jst.event.mapper.HomeMapperExt;
import com.ruoyi.jst.event.service.HomeService;
import com.ruoyi.jst.event.vo.ContestListVO;
import com.ruoyi.jst.event.vo.CourseListVO;
import com.ruoyi.jst.event.vo.HomeHotTagVO;
import com.ruoyi.jst.event.vo.HomeTagSourceVO;
import com.ruoyi.jst.event.vo.HomeTopicVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * 小程序首页聚合服务实现。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class HomeServiceImpl implements HomeService {

    private static final int DEFAULT_RECOMMEND_CONTEST_LIMIT = 8;
    private static final int DEFAULT_RECOMMEND_COURSE_LIMIT = 6;
    private static final int DEFAULT_HOT_TAG_LIMIT = 12;
    private static final int DEFAULT_TOPIC_LIMIT = 3;
    private static final int HOT_TAG_SOURCE_LIMIT = 500;
    private static final int MAX_LIMIT = 20;
    private static final int TOPIC_DESCRIPTION_MAX_LEN = 120;
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]+>");

    @Autowired
    private HomeMapperExt homeMapperExt;

    @Autowired
    private JstCacheService jstCacheService;

    /**
     * 查询首页推荐赛事（缓存 3 分钟）。
     *
     * @return 推荐赛事列表
     * @关联表 jst_contest / jst_enroll_record
     * @关联权限 @Anonymous
     */
    @Override
    public List<ContestListVO> selectRecommendContests() {
        return jstCacheService.getOrLoad("cache:home:recommend-contests", 180,
                () -> homeMapperExt.selectRecommendContests(DEFAULT_RECOMMEND_CONTEST_LIMIT));
    }

    /**
     * 查询首页推荐课程（缓存 3 分钟）。
     *
     * @return 推荐课程列表
     * @关联表 jst_course
     * @关联权限 @Anonymous
     */
    @Override
    public List<CourseListVO> selectRecommendCourses() {
        return jstCacheService.getOrLoad("cache:home:recommend-courses", 180,
                () -> homeMapperExt.selectRecommendCourses(DEFAULT_RECOMMEND_COURSE_LIMIT));
    }

    /**
     * 查询首页热门标签（缓存 3 分钟）。
     *
     * @return 热门标签列表
     * @关联表 jst_contest
     * @关联权限 @Anonymous
     */
    @Override
    public List<HomeHotTagVO> selectHotTags() {
        return jstCacheService.getOrLoad("cache:home:hot-tags", 180, this::loadHotTags);
    }

    private List<HomeHotTagVO> loadHotTags() {
        List<HomeTagSourceVO> sources = homeMapperExt.selectHotTagSources(HOT_TAG_SOURCE_LIMIT);
        if (sources == null || sources.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Integer> tagCountMap = new TreeMap<>();
        boolean hasRecommendTags = false;
        for (HomeTagSourceVO source : sources) {
            List<String> tags = splitTags(source == null ? null : source.getRecommendTags());
            if (tags.isEmpty()) {
                continue;
            }
            hasRecommendTags = true;
            for (String tag : tags) {
                addCount(tagCountMap, tag);
            }
        }

        if (!hasRecommendTags) {
            for (HomeTagSourceVO source : sources) {
                if (source != null && StringUtils.isNotBlank(source.getCategory())) {
                    addCount(tagCountMap, source.getCategory().trim());
                }
            }
        }

        if (tagCountMap.isEmpty()) {
            return Collections.emptyList();
        }

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(tagCountMap.entrySet());
        entries.sort(Comparator.<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue)
                .reversed()
                .thenComparing(Map.Entry::getKey));

        List<HomeHotTagVO> result = new ArrayList<>();
        int safeLimit = normalizeLimit(DEFAULT_HOT_TAG_LIMIT);
        for (Map.Entry<String, Integer> entry : entries) {
            if (result.size() >= safeLimit) {
                break;
            }
            HomeHotTagVO vo = new HomeHotTagVO();
            vo.setTag(entry.getKey());
            vo.setCount(entry.getValue());
            result.add(vo);
        }
        return result;
    }

    /**
     * 查询首页专题活动（缓存 3 分钟）。
     *
     * @return 专题活动列表
     * @关联表 jst_notice
     * @关联权限 @Anonymous
     */
    @Override
    public List<HomeTopicVO> selectTopics() {
        return jstCacheService.getOrLoad("cache:home:topics", 180, this::loadTopics);
    }

    private List<HomeTopicVO> loadTopics() {
        List<HomeTopicVO> topics = homeMapperExt.selectTopics(DEFAULT_TOPIC_LIMIT);
        if (topics == null || topics.isEmpty()) {
            return Collections.emptyList();
        }
        for (HomeTopicVO topic : topics) {
            if (topic != null) {
                topic.setDescription(toPlainSummary(topic.getDescription()));
            }
        }
        return topics;
    }

    private int normalizeLimit(int limit) {
        if (limit <= 0) {
            return 1;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private void addCount(Map<String, Integer> tagCountMap, String tag) {
        Integer current = tagCountMap.get(tag);
        tagCountMap.put(tag, current == null ? 1 : current + 1);
    }

    private List<String> splitTags(String recommendTags) {
        if (StringUtils.isBlank(recommendTags)) {
            return Collections.emptyList();
        }
        String normalized = recommendTags.replace('，', ',').replace('、', ',');
        String[] segments = normalized.split(",");
        Set<String> tagSet = new LinkedHashSet<>();
        for (String segment : segments) {
            String tag = segment == null ? null : segment.trim();
            if (StringUtils.isNotBlank(tag)) {
                tagSet.add(tag);
            }
        }
        if (tagSet.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(tagSet);
    }

    private String toPlainSummary(String content) {
        if (StringUtils.isBlank(content)) {
            return "";
        }
        String plain = HTML_TAG_PATTERN.matcher(content).replaceAll("").replace("&nbsp;", " ").trim();
        if (plain.length() <= TOPIC_DESCRIPTION_MAX_LEN) {
            return plain;
        }
        return plain.substring(0, TOPIC_DESCRIPTION_MAX_LEN);
    }
}
