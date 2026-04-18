package com.ruoyi.jst.channel.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.channel.domain.JstSales;
import com.ruoyi.jst.channel.domain.JstSalesPreRegister;
import com.ruoyi.jst.channel.mapper.JstSalesPreRegisterMapper;
import com.ruoyi.jst.channel.service.SalesPreRegisterService;
import com.ruoyi.jst.channel.service.SalesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

@Service
public class SalesPreRegisterServiceImpl implements SalesPreRegisterService {

    private static final Logger log = LoggerFactory.getLogger(SalesPreRegisterServiceImpl.class);
    private static final Pattern PHONE_RX = Pattern.compile("^1[3-9]\\d{9}$");

    @Autowired
    private JstSalesPreRegisterMapper preRegMapper;

    @Autowired
    private SalesService salesService;

    @Value("${jst.sales.pre_register_valid_days:90}")
    private int validDays;

    @Value("${jst.sales.pre_register_renew_max:2}")
    private int renewMax;

    public void setValidDays(int v) { this.validDays = v; }
    public void setRenewMax(int v) { this.renewMax = v; }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JstSalesPreRegister create(Long salesId, String phone, String targetName) {
        if (salesId == null) throw new ServiceException("缺少 salesId");
        if (phone == null || !PHONE_RX.matcher(phone).matches()) {
            throw new ServiceException("手机号格式非法");
        }
        // A10: 防自绑（销售本人手机号）
        JstSales me = salesService.getById(salesId);
        if (me != null && phone.equals(me.getPhone())) {
            throw new ServiceException("不能预录入自己的手机号");
        }
        // A2: 同手机号已被某销售预录入（uk_phone_pending 兜底）
        JstSalesPreRegister exists = preRegMapper.selectByPhonePending(phone);
        if (exists != null) {
            throw new ServiceException("该手机号已被销售预录入（pending）");
        }
        JstSalesPreRegister row = new JstSalesPreRegister();
        row.setPreId(snowId());
        row.setSalesId(salesId);
        row.setPhone(phone);
        row.setTargetName(targetName);
        row.setStatus("pending");
        row.setExpireAt(new Date(System.currentTimeMillis() + 86400_000L * validDays));
        row.setRenewCount(0);
        preRegMapper.insertJstSalesPreRegister(row);
        return row;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void renew(Long preId, Long currentSalesId) {
        JstSalesPreRegister r = preRegMapper.selectJstSalesPreRegisterByPreId(preId);
        if (r == null) throw new ServiceException("预录入不存在");
        if (!r.getSalesId().equals(currentSalesId)) {
            throw new ServiceException("不能续期他人的预录入");
        }
        if (!"pending".equals(r.getStatus())) {
            throw new ServiceException("仅 pending 状态可续期");
        }
        if (r.getRenewCount() != null && r.getRenewCount() >= renewMax) {
            throw new ServiceException("已达最多续期次数 " + renewMax);
        }
        r.setExpireAt(new Date(System.currentTimeMillis() + 86400_000L * validDays));
        r.setRenewCount((r.getRenewCount() == null ? 0 : r.getRenewCount()) + 1);
        preRegMapper.updateJstSalesPreRegister(r);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long preId, Long currentSalesId) {
        JstSalesPreRegister r = preRegMapper.selectJstSalesPreRegisterByPreId(preId);
        if (r == null) throw new ServiceException("预录入不存在");
        if (!r.getSalesId().equals(currentSalesId)) {
            throw new ServiceException("不能删除他人的预录入");
        }
        if (!"pending".equals(r.getStatus())) {
            throw new ServiceException("仅 pending 状态可删除");
        }
        preRegMapper.deleteJstSalesPreRegisterByPreId(preId);
    }

    @Override
    public List<JstSalesPreRegister> listMine(Long salesId) {
        return preRegMapper.selectMineByStatus(salesId, null);
    }

    @Override
    public List<JstSalesPreRegister> listAllForAdmin() {
        return preRegMapper.selectJstSalesPreRegisterList(new JstSalesPreRegister());
    }

    @Override
    public JstSalesPreRegister findActiveByPhone(String phone) {
        if (phone == null || phone.isEmpty()) return null;
        return preRegMapper.selectByPhonePending(phone);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markMatched(Long preId, Long channelId) {
        int rows = preRegMapper.markMatched(preId, channelId);
        if (rows == 0) {
            log.warn("[PreRegister] markMatched preId={} channelId={} 未影响行数（已 matched/expired?）", preId, channelId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int expirePendingByCron() {
        List<Long> ids = preRegMapper.selectExpiredPending();
        if (ids == null || ids.isEmpty()) return 0;
        return preRegMapper.markExpiredBatch(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int expireBySales(Long salesId) {
        return preRegMapper.expirePendingBySales(salesId);
    }

    private Long snowId() {
        return System.currentTimeMillis() * 10_000L + new Random().nextInt(10_000);
    }
}
