package com.ruoyi.jst.event.util;

import com.ruoyi.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

/**
 * 核销二维码令牌生成器。
 * <p>
 * 格式：uuid.signature
 */
@Component
public class WriteoffQrCodeGenerator {

    private static final Logger log = LoggerFactory.getLogger(WriteoffQrCodeGenerator.class);
    private static final String HMAC_ALGO = "HmacSHA256";
    private static final String TOKEN_PARAM = "t";

    @Value("${jst.biz.writeoff-qr-secret:jst-writeoff-secret}")
    private String secret;

    @Value("${jst.qrcode.writeoff.base-url:}")
    private String baseUrl;

    /**
     * 生成核销二维码 payload。
     *
     * @return 纯签名 token 或带 baseUrl 的完整链接
     */
    public String generateToken() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String token = uuid + "." + sign(uuid);
        return wrapPayload(token);
    }

    /**
     * 从二维码 payload 中提取待验签 token。
     *
     * @param payload 二维码原始 payload
     * @return 纯签名 token；若 payload 不是 URL 或未携带 t 参数，则原样返回
     */
    public String extractToken(String payload) {
        if (StringUtils.isBlank(payload)) {
            return payload;
        }
        // 扫码器/前端可能引入前后空白字符，先剥离，避免 URI.create 抛 IllegalArgumentException
        String trimmed = payload.trim();
        if (!isHttpPayload(trimmed)) {
            return trimmed;
        }
        try {
            URI uri = URI.create(trimmed);
            String query = uri.getRawQuery();
            if (StringUtils.isBlank(query)) {
                return trimmed;
            }
            for (String pair : query.split("&")) {
                if (StringUtils.isBlank(pair)) {
                    continue;
                }
                int eqIndex = pair.indexOf('=');
                String name = eqIndex >= 0 ? pair.substring(0, eqIndex) : pair;
                if (!TOKEN_PARAM.equalsIgnoreCase(name)) {
                    continue;
                }
                String value = eqIndex >= 0 ? pair.substring(eqIndex + 1) : "";
                return URLDecoder.decode(value, StandardCharsets.UTF_8);
            }
            return trimmed;
        } catch (Exception ex) {
            // 不抛出：上层 isValidToken 会统一拒绝；但记录日志便于线上排查
            log.warn("[writeoff-qr] extractToken failed, fallback to raw payload. reason={}", ex.getMessage());
            return trimmed;
        }
    }

    /**
     * 校验纯签名 token 是否有效。
     *
     * @param token 纯签名 token
     * @return 是否通过 HMAC 校验
     */
    public boolean isValidToken(String token) {
        if (StringUtils.isBlank(token) || !token.contains(".")) {
            return false;
        }
        String[] parts = token.split("\\.", 2);
        if (parts.length != 2 || StringUtils.isBlank(parts[0]) || StringUtils.isBlank(parts[1])) {
            return false;
        }
        return sign(parts[0]).equals(parts[1]);
    }

    private String wrapPayload(String token) {
        if (StringUtils.isBlank(baseUrl)) {
            return token;
        }
        return baseUrl + (baseUrl.contains("?") ? "&" : "?") + TOKEN_PARAM + "=" + token;
    }

    private boolean isHttpPayload(String payload) {
        // URL scheme 按 RFC 3986 是 case-insensitive，这里一并兼容大写/混合
        String lower = payload.length() >= 8 ? payload.substring(0, Math.min(8, payload.length())).toLowerCase() : payload.toLowerCase();
        return lower.startsWith("http://") || lower.startsWith("https://");
    }

    private String sign(String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGO));
            byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (Exception ex) {
            throw new IllegalStateException("生成核销二维码签名失败", ex);
        }
    }
}
