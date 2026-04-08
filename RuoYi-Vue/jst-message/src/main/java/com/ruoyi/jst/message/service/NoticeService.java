package com.ruoyi.jst.message.service;

import com.ruoyi.jst.message.domain.JstNotice;
import com.ruoyi.jst.message.dto.NoticeQueryReqDTO;
import com.ruoyi.jst.message.dto.NoticeSaveReqDTO;
import com.ruoyi.jst.message.dto.WxNoticeQueryDTO;
import com.ruoyi.jst.message.vo.BannerVO;
import com.ruoyi.jst.message.vo.WxNoticeCardVO;
import com.ruoyi.jst.message.vo.WxNoticeDetailVO;

import java.util.List;
import java.util.Map;

/**
 * 公告与首页展示领域服务。
 *
 * @author jst
 * @since 1.0.0
 */
public interface NoticeService {

    /**
     * 管理后台新增公告，默认创建为草稿。
     *
     * @param req 保存请求
     * @return 新公告ID
     * @关联表 jst_notice
     * @关联状态机 无
     * @关联权限 jst:message:notice:add
     */
    Long addNotice(NoticeSaveReqDTO req);

    /**
     * 管理后台编辑公告，仅允许草稿/已下线状态。
     *
     * @param req 编辑请求
     * @关联表 jst_notice
     * @关联状态机 无
     * @关联权限 jst:message:notice:edit
     */
    void editNotice(NoticeSaveReqDTO req);

    /**
     * 管理后台发布公告。
     *
     * @param noticeId 公告ID
     * @关联表 jst_notice
     * @关联状态机 无
     * @关联权限 jst:message:notice:publish
     */
    void publishNotice(Long noticeId);

    /**
     * 管理后台下线公告。
     *
     * @param noticeId 公告ID
     * @关联表 jst_notice
     * @关联状态机 无
     * @关联权限 jst:message:notice:offline
     */
    void offlineNotice(Long noticeId);

    /**
     * 管理后台分页查询公告。
     *
     * @param query 查询条件
     * @return 公告列表
     * @关联表 jst_notice
     * @关联状态机 无
     * @关联权限 jst:message:notice:list
     */
    List<JstNotice> selectAdminNoticeList(NoticeQueryReqDTO query);

    /**
     * 小程序端分页查询已发布公告。
     *
     * @param query 查询条件
     * @return 公告卡片列表
     * @关联表 jst_notice
     * @关联状态机 无
     * @关联权限 @Anonymous
     */
    List<WxNoticeCardVO> selectWxNoticeList(WxNoticeQueryDTO query);

    /**
     * 小程序端查询公告详情。
     *
     * @param noticeId 公告ID
     * @return 公告详情
     * @关联表 jst_notice
     * @关联状态机 无
     * @关联权限 @Anonymous
     */
    WxNoticeDetailVO selectWxNoticeDetail(Long noticeId);

    /**
     * 查询首页 banner 列表。
     *
     * @return banner 列表
     * @关联表 jst_notice
     * @关联状态机 无
     * @关联权限 @Anonymous
     */
    List<BannerVO> selectBannerList();

    /**
     * 查询业务字典下拉选项，并按 jst:dict:{dictType} 缓存 1 小时。
     *
     * @param dictType 字典类型
     * @return 字典项列表
     * @关联表 sys_dict_data
     * @关联状态机 无
     * @关联权限 @Anonymous
     */
    List<Map<String, Object>> selectDictOptions(String dictType);
}
