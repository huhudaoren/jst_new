package com.ruoyi.jst.event.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.audit.OperateLog;
import com.ruoyi.jst.common.context.JstLoginContext;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.id.SnowflakeIdWorker;
import com.ruoyi.jst.common.oss.OssService;
import com.ruoyi.jst.common.util.MaskUtil;
import com.ruoyi.jst.event.domain.JstCertRecord;
import com.ruoyi.jst.event.domain.JstCertTemplate;
import com.ruoyi.jst.event.domain.JstContest;
import com.ruoyi.jst.event.dto.CertBatchGrantReqDTO;
import com.ruoyi.jst.event.dto.CertQueryReqDTO;
import com.ruoyi.jst.event.dto.CertSubmitReviewReqDTO;
import com.ruoyi.jst.event.dto.CertTemplateReqDTO;
import com.ruoyi.jst.event.enums.CertIssueStatus;
import com.ruoyi.jst.event.mapper.ContestMapperExt;
import com.ruoyi.jst.event.mapper.PartnerCertMapper;
import com.ruoyi.jst.event.service.PartnerCertService;
import com.ruoyi.jst.event.vo.CertBatchGrantResVO;
import com.ruoyi.jst.event.vo.CertPreviewResVO;
import com.ruoyi.jst.event.vo.CertScoreRefVO;
import com.ruoyi.jst.event.vo.CertTemplateResVO;
import com.ruoyi.jst.event.vo.PartnerCertResVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * Partner certificate management service.
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class PartnerCertServiceImpl implements PartnerCertService {

    private static final String ROLE_PARTNER = "jst_partner";
    private static final String OWNER_TYPE_PARTNER = "partner";
    private static final String OWNER_TYPE_PLATFORM = "platform";
    private static final String AUDIT_PENDING = "pending";
    private static final String AUDIT_APPROVED = "approved";

    @Autowired
    private PartnerCertMapper partnerCertMapper;

    @Autowired
    private ContestMapperExt contestMapperExt;

    @Autowired
    private OssService ossService;

    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    /**
     * Saves a partner-owned certificate template.
     *
     * @param req template request
     * @param file optional background image
     * @return template id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "cert", action = "CERT_TEMPLATE_SAVE", target = "#{req.templateName}", recordResult = true)
    public Long saveTemplate(CertTemplateReqDTO req, MultipartFile file) {
        // TX: template creation binds the current partner and waits for platform review.
        validateTemplateReq(req);
        Long partnerId = currentPartnerId();
        String bgImage = trimToNull(req.getBgImage());
        if (file != null && !file.isEmpty()) {
            bgImage = ossService.uploadWithCheck(file, "cert-template", OssService.IMAGE_MIME);
        }
        if (StringUtils.isBlank(bgImage)) {
            throw new ServiceException("Certificate template background is required", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        Date now = DateUtils.getNowDate();
        JstCertTemplate template = new JstCertTemplate();
        template.setTemplateName(req.getTemplateName().trim());
        template.setOwnerType(OWNER_TYPE_PARTNER);
        template.setOwnerId(partnerId);
        template.setBgImage(bgImage);
        template.setLayoutJson(StringUtils.isBlank(req.getLayoutJson()) ? "{}" : req.getLayoutJson());
        template.setAuditStatus(AUDIT_PENDING);
        template.setStatus(1);
        template.setCreateBy(currentOperatorName());
        template.setCreateTime(now);
        template.setUpdateBy(currentOperatorName());
        template.setUpdateTime(now);
        template.setDelFlag("0");
        int rows = partnerCertMapper.insertTemplate(template);
        if (rows <= 0 || template.getTemplateId() == null) {
            throw new ServiceException("Certificate template save failed", BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        return template.getTemplateId();
    }

    /**
     * Lists templates available to the current partner.
     *
     * @return template list
     */
    @Override
    public List<CertTemplateResVO> listTemplates() {
        return partnerCertMapper.selectTemplateList(currentPartnerId());
    }

    /**
     * Lists certificate records.
     *
     * @param query query DTO carrying PartnerDataScope
     * @return certificate list
     */
    @Override
    public List<PartnerCertResVO> listCerts(CertQueryReqDTO query) {
        if (query == null) {
            query = new CertQueryReqDTO();
        }
        List<PartnerCertResVO> list = partnerCertMapper.selectCertList(query);
        for (PartnerCertResVO vo : list) {
            vo.setGuardianMobileMasked(MaskUtil.mobile(vo.getGuardianMobileMasked()));
            vo.setDisplayStatus(resolveCertDisplayStatus(vo.getIssueStatus()));
        }
        return list;
    }

    /**
     * Creates draft certificates for published scores.
     *
     * @param req batch grant request
     * @return grant result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "cert", action = "CERT_BATCH_GRANT", target = "#{req.contestId}", recordResult = true)
    public CertBatchGrantResVO batchGrant(CertBatchGrantReqDTO req) {
        // TX: batch grant validates template, published scores, and duplicate certificates.
        JstContest contest = getRequiredContest(req.getContestId());
        assertContestOwnership(contest);
        JstCertTemplate template = getRequiredTemplate(req.getTemplateId());
        assertTemplateUsable(template);
        List<CertScoreRefVO> scores = partnerCertMapper.selectPublishedScoresForBatch(req.getContestId(), req.getScoreIds());
        if (scores.isEmpty()) {
            throw new ServiceException(BizErrorCode.JST_EVENT_CERT_BATCH_EMPTY.message(),
                    BizErrorCode.JST_EVENT_CERT_BATCH_EMPTY.code());
        }
        Date now = DateUtils.getNowDate();
        CertBatchGrantResVO result = new CertBatchGrantResVO();
        result.setCreatedCount(0);
        result.setSkippedCount(0);
        List<Long> certIds = new ArrayList<>();
        for (CertScoreRefVO score : scores) {
            if (partnerCertMapper.countCertByScoreId(score.getScoreId()) > 0) {
                result.setSkippedCount(result.getSkippedCount() + 1);
                continue;
            }
            JstCertRecord cert = buildCertRecord(score, template.getTemplateId(), now);
            partnerCertMapper.insertCert(cert);
            if (cert.getCertId() != null) {
                certIds.add(cert.getCertId());
            }
            result.setCreatedCount(result.getCreatedCount() + 1);
        }
        result.setCertIds(certIds);
        if (result.getCreatedCount() == 0) {
            throw new ServiceException("Published scores already have certificates",
                    BizErrorCode.JST_EVENT_CERT_BATCH_EMPTY.code());
        }
        return result;
    }

    /**
     * Submits certificates for platform review.
     *
     * @param req submit request
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "cert", action = "CERT_SUBMIT_REVIEW", target = "#{req.certIds}")
    public void submitReview(CertSubmitReviewReqDTO req) {
        // TX: submit review checks ownership and SM-20 state per certificate.
        Date now = DateUtils.getNowDate();
        for (Long certId : req.getCertIds()) {
            JstCertRecord cert = getRequiredCert(certId);
            assertCertOwnership(cert);
            CertIssueStatus current = CertIssueStatus.fromDb(cert.getIssueStatus());
            current.assertCanTransitTo(CertIssueStatus.PENDING); // SM-20
            int updated = partnerCertMapper.updateIssueStatusByExpected(certId, current.dbValue(),
                    CertIssueStatus.PENDING.dbValue(), currentOperatorName(), now);
            if (updated == 0) {
                throw new ServiceException("Certificate review submit failed: status changed",
                        BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }
        }
    }

    /**
     * Renders a placeholder certificate preview.
     *
     * @param certId certificate id
     * @return preview image data URI
     */
    @Override
    public CertPreviewResVO preview(Long certId) {
        JstCertRecord cert = getRequiredCert(certId);
        assertCertOwnership(cert);
        PartnerCertResVO detail = partnerCertMapper.selectCertDetail(certId);
        if (detail == null) {
            throw new ServiceException(BizErrorCode.JST_EVENT_CERT_NOT_FOUND.message(),
                    BizErrorCode.JST_EVENT_CERT_NOT_FOUND.code());
        }
        detail.setGuardianMobileMasked(MaskUtil.mobile(detail.getGuardianMobileMasked()));
        CertPreviewResVO vo = new CertPreviewResVO();
        vo.setCertId(certId);
        vo.setCertNo(detail.getCertNo());
        vo.setPreviewImage(renderPreview(detail));
        return vo;
    }

    private void validateTemplateReq(CertTemplateReqDTO req) {
        if (req == null || StringUtils.isBlank(req.getTemplateName())) {
            throw new ServiceException("Certificate template name is required", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
    }

    private JstCertRecord buildCertRecord(CertScoreRefVO score, Long templateId, Date now) {
        long seq = snowflakeIdWorker.nextId();
        JstCertRecord cert = new JstCertRecord();
        cert.setCertNo(String.format("JST-%s-ART-%05d", DateUtils.dateTimeNow(DateUtils.YYYY), Math.abs(seq % 100000)));
        cert.setContestId(score.getContestId());
        cert.setScoreId(score.getScoreId());
        cert.setEnrollId(score.getEnrollId());
        cert.setUserId(score.getUserId());
        cert.setParticipantId(score.getParticipantId());
        cert.setTemplateId(templateId);
        cert.setIssueStatus(CertIssueStatus.DRAFT.dbValue());
        cert.setVerifyCode("VC" + Math.abs(seq));
        cert.setCreateBy(currentOperatorName());
        cert.setCreateTime(now);
        cert.setUpdateBy(currentOperatorName());
        cert.setUpdateTime(now);
        cert.setDelFlag("0");
        return cert;
    }

    private JstContest getRequiredContest(Long contestId) {
        if (contestId == null) {
            throw new ServiceException("Contest id is required", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        JstContest contest = contestMapperExt.selectById(contestId);
        if (contest == null) {
            throw new ServiceException(BizErrorCode.JST_EVENT_CONTEST_NOT_FOUND.message(),
                    BizErrorCode.JST_EVENT_CONTEST_NOT_FOUND.code());
        }
        return contest;
    }

    private JstCertTemplate getRequiredTemplate(Long templateId) {
        if (templateId == null) {
            throw new ServiceException("Certificate template id is required", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        JstCertTemplate template = partnerCertMapper.selectTemplateById(templateId);
        if (template == null) {
            throw new ServiceException(BizErrorCode.JST_EVENT_CERT_TEMPLATE_NOT_FOUND.message(),
                    BizErrorCode.JST_EVENT_CERT_TEMPLATE_NOT_FOUND.code());
        }
        return template;
    }

    private JstCertRecord getRequiredCert(Long certId) {
        if (certId == null) {
            throw new ServiceException("Certificate id is required", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        JstCertRecord cert = partnerCertMapper.selectCertById(certId);
        if (cert == null) {
            throw new ServiceException(BizErrorCode.JST_EVENT_CERT_NOT_FOUND.message(),
                    BizErrorCode.JST_EVENT_CERT_NOT_FOUND.code());
        }
        return cert;
    }

    private void assertTemplateUsable(JstCertTemplate template) {
        if (template.getStatus() == null || template.getStatus() != 1) {
            throw new ServiceException("Certificate template is disabled", BizErrorCode.JST_EVENT_CERT_TEMPLATE_NOT_FOUND.code());
        }
        Long currentPartnerId = currentPartnerId();
        boolean ownedByPartner = OWNER_TYPE_PARTNER.equals(template.getOwnerType())
                && currentPartnerId.equals(template.getOwnerId());
        boolean approvedPlatform = OWNER_TYPE_PLATFORM.equals(template.getOwnerType())
                && AUDIT_APPROVED.equals(template.getAuditStatus());
        if ((!ownedByPartner && !approvedPlatform) || !AUDIT_APPROVED.equals(template.getAuditStatus())) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
    }

    private void assertCertOwnership(JstCertRecord cert) {
        JstContest contest = getRequiredContest(cert.getContestId());
        assertContestOwnership(contest);
    }

    private void assertContestOwnership(JstContest contest) {
        if (!JstLoginContext.hasRole(ROLE_PARTNER)) {
            return;
        }
        Long currentPartnerId = JstLoginContext.currentPartnerId();
        if (currentPartnerId == null || !currentPartnerId.equals(contest.getPartnerId())) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
    }

    private Long currentPartnerId() {
        Long partnerId = JstLoginContext.currentPartnerId();
        if (partnerId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        return partnerId;
    }

    private String resolveCertDisplayStatus(String issueStatus) {
        return CertIssueStatus.ISSUED.dbValue().equals(issueStatus) ? "granted" : issueStatus;
    }

    private String renderPreview(PartnerCertResVO cert) {
        try {
            BufferedImage image = new BufferedImage(1000, 700, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(250, 248, 240));
            g.fillRect(0, 0, 1000, 700);
            g.setColor(new Color(176, 132, 46));
            g.setStroke(new BasicStroke(8));
            g.drawRect(40, 40, 920, 620);
            g.setStroke(new BasicStroke(2));
            g.drawRect(70, 70, 860, 560);
            g.setColor(new Color(66, 55, 39));
            g.setFont(new Font("Serif", Font.BOLD, 54));
            drawCentered(g, "Award Certificate", 500, 160);
            g.setFont(new Font("SansSerif", Font.PLAIN, 28));
            drawCentered(g, StringUtils.isBlank(cert.getContestName()) ? "JingSaiTong Contest" : cert.getContestName(), 500, 230);
            g.setFont(new Font("SansSerif", Font.BOLD, 38));
            drawCentered(g, StringUtils.isBlank(cert.getParticipantName()) ? "Participant" : cert.getParticipantName(), 500, 330);
            g.setFont(new Font("SansSerif", Font.PLAIN, 30));
            drawCentered(g, "Award: " + (StringUtils.isBlank(cert.getAwardLevel()) ? "Excellent" : cert.getAwardLevel()), 500, 410);
            g.setFont(new Font("SansSerif", Font.PLAIN, 22));
            drawCentered(g, "Certificate No: " + cert.getCertNo(), 500, 530);
            g.dispose();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(image, "png", output);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(output.toByteArray());
        } catch (Exception e) {
            throw new ServiceException("Certificate preview render failed", BizErrorCode.JST_EVENT_CERT_ILLEGAL_TRANSIT.code());
        }
    }

    private void drawCentered(Graphics2D g, String text, int centerX, int y) {
        int width = g.getFontMetrics().stringWidth(text);
        g.drawString(text, centerX - width / 2, y);
    }

    private String currentOperatorName() {
        String username = SecurityUtils.getUsername();
        return StringUtils.isBlank(username) ? "system" : username;
    }

    private String trimToNull(String text) {
        return StringUtils.isBlank(text) ? null : text.trim();
    }
}
