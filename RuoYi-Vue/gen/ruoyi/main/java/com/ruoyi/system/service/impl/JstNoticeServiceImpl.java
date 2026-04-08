package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.JstNoticeMapper;
import com.ruoyi.system.domain.JstNotice;
import com.ruoyi.system.service.IJstNoticeService;

/**
 * 公告Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstNoticeServiceImpl implements IJstNoticeService 
{
    @Autowired
    private JstNoticeMapper jstNoticeMapper;

    /**
     * 查询公告
     * 
     * @param noticeId 公告主键
     * @return 公告
     */
    @Override
    public JstNotice selectJstNoticeByNoticeId(Long noticeId)
    {
        return jstNoticeMapper.selectJstNoticeByNoticeId(noticeId);
    }

    /**
     * 查询公告列表
     * 
     * @param jstNotice 公告
     * @return 公告
     */
    @Override
    public List<JstNotice> selectJstNoticeList(JstNotice jstNotice)
    {
        return jstNoticeMapper.selectJstNoticeList(jstNotice);
    }

    /**
     * 新增公告
     * 
     * @param jstNotice 公告
     * @return 结果
     */
    @Override
    public int insertJstNotice(JstNotice jstNotice)
    {
        jstNotice.setCreateTime(DateUtils.getNowDate());
        return jstNoticeMapper.insertJstNotice(jstNotice);
    }

    /**
     * 修改公告
     * 
     * @param jstNotice 公告
     * @return 结果
     */
    @Override
    public int updateJstNotice(JstNotice jstNotice)
    {
        jstNotice.setUpdateTime(DateUtils.getNowDate());
        return jstNoticeMapper.updateJstNotice(jstNotice);
    }

    /**
     * 批量删除公告
     * 
     * @param noticeIds 需要删除的公告主键
     * @return 结果
     */
    @Override
    public int deleteJstNoticeByNoticeIds(Long[] noticeIds)
    {
        return jstNoticeMapper.deleteJstNoticeByNoticeIds(noticeIds);
    }

    /**
     * 删除公告信息
     * 
     * @param noticeId 公告主键
     * @return 结果
     */
    @Override
    public int deleteJstNoticeByNoticeId(Long noticeId)
    {
        return jstNoticeMapper.deleteJstNoticeByNoticeId(noticeId);
    }
}
