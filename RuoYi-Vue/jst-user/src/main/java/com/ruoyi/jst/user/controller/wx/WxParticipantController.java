package com.ruoyi.jst.user.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.user.domain.Participant;
import com.ruoyi.jst.user.domain.ParticipantUserMap;
import com.ruoyi.jst.user.dto.BatchCreateParticipantReqDTO;
import com.ruoyi.jst.user.dto.BatchEnrollReqDTO;
import com.ruoyi.jst.user.dto.ChannelParticipantQueryReqDTO;
import com.ruoyi.jst.user.dto.ChannelParticipantUpdateReqDTO;
import com.ruoyi.jst.user.mapper.ParticipantMapper;
import com.ruoyi.jst.user.mapper.ParticipantUserMapMapper;
import com.ruoyi.jst.user.service.ChannelParticipantService;
import com.ruoyi.jst.user.service.ParticipantClaimService;
import com.ruoyi.jst.user.vo.ParticipantClaimResVO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 小程序-参赛档案 Controller
 * <p>
 * 路径前缀: /jst/wx/participant
 * 关联: SM-14 临时档案认领状态机
 * <p>
 * E0-1 新增:
 * - /jst/wx/channel/participant/batch-create (渠道方批量创建临时档案)
 * - /jst/wx/channel/participant/batch-enroll (渠道方批量报名)
 * - /jst/wx/participant/auto-claim (学生登录时自动认领)
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
public class WxParticipantController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(WxParticipantController.class);

    @Autowired
    private ParticipantUserMapMapper mapMapper;

    @Autowired
    private ParticipantMapper participantMapper;

    @Autowired
    private ParticipantClaimService claimService;

    @Autowired
    private ChannelParticipantService channelParticipantService;

    @Autowired
    private com.ruoyi.jst.user.mapper.JstUserMapper jstUserMapper;

    // ========== 学生端原有接口 ==========

    /**
     * 我的所有参赛档案 (已认领)
     */
    @GetMapping("/jst/wx/participant/my")
    public AjaxResult myParticipants() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message());
        }
        List<ParticipantUserMap> maps = mapMapper.selectActiveByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (ParticipantUserMap m : maps) {
            Participant p = participantMapper.selectByPk(m.getParticipantId());
            if (p == null) continue;
            Map<String, Object> vo = new HashMap<>();
            vo.put("participantId", p.getParticipantId());
            vo.put("name", p.getName());
            vo.put("gender", p.getGender());
            vo.put("age", p.getAge());
            vo.put("school", p.getSchool());
            vo.put("organization", p.getOrganization());
            vo.put("className", p.getClassName());
            vo.put("guardianName", p.getGuardianName());
            vo.put("claimMethod", m.getClaimMethod());
            vo.put("claimTime", m.getClaimTime());
            result.add(vo);
        }
        return AjaxResult.success(result);
    }

    @PreAuthorize("@ss.hasRole('jst_channel')")
    @GetMapping("/jst/wx/channel/participant/my")
    public TableDataInfo myCreatedParticipants(@Valid ChannelParticipantQueryReqDTO query) {
        Long userId = SecurityUtils.getUserId();
        startPage();
        return getDataTable(channelParticipantService.selectMyParticipants(userId, query.getStatus()));
    }

    @PreAuthorize("@ss.hasRole('jst_channel')")
    @PutMapping("/jst/wx/channel/participant/{participantId}")
    public AjaxResult updateChannelParticipant(@PathVariable Long participantId,
                                               @Valid @RequestBody ChannelParticipantUpdateReqDTO req) {
        Long userId = SecurityUtils.getUserId();
        return AjaxResult.success(channelParticipantService.updateParticipant(userId, participantId, req));
    }

    @PreAuthorize("@ss.hasRole('jst_channel')")
    @DeleteMapping("/jst/wx/channel/participant/{participantId}")
    public AjaxResult deleteChannelParticipant(@PathVariable Long participantId) {
        Long userId = SecurityUtils.getUserId();
        channelParticipantService.deleteParticipant(userId, participantId);
        return AjaxResult.success();
    }

    /**
     * 用户主动认领某个临时档案
     */
    @PostMapping("/jst/wx/participant/claim")
    public AjaxResult claim(@RequestParam Long participantId) {
        Long userId = SecurityUtils.getUserId();
        Participant p = participantMapper.selectByPk(participantId);
        if (p == null) {
            throw new ServiceException(BizErrorCode.JST_USER_PARTICIPANT_NOT_FOUND.message());
        }
        ParticipantClaimResVO vo = claimService.claimByAdmin(participantId, userId, "用户主动认领");
        return AjaxResult.success(vo);
    }

    /**
     * 查看档案详情(必须是已认领的)
     */
    @GetMapping("/jst/wx/participant/{participantId}")
    public AjaxResult detail(@PathVariable Long participantId) {
        Long userId = SecurityUtils.getUserId();
        ParticipantUserMap m = mapMapper.selectActiveByParticipantId(participantId);
        if (m == null || !m.getUserId().equals(userId)) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        Participant p = participantMapper.selectByPk(participantId);
        Map<String, Object> vo = new HashMap<>();
        vo.put("participantId", p.getParticipantId());
        vo.put("name", p.getName());
        vo.put("gender", p.getGender());
        vo.put("birthday", p.getBirthday());
        vo.put("age", p.getAge());
        vo.put("school", p.getSchool());
        vo.put("organization", p.getOrganization());
        vo.put("className", p.getClassName());
        vo.put("claimStatus", p.getClaimStatus());
        return AjaxResult.success(vo);
    }

    // ========== E0-1 新增: 渠道方批量操作 ==========

    /**
     * 渠道方批量创建临时参赛档案
     * <p>
     * 接收 JSON 数组，每条含 name/gender/age/school/className/guardianMobile/guardianName。
     * 创建后状态为 unclaimed，等待学生登录时自动认领。
     *
     * @param list 批量创建入参列表
     * @return 创建成功的 participantId 列表
     * @关联表 jst_participant
     * @关联权限 hasRole('jst_channel')
     */
    @PreAuthorize("@ss.hasRole('jst_channel')")
    @PostMapping("/jst/wx/channel/participant/batch-create")
    public AjaxResult batchCreate(@Valid @RequestBody List<BatchCreateParticipantReqDTO> list) {
        Long userId = SecurityUtils.getUserId();
        if (list == null || list.isEmpty()) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        if (list.size() > 200) {
            throw new ServiceException("单次批量创建不得超过200条",
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }

        Date now = DateUtils.getNowDate();
        String operator = String.valueOf(userId);
        List<Long> createdIds = new ArrayList<>();

        for (BatchCreateParticipantReqDTO item : list) {
            Participant p = new Participant();
            p.setParticipantType("temporary_participant");
            p.setName(item.getName());
            p.setGender(item.getGender());
            p.setAge(item.getAge());
            p.setSchool(item.getSchool());
            p.setClassName(item.getClassName());
            p.setGuardianMobile(item.getGuardianMobile());
            p.setGuardianName(item.getGuardianName());
            p.setClaimStatus("unclaimed");
            p.setVisibleScope("channel_only");
            p.setCreateBy(operator);
            p.setCreateTime(now);
            p.setUpdateBy(operator);
            p.setUpdateTime(now);
            participantMapper.insertParticipant(p);
            createdIds.add(p.getParticipantId());
        }

        log.info("[BatchCreate] 渠道方批量创建临时档案 userId={} count={}", userId, createdIds.size());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("createdCount", createdIds.size());
        result.put("participantIds", createdIds);
        return AjaxResult.success(result);
    }

    /**
     * 渠道方批量报名
     * <p>
     * 接收 contestId + participantIds，内部循环走 F9 报名创建逻辑。
     * 本期仅做参赛人绑定到赛事的记录创建，实际报名流程（表单/审核）由学生认领后完成。
     *
     * @param req 批量报名入参
     * @return 报名结果
     * @关联表 jst_participant / jst_enroll_record
     * @关联权限 hasRole('jst_channel')
     */
    @PreAuthorize("@ss.hasRole('jst_channel')")
    @PostMapping("/jst/wx/channel/participant/batch-enroll")
    public AjaxResult batchEnroll(@Valid @RequestBody BatchEnrollReqDTO req) {
        Long userId = SecurityUtils.getUserId();

        // 本期 MVP: 仅验证参赛人存在性，实际报名（表单提交+审核）由学生端完成
        // 此处预留批量报名框架，返回可报名的参赛人数
        List<Long> validIds = new ArrayList<>();
        List<Long> invalidIds = new ArrayList<>();
        for (Long participantId : req.getParticipantIds()) {
            Participant p = participantMapper.selectByPk(participantId);
            if (p == null) {
                invalidIds.add(participantId);
            } else {
                validIds.add(participantId);
            }
        }

        log.info("[BatchEnroll] 渠道方批量报名 userId={} contestId={} valid={} invalid={}",
                userId, req.getContestId(), validIds.size(), invalidIds.size());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("contestId", req.getContestId());
        result.put("validCount", validIds.size());
        result.put("invalidCount", invalidIds.size());
        result.put("invalidParticipantIds", invalidIds);
        result.put("message", "批量报名预校验完成，实际报名由学生认领后在赛事详情页完成");
        return AjaxResult.success(result);
    }

    // ========== E0-1 新增: 自动认领 ==========

    /**
     * 学生登录时自动触发临时档案认领
     * <p>
     * 按"监护人手机号 + 参赛人姓名"精确匹配临时档案。
     * 匹配到唯一候选则自动认领，多候选返回 JST_PARTICIPANT_AUTO_CLAIM_CONFLICT。
     *
     * @return 认领结果
     * @关联表 jst_participant / jst_participant_user_map
     * @关联状态机 SM-14
     */
    @PostMapping("/jst/wx/participant/auto-claim")
    public AjaxResult autoClaim() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }

        // 从 jst_user 查询当前用户手机号和姓名，用于临时档案自动认领匹配
        com.ruoyi.jst.user.domain.JstUser jstUser = jstUserMapper.selectJstUserByUserId(userId);
        String mobile = jstUser != null ? jstUser.getMobile() : null;
        String name = jstUser != null ? jstUser.getRealName() : null;
        if (mobile == null || mobile.isEmpty()) {
            return AjaxResult.success("用户未绑定手机号，无法自动认领");
        }
        ParticipantClaimResVO vo = claimService.claimByAuto(userId, mobile, name);
        if (vo == null) {
            return AjaxResult.success("无匹配的临时档案，或已转人工处理");
        }
        return AjaxResult.success(vo);
    }
}
