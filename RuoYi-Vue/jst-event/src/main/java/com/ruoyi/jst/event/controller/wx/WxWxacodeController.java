package com.ruoyi.jst.event.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 小程序码生成 Controller。
 * <p>
 * 如微信小程序 API 未配置（mock 模式），返回占位图 URL。
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/wx/wxacode")
public class WxWxacodeController extends BaseController {

    /** 小程序认证 mock 开关。true = 未接入真实微信 API。 */
    @Value("${jst.wxauth.mock:true}")
    private boolean wxAuthMock;

    /** 占位图 URL（可替换为项目 Logo CDN 地址）。 */
    private static final String PLACEHOLDER_IMAGE = "/static/images/wxacode-placeholder.png";

    /**
     * 获取小程序码图片 URL。
     * <p>
     * Mock 模式下返回占位图 URL，真实模式下调用微信 wxacode.getUnlimited API（待实现）。
     *
     * @param path  小程序页面路径
     * @param scene 场景参数
     * @return 小程序码图片 URL
     */
    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasPermi('jst:event:wxacode:get')")
    @GetMapping
    public AjaxResult getWxacode(@RequestParam("path") String path,
                                 @RequestParam("scene") String scene) {
        Map<String, Object> data = new LinkedHashMap<>();
        if (wxAuthMock) {
            data.put("wxacodeUrl", PLACEHOLDER_IMAGE);
            data.put("mock", true);
        } else {
            // TODO: 调用微信 wxacode.getUnlimited API 并缓存结果
            data.put("wxacodeUrl", PLACEHOLDER_IMAGE);
            data.put("mock", true);
        }
        data.put("path", path);
        data.put("scene", scene);
        return AjaxResult.success(data);
    }
}
