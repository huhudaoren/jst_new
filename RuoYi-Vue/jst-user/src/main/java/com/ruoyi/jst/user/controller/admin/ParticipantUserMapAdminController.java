package com.ruoyi.jst.user.controller.admin;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.user.dto.ParticipantUserMapQueryReqDTO;
import com.ruoyi.jst.user.service.ParticipantAdminService;
import com.ruoyi.jst.user.vo.ParticipantUserMapAdminResVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端参赛档案认领映射查询 Controller。
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/admin/participant-user-map")
public class ParticipantUserMapAdminController extends BaseController {

    @Autowired
    private ParticipantAdminService participantAdminService;

    /**
     * 认领映射分页列表（支持 participantId / userId 过滤）。
     *
     * @param query 查询条件
     * @return 分页数据
     * @关联表 jst_participant_user_map / jst_user / jst_participant
     * @关联状态机 SM-14
     * @关联权限 jst:participant:list
     */
    @PreAuthorize("@ss.hasPermi('jst:participant:list')")
    @GetMapping("/list")
    public TableDataInfo list(@Validated ParticipantUserMapQueryReqDTO query) {
        startPage();
        List<ParticipantUserMapAdminResVO> rows = participantAdminService.selectParticipantUserMapList(query);
        return getDataTable(rows);
    }
}
