package com.ruoyi.jst.user.controller.admin;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.user.dto.ParticipantAdminQueryReqDTO;
import com.ruoyi.jst.user.service.ParticipantAdminService;
import com.ruoyi.jst.user.vo.ParticipantAdminDetailResVO;
import com.ruoyi.jst.user.vo.ParticipantAdminListResVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端参赛档案查询 Controller。
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/admin/participant")
public class ParticipantAdminController extends BaseController {

    @Autowired
    private ParticipantAdminService participantAdminService;

    /**
     * 参赛档案分页列表（姓名/手机号模糊）。
     *
     * @param query 查询条件
     * @return 分页数据
     * @关联表 jst_participant / jst_user / jst_channel
     * @关联状态机 SM-14
     * @关联权限 jst:participant:list
     */
    @PreAuthorize("@ss.hasPermi('jst:participant:list')")
    @GetMapping("/list")
    public TableDataInfo list(@Validated ParticipantAdminQueryReqDTO query) {
        startPage();
        List<ParticipantAdminListResVO> rows = participantAdminService.selectParticipantList(query);
        return getDataTable(rows);
    }

    /**
     * 参赛档案详情。
     *
     * @param participantId 参赛档案ID
     * @return 详情数据
     * @关联表 jst_participant / jst_user / jst_channel
     * @关联状态机 SM-14
     * @关联权限 jst:participant:list
     */
    @PreAuthorize("@ss.hasPermi('jst:participant:list')")
    @GetMapping("/{participantId}")
    public AjaxResult getInfo(@PathVariable("participantId") Long participantId) {
        ParticipantAdminDetailResVO detail = participantAdminService.selectParticipantDetail(participantId);
        return AjaxResult.success(detail);
    }
}
