package com.ruoyi.jst.channel.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.channel.domain.JstSales;
import com.ruoyi.jst.channel.dto.SalesCreateOnestopReqDTO;
import com.ruoyi.jst.channel.mapper.JstSalesMapper;
import com.ruoyi.jst.channel.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class SalesServiceImpl implements SalesService {

    private static final String ROLE_KEY_SALES = "jst_sales";
    private static final String ROLE_KEY_SALES_MANAGER = "jst_sales_manager";

    @Autowired
    private JstSalesMapper salesMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Lazy 注入避开循环依赖：
     * SalesServiceImpl -> PasswordEncoder（由 SecurityConfig 定义）
     * -> SecurityConfig -> permitAllUrl -> WebMvcAutoConfiguration -> ChannelWebConfig
     * -> RestrictedSalesActionInterceptor -> SalesService
     */
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Value("${jst.sales.resign_apply_max_days:7}")
    private int resignMaxDays;

    @Value("${jst.sales.transition_months:3}")
    private int transitionMonths;

    /** 测试钩子 */
    public void setResignMaxDays(int v) { this.resignMaxDays = v; }
    public void setTransitionMonths(int v) { this.transitionMonths = v; }

    @Override
    public JstSales getById(Long salesId) {
        return salesMapper.selectJstSalesBySalesId(salesId);
    }

    @Override
    public JstSales getBySysUserId(Long sysUserId) {
        return salesMapper.selectBySysUserId(sysUserId);
    }

    @Override
    public JstSales getBySalesNo(String salesNo) {
        return salesMapper.selectBySalesNo(salesNo);
    }

    @Override
    public List<JstSales> listSubordinates(Long managerId) {
        return salesMapper.selectByManagerId(managerId);
    }

    @Override
    public List<Long> resolveScopeSalesIds(Long currentSalesId) {
        if (currentSalesId == null) return null;
        JstSales me = salesMapper.selectJstSalesBySalesId(currentSalesId);
        if (me == null) return Collections.singletonList(currentSalesId);
        if (me.getIsManager() != null && me.getIsManager() == 1) {
            List<Long> ids = salesMapper.selectActiveTeamIds(currentSalesId);
            if (ids == null || ids.isEmpty()) return Collections.singletonList(currentSalesId);
            return ids;
        }
        return Collections.singletonList(currentSalesId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JstSales create(JstSales row) {
        if (row.getSysUserId() == null) {
            throw new ServiceException("缺少 sys_user_id");
        }
        if (salesMapper.selectBySysUserId(row.getSysUserId()) != null) {
            throw new ServiceException("该 sys_user 已是销售");
        }
        if (row.getSalesId() == null) row.setSalesId(snowId());
        if (row.getSalesNo() == null) row.setSalesNo(generateSalesNo());
        if (row.getStatus() == null) row.setStatus("active");
        if (row.getIsManager() == null) row.setIsManager(0);
        if (row.getCommissionRateDefault() == null) row.setCommissionRateDefault(new BigDecimal("0.0500"));
        if (row.getManagerCommissionRate() == null) row.setManagerCommissionRate(BigDecimal.ZERO);
        salesMapper.insertJstSales(row);
        return row;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOnestop(SalesCreateOnestopReqDTO req) {
        // 1) user_name 唯一性
        Integer existCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sys_user WHERE user_name = ? AND del_flag = '0'",
                Integer.class, req.getUserName());
        if (existCount != null && existCount > 0) {
            throw new ServiceException("用户名 " + req.getUserName() + " 已存在");
        }
        // 2) 手机号在 jst_sales 唯一
        Integer phoneCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM jst_sales WHERE phone = ?",
                Integer.class, req.getPhone());
        if (phoneCount != null && phoneCount > 0) {
            throw new ServiceException("手机号 " + req.getPhone() + " 已被其他销售占用");
        }

        // 3) 查 role_id
        String roleKey = Boolean.TRUE.equals(req.getAsManager()) ? ROLE_KEY_SALES_MANAGER : ROLE_KEY_SALES;
        Long roleId;
        try {
            roleId = jdbcTemplate.queryForObject(
                    "SELECT role_id FROM sys_role WHERE role_key = ? AND status = '0' AND del_flag = '0' LIMIT 1",
                    Long.class, roleKey);
        } catch (EmptyResultDataAccessException e) {
            throw new ServiceException("销售角色 " + roleKey + " 不存在，请先初始化角色");
        }
        if (roleId == null) {
            throw new ServiceException("销售角色 " + roleKey + " 不存在，请先初始化角色");
        }

        // 4) INSERT sys_user
        String hashedPwd = passwordEncoder.encode(req.getInitPassword());
        String currentUserName;
        try {
            currentUserName = SecurityUtils.getUsername();
        } catch (Exception e) {
            currentUserName = "system";
        }
        jdbcTemplate.update(
                "INSERT INTO sys_user (user_name, nick_name, phonenumber, password, user_type, status, del_flag, create_by, create_time) " +
                        "VALUES (?, ?, ?, ?, '00', '0', '0', ?, NOW())",
                req.getUserName(), req.getSalesName(), req.getPhone(), hashedPwd, currentUserName);

        Long newUserId = jdbcTemplate.queryForObject(
                "SELECT user_id FROM sys_user WHERE user_name = ? AND del_flag = '0'",
                Long.class, req.getUserName());
        if (newUserId == null) {
            throw new ServiceException("创建系统用户失败");
        }

        // 5) INSERT sys_user_role
        jdbcTemplate.update(
                "INSERT INTO sys_user_role (user_id, role_id) VALUES (?, ?)",
                newUserId, roleId);

        // 6) INSERT jst_sales（复用 create() 的默认值/校验逻辑）
        JstSales row = new JstSales();
        row.setSysUserId(newUserId);
        row.setSalesName(req.getSalesName());
        row.setPhone(req.getPhone());
        row.setManagerId(req.getManagerId());
        row.setCommissionRateDefault(req.getCommissionRateDefault());
        row.setIsManager(Boolean.TRUE.equals(req.getAsManager()) ? 1 : 0);
        JstSales saved = create(row);
        return saved.getSalesId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDefaultRate(Long salesId, BigDecimal rate) {
        JstSales s = requireSales(salesId);
        s.setCommissionRateDefault(rate);
        salesMapper.updateJstSales(s);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateManager(Long salesId, Long managerId) {
        JstSales s = requireSales(salesId);
        if (managerId != null) {
            JstSales mgr = requireSales(managerId);
            if (mgr.getIsManager() == null || mgr.getIsManager() != 1) {
                throw new ServiceException("目标销售不是主管");
            }
        }
        s.setManagerId(managerId);
        salesMapper.updateJstSales(s);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyResign(Long salesId, Date expectedResignDate) {
        JstSales s = requireSales(salesId);
        if (!"active".equals(s.getStatus())) {
            throw new ServiceException("仅在职销售可申请离职");
        }
        if (expectedResignDate == null) throw new ServiceException("缺少预期离职日");
        Date now = new Date();
        long diffDays = (expectedResignDate.getTime() - now.getTime()) / 86400_000L;
        if (diffDays < 0) throw new ServiceException("预期离职日不能早于今天");
        if (diffDays > resignMaxDays) {
            throw new ServiceException("预期离职日不能超过 " + resignMaxDays + " 天后");
        }
        s.setStatus("resign_apply");
        s.setResignApplyDate(now);
        s.setExpectedResignDate(expectedResignDate);
        salesMapper.updateJstSales(s);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeResign(Long salesId) {
        JstSales s = requireSales(salesId);
        if (!"active".equals(s.getStatus()) && !"resign_apply".equals(s.getStatus())) {
            throw new ServiceException("当前状态不能执行离职");
        }
        Date now = new Date();
        s.setStatus("resigned_pending_settle");
        s.setActualResignDate(now);
        LocalDateTime endTime = now.toInstant().atZone(ZoneId.systemDefault())
                .toLocalDateTime().plusMonths(transitionMonths);
        s.setTransitionEndDate(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()));
        salesMapper.updateJstSales(s);
        // 后续：触发 SalesResignedEvent 让 Listener 处理 binding 转移、pre_register 失效、sys_user 禁用
        // (那部分由 SalesResignationService 编排，本 Service 只推状态机)
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void endTransition(Long salesId) {
        JstSales s = requireSales(salesId);
        if (!"resigned_pending_settle".equals(s.getStatus())) {
            throw new ServiceException("仅过渡期内销售可结束过渡");
        }
        s.setStatus("resigned_final");
        salesMapper.updateJstSales(s);
    }

    @Override
    public List<JstSales> listForAdmin(JstSales query) {
        return salesMapper.selectJstSalesList(query);
    }

    private JstSales requireSales(Long salesId) {
        JstSales s = salesMapper.selectJstSalesBySalesId(salesId);
        if (s == null) throw new ServiceException("销售不存在: " + salesId);
        return s;
    }

    private Long snowId() {
        // 简易雪花替代品：当前时间戳 + 4 位随机
        return System.currentTimeMillis() * 10_000L + new Random().nextInt(10_000);
    }

    private String generateSalesNo() {
        return "S" + new java.text.SimpleDateFormat("yyMM").format(new Date())
                + String.format("%04d", new Random().nextInt(10000));
    }
}
