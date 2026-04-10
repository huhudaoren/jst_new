package com.ruoyi.jst.channel.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.channel.dto.ChannelDashboardQueryDTO;
import com.ruoyi.jst.channel.dto.ChannelTopRankingReqDTO;
import com.ruoyi.jst.channel.mapper.lookup.ChannelLookupMapper;
import com.ruoyi.jst.channel.service.ChannelDashboardService;
import com.ruoyi.jst.channel.service.ChannelSupplementService;
import com.ruoyi.jst.common.exception.BizErrorCode;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 小程序-渠道方工作台 Controller
 * <p>
 * 路径前缀：/jst/wx/channel
 * 关联文档：架构设计/27-用户端API契约.md
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/wx/channel")
public class WxChannelDashboardController extends BaseController {

    @Autowired
    private ChannelDashboardService channelDashboardService;

    @Autowired
    private ChannelSupplementService channelSupplementService;

    @Autowired
    private ChannelLookupMapper channelLookupMapper;

    // ========== 原有工作台接口 ==========

    /**
     * 月度统计
     */
    @PreAuthorize("@ss.hasRole('jst_channel') or @ss.hasPermi('jst:channel:dashboard:monthly')")
    @GetMapping("/dashboard/monthly")
    public AjaxResult monthly(@Valid ChannelDashboardQueryDTO query) {
        return AjaxResult.success(channelDashboardService.getMonthly(query));
    }

    /**
     * 学生列表
     */
    @PreAuthorize("@ss.hasRole('jst_channel') or @ss.hasPermi('jst:channel:dashboard:students')")
    @GetMapping("/students")
    public TableDataInfo students(@Valid ChannelDashboardQueryDTO query) {
        return getDataTable(channelDashboardService.selectStudentList(query));
    }

    /**
     * 订单列表
     */
    @PreAuthorize("@ss.hasRole('jst_channel') or @ss.hasPermi('jst:channel:dashboard:orders')")
    @GetMapping("/orders")
    public TableDataInfo orders(@Valid ChannelDashboardQueryDTO query) {
        return getDataTable(channelDashboardService.selectOrderList(query));
    }

    /**
     * 综合统计
     */
    @PreAuthorize("@ss.hasRole('jst_channel') or @ss.hasPermi('jst:channel:dashboard:stats')")
    @GetMapping("/dashboard/stats")
    public AjaxResult stats(@Valid ChannelDashboardQueryDTO query) {
        return AjaxResult.success(channelDashboardService.getStats(query));
    }

    // ========== E0-1 新增接口 ==========

    /**
     * 渠道方权益列表
     * <p>
     * 从 jst_user_rights 按当前登录用户ID查询，返回 rightsName/remainQuota/expireTime
     *
     * @return 权益列表
     * @关联表 jst_user_rights / jst_rights_template
     * @关联权限 hasRole('jst_channel')
     */
    @PreAuthorize("@ss.hasRole('jst_channel')")
    @GetMapping("/rights/my")
    public AjaxResult myRights() {
        Long userId = SecurityUtils.getUserId();
        return AjaxResult.success(channelSupplementService.selectMyRights(userId));
    }

    /**
     * 查指定绑定学生的成绩列表
     * <p>
     * 校验绑定关系后返回已发布成绩
     *
     * @param studentId 学生用户ID
     * @return 成绩列表
     * @关联表 jst_student_channel_binding / jst_enroll_record / jst_contest
     * @关联权限 hasRole('jst_channel')
     */
    @PreAuthorize("@ss.hasRole('jst_channel')")
    @GetMapping("/students/{studentId}/score")
    public AjaxResult studentScore(@PathVariable("studentId") Long studentId) {
        Long channelId = requireCurrentChannelId();
        return AjaxResult.success(channelSupplementService.selectStudentScores(channelId, studentId));
    }

    /**
     * 查指定绑定学生的证书列表
     *
     * @param studentId 学生用户ID
     * @return 证书列表
     * @关联表 jst_student_channel_binding / jst_enroll_record / jst_contest
     * @关联权限 hasRole('jst_channel')
     */
    @PreAuthorize("@ss.hasRole('jst_channel')")
    @GetMapping("/students/{studentId}/cert")
    public AjaxResult studentCert(@PathVariable("studentId") Long studentId) {
        Long channelId = requireCurrentChannelId();
        return AjaxResult.success(channelSupplementService.selectStudentCerts(channelId, studentId));
    }

    @PreAuthorize("@ss.hasRole('jst_channel')")
    @GetMapping("/dashboard/top-contests")
    public AjaxResult topContests(@Valid ChannelTopRankingReqDTO query) {
        Long channelId = requireCurrentChannelId();
        return AjaxResult.success(channelSupplementService.selectTopContests(channelId, query.getPeriod(), query.getLimit()));
    }

    @PreAuthorize("@ss.hasRole('jst_channel')")
    @GetMapping("/dashboard/top-students")
    public AjaxResult topStudents(@Valid ChannelTopRankingReqDTO query) {
        Long channelId = requireCurrentChannelId();
        return AjaxResult.success(channelSupplementService.selectTopStudents(channelId, query.getLimit()));
    }

    @PreAuthorize("@ss.hasRole('jst_channel')")
    @GetMapping("/orders/{orderId}")
    public AjaxResult orderDetail(@PathVariable("orderId") Long orderId) {
        Long channelId = requireCurrentChannelId();
        return AjaxResult.success(channelSupplementService.selectOrderDetail(channelId, orderId));
    }

    /**
     * 渠道方自助解绑学生（Q-01）
     * <p>
     * 解绑后学生可被其他渠道方绑定，不回溯历史返点。
     *
     * @param bindingId 绑定记录ID
     * @return 操作结果
     * @关联表 jst_student_channel_binding
     * @关联决策 Q-01
     * @关联权限 hasRole('jst_channel')
     */
    @PreAuthorize("@ss.hasRole('jst_channel')")
    @PostMapping("/binding/{bindingId}/unbind")
    public AjaxResult unbindStudent(@PathVariable("bindingId") Long bindingId) {
        Long channelId = requireCurrentChannelId();
        channelSupplementService.unbindStudent(channelId, bindingId);
        return AjaxResult.success("解绑成功");
    }

    // ========== private helpers ==========

    /**
     * 获取当前登录渠道方的 channelId
     */
    private Long requireCurrentChannelId() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        Map<String, Object> channel = channelLookupMapper.selectCurrentByUserId(userId);
        if (channel == null || channel.isEmpty()) {
            throw new ServiceException(BizErrorCode.JST_CHANNEL_NOT_AUTHED.message(),
                    BizErrorCode.JST_CHANNEL_NOT_AUTHED.code());
        }
        Object channelIdObj = channel.get("channelId");
        if (channelIdObj == null) {
            throw new ServiceException(BizErrorCode.JST_CHANNEL_NOT_AUTHED.message(),
                    BizErrorCode.JST_CHANNEL_NOT_AUTHED.code());
        }
        return channelIdObj instanceof Number ? ((Number) channelIdObj).longValue() : Long.valueOf(String.valueOf(channelIdObj));
    }
}
