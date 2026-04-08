package com.ruoyi.jst.common.crypto;

import com.ruoyi.common.exception.ServiceException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES-256-CBC 字段级加解密
 * <p>
 * 用于 jst_user.id_card_no / jst_channel.bank_account_no 等敏感字段。
 * 密钥来源：环境变量 JST_AES_KEY（32 字节 base64 编码后注入 application.yml）。
 * 密文格式：base64( IV(16字节) + CIPHERTEXT )
 * <p>
 * 配置项：jst.security.aes-key
 * 密钥生成：openssl rand -base64 32（运维操作，参见 16-安全与敏感字段.md）
 *
 * @author jst
 * @since 1.0.0
 */
@Component
public class JstCipher {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORM = "AES/CBC/PKCS5Padding";
    private static final int IV_LENGTH = 16;

    @Value("${jst.security.aes-key:}")
    private String aesKeyBase64;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        if (aesKeyBase64 == null || aesKeyBase64.isEmpty()) {
            throw new IllegalStateException(
                "未配置 jst.security.aes-key，请在环境变量 JST_AES_KEY 注入 32 字节 base64 密钥");
        }
        byte[] keyBytes = Base64.getDecoder().decode(aesKeyBase64);
        if (keyBytes.length != 32) {
            throw new IllegalStateException("AES key 必须为 32 字节（256 bit），当前 " + keyBytes.length);
        }
        this.secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
    }

    /**
     * 加密
     * @param plain 明文（null/empty 直接返回原值）
     * @return base64( IV + cipherBytes )
     */
    public String encrypt(String plain) {
        if (plain == null || plain.isEmpty()) {
            return plain;
        }
        try {
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            Cipher cipher = Cipher.getInstance(TRANSFORM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] cipherBytes = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
            byte[] combined = new byte[iv.length + cipherBytes.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherBytes, 0, combined, iv.length, cipherBytes.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new ServiceException("AES 加密失败: " + e.getMessage());
        }
    }

    /**
     * 解密
     * @param cipherText base64( IV + cipherBytes )（null/empty 直接返回原值）
     */
    public String decrypt(String cipherText) {
        if (cipherText == null || cipherText.isEmpty()) {
            return cipherText;
        }
        try {
            byte[] combined = Base64.getDecoder().decode(cipherText);
            if (combined.length < IV_LENGTH) {
                throw new ServiceException("密文长度异常");
            }
            byte[] iv = new byte[IV_LENGTH];
            byte[] cipherBytes = new byte[combined.length - IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, IV_LENGTH);
            System.arraycopy(combined, IV_LENGTH, cipherBytes, 0, cipherBytes.length);
            Cipher cipher = Cipher.getInstance(TRANSFORM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            return new String(cipher.doFinal(cipherBytes), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new ServiceException("AES 解密失败: " + e.getMessage());
        }
    }
}
