package com.ruoyi.jst.message.service.impl;

import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.html.EscapeUtil;
import com.ruoyi.jst.common.audit.OperateLog;
import com.ruoyi.jst.common.cache.JstCacheService;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.message.domain.JstNotice;
import com.ruoyi.jst.message.dto.NoticeQueryReqDTO;
import com.ruoyi.jst.message.dto.NoticeSaveReqDTO;
import com.ruoyi.jst.message.dto.WxNoticeQueryDTO;
import com.ruoyi.jst.message.mapper.DictLookupMapper;
import com.ruoyi.jst.message.mapper.JstNoticeMapper;
import com.ruoyi.jst.message.mapper.NoticeMapperExt;
import com.ruoyi.jst.message.service.NoticeService;
import com.ruoyi.jst.message.vo.BannerVO;
import com.ruoyi.jst.message.vo.WxNoticeCardVO;
import com.ruoyi.jst.message.vo.WxNoticeDetailVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 公告与首页展示领域服务实现。
 * <p>
 * 约束：
 * <ul>
 *   <li>公告写操作统一由本服务承接，事务边界在 Service 层</li>
 *   <li>公告状态流转不单独建状态机类，但必须显式校验允许状态</li>
 *   <li>业务字典按 jst:dict:{dictType} 缓存 1 小时</li>
 * </ul>
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    private static final Logger log = LoggerFactory.getLogger(NoticeServiceImpl.class);

    private static final int DICT_CACHE_TTL_HOURS = 1;
    private static final int BANNER_LIMIT = 5;
    private static final int SUMMARY_LENGTH = 60;

    @Autowired
    private JstNoticeMapper jstNoticeMapper;

    @Autowired
    private NoticeMapperExt noticeMapperExt;

    @Autowired
    private DictLookupMapper dictLookupMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private JstCacheService jstCacheService;

    /**
     * 管理后台新增公告。
     *
     * @param req 保存请求
     * @return 新公告ID
     * @关联表 jst_notice
     * @关联状态机 无
     * @关联权限 jst:message:notice:add
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "消息中心", action = "NOTICE_ADD", target = "#{req.title}")
    public Long addNotice(NoticeSaveReqDTO req) {
        // TX: NoticeService.addNotice
        JstNotice notice = new JstNotice();
        notice.setTitle(req.getTitle());
        notice.setCategory(req.getCategory());
        notice.setContent(cleanRichText(req.getContent()));
        notice.setCoverImage(req.getCoverImage());
        notice.setTopFlag(normalizeTopFlag(req.getTopFlag()));
        notice.setStatus("draft");
        notice.setCreateBy(currentOperatorName());
        notice.setCreateTime(DateUtils.getNowDate());
        notice.setUpdateBy(currentOperatorName());
        notice.setUpdateTime(DateUtils.getNowDate());
        notice.setDelFlag("0");
        jstNoticeMapper.insertJstNotice(notice);
        evictNoticeCache();
        return notice.getNoticeId();
    }

    /**
     * 管理后台编辑公告。
     *
     * @param req 编辑请求
     * @关联表 jst_notice
     * @关联状态机 无
     * @关联权限 jst:message:notice:edit
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "消息中心", action = "NOTICE_EDIT", target = "#{req.noticeId}")
    public void editNotice(NoticeSaveReqDTO req) {
        // TX: NoticeService.editNotice
        if (req.getNoticeId() == null) {
            throw new ServiceException("noticeId 不能为空", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        JstNotice current = requireNotice(req.getNoticeId());
        assertEditableStatus(current.getStatus());

        JstNotice updating = new JstNotice();
        updating.setNoticeId(req.getNoticeId());
        updating.setTitle(req.getTitle());
        updating.setCategory(req.getCategory());
        updating.setContent(cleanRichText(req.getContent()));
        updating.setCoverImage(req.getCoverImage());
        updating.setTopFlag(normalizeTopFlag(req.getTopFlag()));
        updating.setUpdateBy(currentOperatorName());
        updating.setUpdateTime(DateUtils.getNowDate());

        int updated = noticeMapperExt.updateEditableNotice(updating);
        if (updated == 0) {
            throw new ServiceException("公告编辑失败，当前状态不可编辑或已变更",
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        evictNoticeCache();
    }

    /**
     * 管理后台发布公告。
     *
     * @param noticeId 公告ID
     * @关联表 jst_notice
     * @关联状态机 无
     * @关联权限 jst:message:notice:publish
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "消息中心", action = "NOTICE_PUBLISH", target = "#{noticeId}")
    public void publishNotice(Long noticeId) {
        // TX: NoticeService.publishNotice
        JstNotice notice = requireNotice(noticeId);
        if (!"draft".equals(notice.getStatus())) {
            throw new ServiceException("仅草稿状态公告允许发布", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        int updated = noticeMapperExt.updateNoticeStatus(
                noticeId,
                "draft",
                "published",
                DateUtils.getNowDate(),
                currentOperatorName()
        );
        if (updated == 0) {
            throw new ServiceException("公告发布失败，状态已变更",
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        evictNoticeCache();
    }

    /**
     * 管理后台下线公告。
     *
     * @param noticeId 公告ID
     * @关联表 jst_notice
     * @关联状态机 无
     * @关联权限 jst:message:notice:offline
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "消息中心", action = "NOTICE_OFFLINE", target = "#{noticeId}")
    public void offlineNotice(Long noticeId) {
        // TX: NoticeService.offlineNotice
        JstNotice notice = requireNotice(noticeId);
        if (!"published".equals(notice.getStatus())) {
            throw new ServiceException("仅已发布状态公告允许下线", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        int updated = noticeMapperExt.updateNoticeStatus(
                noticeId,
                "published",
                "offline",
                null,
                currentOperatorName()
        );
        if (updated == 0) {
            throw new ServiceException("公告下线失败，状态已变更",
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        evictNoticeCache();
    }

    /**
     * 管理后台分页查询公告。
     *
     * @param query 查询条件
     * @return 公告列表
     * @关联表 jst_notice
     * @关联状态机 无
     * @关联权限 jst:message:notice:list
     */
    @Override
    public List<JstNotice> selectAdminNoticeList(NoticeQueryReqDTO query) {
        return noticeMapperExt.selectAdminNoticeList(query);
    }

    /**
     * 小程序端分页查询公告列表（缓存 5 分钟）。
     *
     * @param query 查询条件
     * @return 公告卡片列表
     * @关联表 jst_notice
     * @关联状态机 无
     * @关联权限 @Anonymous
     */
    @Override
    public List<WxNoticeCardVO> selectWxNoticeList(WxNoticeQueryDTO query) {
        String category = query == null ? null : query.getCategory();
        int pn = (query != null && query.getPageNum() != null) ? query.getPageNum() : 1;
        String key = "cache:notice:list:" + safeKeyPart(category) + ":" + pn;
        return jstCacheService.getOrLoad(key, 300, () -> loadWxNoticeList(category));
    }

    private List<WxNoticeCardVO> loadWxNoticeList(String category) {
        List<JstNotice> notices = noticeMapperExt.selectPublishedNoticeList(category);
        List<WxNoticeCardVO> result = new ArrayList<>(notices.size());
        for (JstNotice notice : notices) {
            WxNoticeCardVO vo = new WxNoticeCardVO();
            vo.setNoticeId(notice.getNoticeId());
            vo.setTitle(notice.getTitle());
            vo.setCategory(notice.getCategory());
            vo.setCoverImage(notice.getCoverImage());
            vo.setTopFlag(toBoolean(notice.getTopFlag()));
            vo.setPublishTime(notice.getPublishTime());
            vo.setSummary(buildSummary(notice.getContent()));
            result.add(vo);
        }
        return result;
    }

    /**
     * 小程序端查询公告详情。
     *
     * @param noticeId 公告ID
     * @return 公告详情
     * @关联表 jst_notice
     * @关联状态机 无
     * @关联权限 @Anonymous
     */
    @Override
    public WxNoticeDetailVO selectWxNoticeDetail(Long noticeId) {
        JstNotice notice = noticeMapperExt.selectPublishedNoticeById(noticeId);
        if (notice == null) {
            throw new ServiceException(BizErrorCode.JST_MSG_NOTICE_NOT_FOUND.message(),
                    BizErrorCode.JST_MSG_NOTICE_NOT_FOUND.code());
        }
        WxNoticeDetailVO vo = new WxNoticeDetailVO();
        vo.setNoticeId(notice.getNoticeId());
        vo.setTitle(notice.getTitle());
        vo.setCategory(notice.getCategory());
        vo.setCoverImage(notice.getCoverImage());
        vo.setTopFlag(toBoolean(notice.getTopFlag()));
        vo.setPublishTime(notice.getPublishTime());
        vo.setContent(notice.getContent());
        return vo;
    }

    /**
     * 查询首页 banner（缓存 3 分钟）。
     *
     * @return banner 列表
     * @关联表 jst_notice
     * @关联状态机 无
     * @关联权限 @Anonymous
     */
    @Override
    public List<BannerVO> selectBannerList() {
        return jstCacheService.getOrLoad("cache:home:banners", 180, this::loadBannerList);
    }

    private List<BannerVO> loadBannerList() {
        List<JstNotice> notices = noticeMapperExt.selectTopBannerNotices(BANNER_LIMIT);
        List<BannerVO> result = new ArrayList<>(notices.size());
        for (JstNotice notice : notices) {
            BannerVO vo = new BannerVO();
            vo.setId(notice.getNoticeId());
            vo.setType("notice");
            vo.setTitle(notice.getTitle());
            vo.setImage(notice.getCoverImage());
            vo.setLinkUrl("/pages-sub/notice/detail?id=" + notice.getNoticeId());
            result.add(vo);
        }
        return result;
    }

    /**
     * 查询业务字典下拉选项并缓存（缓存 30 分钟，含 TTL 抖动防雪崩）。
     *
     * @param dictType 字典类型
     * @return 字典项列表
     * @关联表 sys_dict_data
     * @关联状态机 无
     * @关联权限 @Anonymous
     */
    @Override
    public List<Map<String, Object>> selectDictOptions(String dictType) {
        if (!isAllowedDictType(dictType)) {
            return Collections.emptyList();
        }
        String cacheKey = "cache:dict:" + dictType;
        return jstCacheService.getOrLoad(cacheKey, 1800, () -> loadDictOptions(dictType));
    }

    private List<Map<String, Object>> loadDictOptions(String dictType) {
        List<SysDictData> rows = dictLookupMapper.selectEnabledByType(dictType);
        List<Map<String, Object>> result = new ArrayList<>(rows.size());
        for (SysDictData row : rows) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("label", row.getDictLabel());
            item.put("value", row.getDictValue());
            item.put("cssClass", row.getCssClass());
            result.add(item);
        }
        return result;
    }

    private JstNotice requireNotice(Long noticeId) {
        JstNotice notice = jstNoticeMapper.selectJstNoticeByNoticeId(noticeId);
        if (notice == null || "2".equals(notice.getDelFlag())) {
            throw new ServiceException(BizErrorCode.JST_MSG_NOTICE_NOT_FOUND.message(),
                    BizErrorCode.JST_MSG_NOTICE_NOT_FOUND.code());
        }
        return notice;
    }

    private void assertEditableStatus(String status) {
        if (!"draft".equals(status) && !"offline".equals(status)) {
            throw new ServiceException("仅草稿或已下线状态公告允许编辑", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
    }

    private String cleanRichText(String content) {
        return EscapeUtil.clean(content);
    }

    private Integer normalizeTopFlag(Integer topFlag) {
        return topFlag == null ? 0 : topFlag;
    }

    private Boolean toBoolean(Integer topFlag) {
        return topFlag != null && topFlag == 1;
    }

    private String buildSummary(String content) {
        if (StringUtils.isEmpty(content)) {
            return "";
        }
        String plain = content.replaceAll("<[^>]+>", "").replace("&nbsp;", " ").trim();
        if (plain.length() <= SUMMARY_LENGTH) {
            return plain;
        }
        return plain.substring(0, SUMMARY_LENGTH);
    }

    private boolean isAllowedDictType(String dictType) {
        return StringUtils.isNotEmpty(dictType) && dictType.startsWith("jst_");
    }

    private String currentOperatorName() {
        String username = SecurityUtils.getUsername();
        return StringUtils.isEmpty(username) ? "system" : username;
    }

    /**
     * 公告写操作后清除缓存（公告列表 + 首页 banner/专题）。
     */
    private void evictNoticeCache() {
        jstCacheService.evictByPrefix("cache:notice:");
        jstCacheService.evictByPrefix("cache:home:");
    }

    private String safeKeyPart(String value) {
        return StringUtils.isEmpty(value) ? "_all" : value;
    }
}
