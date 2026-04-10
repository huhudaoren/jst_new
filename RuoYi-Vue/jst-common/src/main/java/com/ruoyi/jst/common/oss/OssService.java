package com.ruoyi.jst.common.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.ruoyi.common.exception.ServiceException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * 阿里云 OSS 文件服务
 * <p>
 * 强制约束（参见 16-安全与敏感字段.md §5.5）：
 * <ul>
 *   <li>Bucket 必须为私有读写</li>
 *   <li>访问通过签名 URL，TTL：图片 1h，凭证/合同 10min</li>
 *   <li>禁止前端直传，所有上传走本服务</li>
 *   <li>上传时校验 MIME + 大小</li>
 * </ul>
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class OssService {

    private static final Logger log = LoggerFactory.getLogger(OssService.class);

    /** 默认允许的图片 MIME */
    public static final Set<String> IMAGE_MIME = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp");

    /** 凭证/合同允许的 MIME */
    public static final Set<String> DOCUMENT_MIME = Set.of(
            "image/jpeg", "image/png", "application/pdf");

    /** 默认大小限制 10MB */
    public static final long MAX_SIZE = 10L * 1024 * 1024;

    @Autowired
    private OssProperties props;

    private OSS ossClient;

    @PostConstruct
    public void init() {
        if (props.getAccessKeyId() == null || props.getAccessKeyId().isEmpty()) {
            log.warn("[OSS] 未配置 jst.oss.access-key-id，OssService 不可用（开发期可忽略）");
            return;
        }
        this.ossClient = new OSSClientBuilder().build(
                props.getEndpoint(), props.getAccessKeyId(), props.getAccessKeySecret());
        log.info("[OSS] 客户端初始化完成 bucket={}", props.getBucket());
    }

    @PreDestroy
    public void destroy() {
        if (ossClient != null) ossClient.shutdown();
    }

    /**
     * 上传文件并校验
     * @param file       MultipartFile
     * @param bizDir     业务子目录（如 enroll/voucher/contract）
     * @param allowedMime 允许的 MIME 集合
     * @return ObjectKey（不含 host），写入 DB 的 *_url 字段
     */
    public String uploadWithCheck(MultipartFile file, String bizDir, Set<String> allowedMime) {
        if (file == null || file.isEmpty()) {
            throw new ServiceException("文件不能为空");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new ServiceException("文件大小超过 " + (MAX_SIZE / 1024 / 1024) + "MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !allowedMime.contains(contentType)) {
            throw new ServiceException("不支持的文件类型: " + contentType);
        }
        ensureClientInitialized();
        String objectKey = buildObjectKey(bizDir, file.getOriginalFilename());
        try (InputStream in = file.getInputStream()) {
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(file.getSize());
            meta.setContentType(contentType);
            ossClient.putObject(props.getBucket(), objectKey, in, meta);
            log.info("[OSS] 上传成功 key={} size={}B", objectKey, file.getSize());
            return objectKey;
        } catch (Exception e) {
            log.error("[OSS] 上传失败 key={}", objectKey, e);
            throw new ServiceException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 生成签名访问 URL
     * @param objectKey DB 中存储的 key
     * @param expireSec TTL 秒数
     */
    public String signUrl(String objectKey, long expireSec) {
        if (objectKey == null || objectKey.isEmpty()) return null;
        ensureClientInitialized();
        Date expire = new Date(System.currentTimeMillis() + expireSec * 1000);
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(props.getBucket(), objectKey);
        req.setExpiration(expire);
        URL url = ossClient.generatePresignedUrl(req);
        return url.toString();
    }

    private void ensureClientInitialized() {
        if (ossClient == null) {
            throw new ServiceException("OSS 服务未配置或未初始化");
        }
    }

    private String buildObjectKey(String bizDir, String originalName) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf('.'));
        }
        return "jst/" + bizDir + "/" + date + "/" + UUID.randomUUID().toString().replace("-", "") + ext;
    }
}
