package com.ruoyi.jst.event.util;

import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
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

    private static final String HMAC_ALGO = "HmacSHA256";

    @Value("${jst.biz.writeoff-qr-secret:jst-writeoff-secret}")
    private String secret;

    public String generateToken() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid + "." + sign(uuid);
    }

    public boolean isValid(String token) {
        if (StringUtils.isBlank(token) || !token.contains(".")) {
            return false;
        }
        String[] parts = token.split("\\.", 2);
        if (parts.length != 2 || StringUtils.isBlank(parts[0]) || StringUtils.isBlank(parts[1])) {
            return false;
        }
        return sign(parts[0]).equals(parts[1]);
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
