package com.ruoyi.jst.event.service;

import com.ruoyi.jst.event.dto.EnrollAuditReqDTO;
import com.ruoyi.jst.event.dto.EnrollDraftDTO;
import com.ruoyi.jst.event.dto.EnrollQueryReqDTO;
import com.ruoyi.jst.event.dto.EnrollSubmitDTO;
import com.ruoyi.jst.event.dto.EnrollSupplementDTO;
import com.ruoyi.jst.event.dto.ScoreItemReqDTO;
import com.ruoyi.jst.event.dto.TeamEnrollReqDTO;
import com.ruoyi.jst.event.vo.EnrollChannelGroupVO;
import com.ruoyi.jst.event.vo.EnrollDetailVO;
import com.ruoyi.jst.event.vo.EnrollListVO;
import com.ruoyi.jst.event.vo.EnrollSubmitResVO;
import com.ruoyi.jst.event.vo.TeamEnrollResVO;

import java.util.List;

/**
 * 报名记录领域服务接口。
 *
 * @author jst
 * @since 1.0.0
 */
public interface EnrollRecordService {

    /**
     * 保存报名草稿。
     *
     * @param req 草稿请求
     * @return 保存结果
     */
    EnrollSubmitResVO saveDraft(EnrollDraftDTO req);

    /**
     * 提交报名。
     *
     * @param req 提交请求
     * @return 提交结果
     */
    EnrollSubmitResVO submit(EnrollSubmitDTO req);

    /**
     * 查询小程序报名详情。
     *
     * @param enrollId 报名 ID
     * @return 详情
     */
    EnrollDetailVO getWxDetail(Long enrollId);

    /**
     * 提交补件。
     *
     * @param enrollId 报名 ID
     * @param req      补件请求
     */
    void supplement(Long enrollId, EnrollSupplementDTO req);

    /**
     * 查询后台报名列表。
     *
     * @param query 查询条件
     * @return 列表
     */
    List<EnrollListVO> selectAdminList(EnrollQueryReqDTO query);

    /**
     * 小程序-我的报名列表 (从当前登录上下文取 userId)
     *
     * @param auditStatus 可选,按审核状态过滤
     * @return 列表
     */
    List<EnrollListVO> selectMyList(String auditStatus);

    /**
     * 查询后台报名详情。
     *
     * @param enrollId 报名 ID
     * @return 详情
     */
    EnrollDetailVO getAdminDetail(Long enrollId);

    /**
     * 审核报名记录。
     *
     * @param enrollId 报名 ID
     * @param req      审核请求
     */
    void audit(Long enrollId, EnrollAuditReqDTO req);

    /**
     * 批量审核报名记录。
     *
     * @param enrollIds   报名ID集合
     * @param auditStatus 目标审核状态
     * @param remark      审核备注
     * @param scores      各成绩项打分（批量通过时统一分数，可选）
     * @return 成功处理数量
     */
    int batchAudit(List<Long> enrollIds, String auditStatus, String remark, List<ScoreItemReqDTO> scores);

    /**
     * 团队报名提交。
     *
     * @param req 团队报名请求
     * @return 团队报名结果
     * @关联表 jst_enroll_record, jst_contest, jst_appointment_slot
     */
    TeamEnrollResVO submitTeam(TeamEnrollReqDTO req);

    /**
     * 按"用户所属渠道"聚合指定赛事的报名记录，用于管理端「按渠道查看」Tab。
     *
     * @param contestId 赛事 ID
     * @return 聚合结果列表
     * @关联任务 ADMIN-UX-B3 主线 B
     */
    List<EnrollChannelGroupVO> groupByBoundChannel(Long contestId);
}
