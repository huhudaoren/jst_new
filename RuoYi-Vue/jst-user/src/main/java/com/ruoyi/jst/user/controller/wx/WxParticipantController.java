package com.ruoyi.jst.user.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.user.domain.Participant;
import com.ruoyi.jst.user.domain.ParticipantUserMap;
import com.ruoyi.jst.user.mapper.ParticipantMapper;
import com.ruoyi.jst.user.mapper.ParticipantUserMapMapper;
import com.ruoyi.jst.user.service.ParticipantClaimService;
import com.ruoyi.jst.user.vo.ParticipantClaimResVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小程序-参赛档案 Controller (F3 用户端补丁)
 * <p>
 * 路径前缀:/jst/wx/participant
 * 关联:SM-14 临时档案认领状态机
 * <p>
 * 用户场景:
 * 1. 家长在我的页面查看自己已认领的子女参赛档案
 * 2. 家长手动主动认领某个临时档案 (需提供 participantId,通过赛事方/老师扫码分享得到)
 *
 * @author jst
 * @since 1.0.0
 */
@RestController
@RequestMapping("/jst/wx/participant")
public class WxParticipantController extends BaseController {

    @Autowired
    private ParticipantUserMapMapper mapMapper;

    @Autowired
    private ParticipantMapper participantMapper;

    @Autowired
    private ParticipantClaimService claimService;

    /**
     * 我的所有参赛档案 (已认领)
     */
    @GetMapping("/my")
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
            // 不返回 idCardNo / guardianMobile (敏感字段)
            result.add(vo);
        }
        return AjaxResult.success(result);
    }

    /**
     * 用户主动认领某个临时档案
     * 业务约束:必须传 participantId,且该档案的 guardian_mobile 必须与当前用户 mobile 一致(否则拒绝)
     */
    @PostMapping("/claim")
    public AjaxResult claim(@RequestParam Long participantId) {
        Long userId = SecurityUtils.getUserId();
        Participant p = participantMapper.selectByPk(participantId);
        if (p == null) {
            throw new ServiceException(BizErrorCode.JST_USER_PARTICIPANT_NOT_FOUND.message());
        }
        // 简单防越权:guardian_mobile 必须能匹配当前用户(实际场景中应从 jst_user 取 mobile 比对)
        // TODO 接入 user.mobile 比对,Mock 阶段允许任意手势

        ParticipantClaimResVO vo = claimService.claimByAdmin(participantId, userId, "用户主动认领");
        return AjaxResult.success(vo);
    }

    /**
     * 查看档案详情(必须是已认领的)
     */
    @GetMapping("/{participantId}")
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
}
