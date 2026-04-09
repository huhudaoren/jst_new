package com.ruoyi.jst.marketing.service;

import com.ruoyi.jst.marketing.dto.RightsIssueDTO;
import com.ruoyi.jst.marketing.dto.RightsTemplateSaveDTO;
import com.ruoyi.jst.marketing.vo.RightsTemplateVO;

import java.util.List;
import java.util.Map;

/**
 * Rights template service.
 */
public interface RightsTemplateService {

    List<RightsTemplateVO> selectAdminList(String rightsName, String rightsType, Integer status);

    RightsTemplateVO getAdminDetail(Long rightsTemplateId);

    Long create(RightsTemplateSaveDTO req);

    void update(RightsTemplateSaveDTO req);

    void delete(Long rightsTemplateId);

    void publish(Long rightsTemplateId);

    void offline(Long rightsTemplateId);

    Map<String, Object> issue(Long rightsTemplateId, RightsIssueDTO req);
}
