package com.ruoyi.jst.common.oss;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云 OSS 配置
 * <p>
 * 配置项前缀：jst.oss
 * AK/SK 必须通过环境变量注入：JST_OSS_AK / JST_OSS_SK
 *
 * @author jst
 * @since 1.0.0
 */
@Configuration
@ConfigurationProperties(prefix = "jst.oss")
public class OssProperties {

    /** OSS endpoint，例如 oss-cn-hangzhou.aliyuncs.com */
    private String endpoint;

    /** Access Key ID（环境变量 JST_OSS_AK） */
    private String accessKeyId;

    /** Access Key Secret（环境变量 JST_OSS_SK） */
    private String accessKeySecret;

    /** Bucket 名称 */
    private String bucket;

    /** Bucket 公网访问 base url（用于拼装签名 URL） */
    private String baseUrl;

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public String getAccessKeyId() { return accessKeyId; }
    public void setAccessKeyId(String accessKeyId) { this.accessKeyId = accessKeyId; }
    public String getAccessKeySecret() { return accessKeySecret; }
    public void setAccessKeySecret(String accessKeySecret) { this.accessKeySecret = accessKeySecret; }
    public String getBucket() { return bucket; }
    public void setBucket(String bucket) { this.bucket = bucket; }
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
}
