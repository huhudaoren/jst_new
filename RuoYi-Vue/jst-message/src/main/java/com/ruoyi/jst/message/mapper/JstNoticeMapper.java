package com.ruoyi.jst.message.mapper;

import java.util.List;
import com.ruoyi.jst.message.domain.JstNotice;

/**
 * 公告Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstNoticeMapper 
{
    /**
     * 查询公告
     * 
     * @param noticeId 公告主键
     * @return 公告
     */
    public JstNotice selectJstNoticeByNoticeId(Long noticeId);

    /**
     * 查询公告列表
     * 
     * @param jstNotice 公告
     * @return 公告集合
     */
    public List<JstNotice> selectJstNoticeList(JstNotice jstNotice);

    /**
     * 新增公告
     * 
     * @param jstNotice 公告
     * @return 结果
     */
    public int insertJstNotice(JstNotice jstNotice);

    /**
     * 修改公告
     * 
     * @param jstNotice 公告
     * @return 结果
     */
    public int updateJstNotice(JstNotice jstNotice);

    /**
     * 删除公告
     * 
     * @param noticeId 公告主键
     * @return 结果
     */
    public int deleteJstNoticeByNoticeId(Long noticeId);

    /**
     * 批量删除公告
     * 
     * @param noticeIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstNoticeByNoticeIds(Long[] noticeIds);
}
