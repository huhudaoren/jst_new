package com.ruoyi.jst.channel.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.channel.domain.JstSalesFollowupRecord;
import com.ruoyi.jst.channel.dto.FollowupCreateReqDTO;
import com.ruoyi.jst.channel.dto.FollowupQueryReqDTO;
import com.ruoyi.jst.channel.dto.FollowupUpdateReqDTO;
import com.ruoyi.jst.channel.mapper.JstSalesFollowupRecordMapper;
import com.ruoyi.jst.channel.service.SalesFollowupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 跟进记录服务实现。
 * <p>
 * 编辑窗口为 24h（can_edit_until），仅记录所有者可修改/删除。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class SalesFollowupServiceImpl implements SalesFollowupService {

    private static final Logger log = LoggerFactory.getLogger(SalesFollowupServiceImpl.class);

    /** 24 小时（毫秒） */
    private static final long EDIT_WINDOW_MS = 24L * 3600 * 1000;

    @Autowired
    private JstSalesFollowupRecordMapper followupMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JstSalesFollowupRecord create(Long salesId, FollowupCreateReqDTO req) {
        if (salesId == null) {
            throw new ServiceException("salesId 不能为空");
        }
        if (req.getChannelId() == null) {
            throw new ServiceException("渠道 ID 不能为空");
        }
        JstSalesFollowupRecord record = new JstSalesFollowupRecord();
        record.setRecordId(snowId());
        record.setSalesId(salesId);
        record.setChannelId(req.getChannelId());
        record.setFollowupType(req.getFollowupType());
        record.setFollowupAt(req.getFollowupAt() != null ? req.getFollowupAt() : new Date());
        record.setContent(req.getContent());
        record.setMood(req.getMood());
        record.setNextFollowupAt(req.getNextFollowupAt());
        record.setAttachmentUrls(req.getAttachmentUrls());
        // 24h 编辑窗口
        record.setCanEditUntil(new Date(System.currentTimeMillis() + EDIT_WINDOW_MS));
        followupMapper.insertJstSalesFollowupRecord(record);
        log.info("[SalesFollowup] 新建跟进记录 recordId={} salesId={} channelId={}", record.getRecordId(), salesId, req.getChannelId());
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long recordId, Long currentSalesId, FollowupUpdateReqDTO req) {
        JstSalesFollowupRecord existing = requireRecord(recordId);
        checkOwner(existing, currentSalesId);
        checkEditWindow(existing);

        if (req.getFollowupType() != null) existing.setFollowupType(req.getFollowupType());
        if (req.getFollowupAt() != null) existing.setFollowupAt(req.getFollowupAt());
        if (req.getContent() != null) existing.setContent(req.getContent());
        if (req.getMood() != null) existing.setMood(req.getMood());
        if (req.getNextFollowupAt() != null) existing.setNextFollowupAt(req.getNextFollowupAt());
        if (req.getAttachmentUrls() != null) existing.setAttachmentUrls(req.getAttachmentUrls());
        followupMapper.updateJstSalesFollowupRecord(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long recordId, Long currentSalesId) {
        JstSalesFollowupRecord existing = requireRecord(recordId);
        checkOwner(existing, currentSalesId);
        checkEditWindow(existing);
        followupMapper.deleteJstSalesFollowupRecordByRecordId(recordId);
    }

    @Override
    public List<JstSalesFollowupRecord> listByChannel(Long channelId) {
        return followupMapper.listByChannel(channelId);
    }

    @Override
    public List<JstSalesFollowupRecord> listMine(Long salesId, FollowupQueryReqDTO query) {
        Long channelId = null;
        String followupType = null;
        Date dateFrom = null;
        Date dateTo = null;
        if (query != null) {
            channelId = query.getChannelId();
            followupType = query.getFollowupType();
            dateFrom = query.getDateFrom();
            dateTo = query.getDateTo();
        }
        // salesId==null 表示 admin 视角，listMineWithFilter 在 salesId 为 null 时不加 sales_id 过滤
        return followupMapper.listMineWithFilter(salesId, channelId, followupType, dateFrom, dateTo);
    }

    @Override
    public List<JstSalesFollowupRecord> selectDueRemindersForJob(Date startOfDay, Date endOfDay) {
        return followupMapper.selectDueReminders(startOfDay, endOfDay);
    }

    // ---- helpers ----

    private JstSalesFollowupRecord requireRecord(Long recordId) {
        JstSalesFollowupRecord record = followupMapper.selectJstSalesFollowupRecordByRecordId(recordId);
        if (record == null) {
            throw new ServiceException("跟进记录不存在");
        }
        return record;
    }

    private void checkOwner(JstSalesFollowupRecord record, Long currentSalesId) {
        if (!record.getSalesId().equals(currentSalesId)) {
            throw new ServiceException("无权操作他人的跟进记录");
        }
    }

    private void checkEditWindow(JstSalesFollowupRecord record) {
        if (record.getCanEditUntil() != null && record.getCanEditUntil().before(new Date())) {
            throw new ServiceException("跟进记录超过 24h 编辑窗口，无法修改");
        }
    }

    private Long snowId() {
        return System.currentTimeMillis() * 10_000L + new Random().nextInt(10_000);
    }
}
