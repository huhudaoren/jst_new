package com.ruoyi.jst.message.mapper;

import com.ruoyi.jst.message.domain.JstNotice;
import com.ruoyi.jst.message.dto.NoticeQueryReqDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 公告扩展 Mapper。
 * <p>
 * 用于承接公告管理端列表、状态流转，以及小程序端公告/banner 查询。
 *
 * @author jst
 * @since 1.0.0
 */
@Mapper
public interface NoticeMapperExt {

    /**
     * 管理后台查询公告列表。
     *
     * @param query 查询条件
     * @return 公告列表
     */
    List<JstNotice> selectAdminNoticeList(NoticeQueryReqDTO query);

    /**
     * 编辑公告基础字段，仅允许草稿/已下线状态。
     *
     * @param notice 公告实体
     * @return 影响行数
     */
    int updateEditableNotice(JstNotice notice);

    /**
     * 按预期状态更新公告状态。
     *
     * @param noticeId 目标公告ID
     * @param expectedStatus 预期旧状态
     * @param newStatus 新状态
     * @param publishTime 发布时间
     * @param updateBy 更新人
     * @return 影响行数
     */
    int updateNoticeStatus(@Param("noticeId") Long noticeId,
                           @Param("expectedStatus") String expectedStatus,
                           @Param("newStatus") String newStatus,
                           @Param("publishTime") Date publishTime,
                           @Param("updateBy") String updateBy);

    /**
     * 小程序端查询已发布公告列表。
     *
     * @param category 分类
     * @return 公告列表
     */
    List<JstNotice> selectPublishedNoticeList(@Param("category") String category);

    /**
     * 小程序端查询已发布公告详情。
     *
     * @param noticeId 公告ID
     * @return 公告详情
     */
    JstNotice selectPublishedNoticeById(@Param("noticeId") Long noticeId);

    /**
     * 查询首页 banner 公告。
     *
     * @param limit 返回条数
     * @return 置顶且已发布的公告
     */
    List<JstNotice> selectTopBannerNotices(@Param("limit") Integer limit);
}
