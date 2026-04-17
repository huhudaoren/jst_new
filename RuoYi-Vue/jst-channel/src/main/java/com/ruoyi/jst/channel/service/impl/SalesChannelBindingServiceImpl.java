package com.ruoyi.jst.channel.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.channel.domain.JstSalesChannelBinding;
import com.ruoyi.jst.channel.mapper.JstSalesChannelBindingMapper;
import com.ruoyi.jst.channel.service.SalesChannelBindingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class SalesChannelBindingServiceImpl implements SalesChannelBindingService {

    private static final Logger log = LoggerFactory.getLogger(SalesChannelBindingServiceImpl.class);

    @Autowired
    private JstSalesChannelBindingMapper bindingMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JstSalesChannelBinding bindOrTransfer(Long channelId, Long newSalesId, String bindSource, String remark, Long operatorId) {
        if (channelId == null || newSalesId == null || bindSource == null) {
            throw new ServiceException("bindOrTransfer 参数缺失");
        }
        Date now = new Date();
        JstSalesChannelBinding current = bindingMapper.selectCurrentByChannelId(channelId);
        if (current != null) {
            bindingMapper.closeBinding(current.getBindingId(), now);
        }
        JstSalesChannelBinding row = new JstSalesChannelBinding();
        row.setBindingId(snowId());
        row.setChannelId(channelId);
        row.setSalesId(newSalesId);
        row.setEffectiveFrom(now);
        row.setBindSource(bindSource);
        row.setBindRemark(remark);
        row.setOperatorId(operatorId);
        bindingMapper.insertJstSalesChannelBinding(row);
        return row;
    }

    @Override
    public JstSalesChannelBinding getCurrentByChannelId(Long channelId) {
        return bindingMapper.selectCurrentByChannelId(channelId);
    }

    @Override
    public JstSalesChannelBinding getBindingAtTime(Long channelId, Date atTime) {
        return bindingMapper.selectAtTime(channelId, atTime);
    }

    @Override
    public List<JstSalesChannelBinding> listCurrentByOwnerSales(Long salesId) {
        return bindingMapper.selectCurrentByOwnerSales(salesId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int transferAllToManager(Long fromSalesId, Long managerId, Long operatorId) {
        if (fromSalesId == null) throw new ServiceException("fromSalesId 缺失");
        List<JstSalesChannelBinding> mine = bindingMapper.selectCurrentByOwnerSales(fromSalesId);
        if (mine == null || mine.isEmpty()) return 0;
        if (managerId == null) {
            log.warn("[Binding] 销售 {} 无主管 (顶级)，{} 个渠道留在原绑定，admin 需手动分配", fromSalesId, mine.size());
            return 0;
        }
        int n = 0;
        for (JstSalesChannelBinding b : mine) {
            bindOrTransfer(b.getChannelId(), managerId, "transfer_resign",
                    "离职自动转移 from sales " + fromSalesId, operatorId);
            n++;
        }
        return n;
    }

    private Long snowId() {
        return System.currentTimeMillis() * 10_000L + new Random().nextInt(10_000);
    }
}
