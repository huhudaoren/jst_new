package com.ruoyi.jst.event.service;

import com.ruoyi.jst.event.dto.AuditReqDTO;
import com.ruoyi.jst.event.dto.ContestQueryReqDTO;
import com.ruoyi.jst.event.dto.ContestSaveReqDTO;
import com.ruoyi.jst.event.dto.WxContestQueryDTO;
import com.ruoyi.jst.event.vo.CategoryStatVO;
import com.ruoyi.jst.event.vo.ContestDetailVO;
import com.ruoyi.jst.event.vo.ContestListVO;
import com.ruoyi.jst.event.vo.WxContestCardVO;
import com.ruoyi.jst.event.vo.WxContestDetailVO;

import java.util.List;

/**
 * 赛事领域服务。
 *
 * @author jst
 * @since 1.0.0
 */
public interface ContestService {

    Long addContest(ContestSaveReqDTO req);

    void editContest(ContestSaveReqDTO req);

    void submitContest(Long contestId);

    void approveContest(Long contestId, AuditReqDTO req);

    void rejectContest(Long contestId, AuditReqDTO req);

    void onlineContest(Long contestId);

    void offlineContest(Long contestId);

    List<ContestListVO> selectAdminList(ContestQueryReqDTO query);

    ContestDetailVO getAdminDetail(Long contestId);

    List<WxContestCardVO> selectWxList(WxContestQueryDTO query);

    WxContestDetailVO getWxDetail(Long contestId);

    List<WxContestCardVO> selectHotList(Integer limit);

    List<CategoryStatVO> selectCategoryStats();
}
