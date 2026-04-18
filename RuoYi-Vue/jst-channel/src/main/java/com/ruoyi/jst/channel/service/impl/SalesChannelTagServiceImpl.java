package com.ruoyi.jst.channel.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.channel.domain.JstSalesChannelTag;
import com.ruoyi.jst.channel.mapper.JstSalesChannelTagMapper;
import com.ruoyi.jst.channel.service.SalesChannelTagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

/**
 * 渠道标签服务实现。
 * <p>
 * 采用 add/remove 模型（无 update），UK 冲突时转为友好业务异常。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class SalesChannelTagServiceImpl implements SalesChannelTagService {

    private static final Logger log = LoggerFactory.getLogger(SalesChannelTagServiceImpl.class);

    @Autowired
    private JstSalesChannelTagMapper tagMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JstSalesChannelTag addTag(Long channelId, String tagCode, String tagColor, Long currentUserId) {
        if (channelId == null) throw new ServiceException("渠道 ID 不能为空");
        if (tagCode == null || tagCode.trim().isEmpty()) throw new ServiceException("标签代码不能为空");

        JstSalesChannelTag tag = new JstSalesChannelTag();
        tag.setId(snowId());
        tag.setChannelId(channelId);
        tag.setTagCode(tagCode.trim());
        tag.setTagColor(tagColor);

        try {
            tagMapper.insertJstSalesChannelTag(tag);
        } catch (DuplicateKeyException e) {
            throw new ServiceException("该渠道已存在标签 [" + tagCode + "]，请勿重复添加");
        }
        log.info("[SalesChannelTag] 添加标签 id={} channelId={} tagCode={}", tag.getId(), channelId, tagCode);
        return tag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeTag(Long id, String currentUserName, boolean isAdmin) {
        JstSalesChannelTag tag = tagMapper.selectJstSalesChannelTagById(id);
        if (tag == null) {
            throw new ServiceException("标签不存在");
        }
        if (!isAdmin && !currentUserName.equals(tag.getCreateBy())) {
            throw new ServiceException("仅标签创建者或管理员可删除");
        }
        tagMapper.deleteJstSalesChannelTagById(id);
    }

    @Override
    public List<JstSalesChannelTag> listByChannel(Long channelId) {
        return tagMapper.listByChannel(channelId);
    }

    private Long snowId() {
        return System.currentTimeMillis() * 10_000L + new Random().nextInt(10_000);
    }
}
