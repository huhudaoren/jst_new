package com.ruoyi.jst.organizer.controller.public_;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.jst.common.oss.OssService;
import com.ruoyi.jst.organizer.dto.PartnerApplyReqDTO;
import com.ruoyi.jst.organizer.service.PartnerApplyService;
import com.ruoyi.jst.organizer.vo.PartnerApplyStatusResVO;
import com.ruoyi.jst.organizer.vo.PartnerApplySubmitResVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 赛事方入驻申请-公开接口 Controller
 *
 * @author jst
 * @since 1.0.0
 */
@Anonymous
@Validated
@RestController
@RequestMapping("/jst/public/organizer/partner/apply")
public class PublicPartnerApplyController extends BaseController {

    @Autowired
    private PartnerApplyService partnerApplyService;

    @Autowired
    private OssService ossService;

    /**
     * 公开提交赛事方入驻申请。
     *
     * @param req 申请请求
     * @return 提交结果
     */
    @Anonymous
    @PostMapping
    public AjaxResult submit(@Valid @RequestBody PartnerApplyReqDTO req) {
        PartnerApplySubmitResVO vo = partnerApplyService.submitPublicApply(req);
        return AjaxResult.success(vo);
    }

    /**
     * 公开查询申请状态。
     *
     * @param applyNo 申请单号
     * @return 状态结果
     */
    @Anonymous
    @GetMapping("/{applyNo}/status")
    public AjaxResult status(@PathVariable("applyNo") String applyNo) {
        PartnerApplyStatusResVO vo = partnerApplyService.queryPublicStatus(applyNo);
        return AjaxResult.success(vo);
    }

    /**
     * 公开上传赛事方入驻资质附件。
     *
     * @param file 资质附件
     * @return OSS objectKey 与临时签名访问地址
     */
    @Anonymous
    @PostMapping("/upload")
    public AjaxResult upload(@RequestParam("file") MultipartFile file) {
        String objectKey = ossService.uploadWithCheck(file, "partner-apply", OssService.DOCUMENT_MIME);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("objectKey", objectKey);
        data.put("url", ossService.signUrl(objectKey, 600));
        data.put("originalFilename", file.getOriginalFilename());
        return AjaxResult.success(data);
    }
}
