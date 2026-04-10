package com.ruoyi.jst.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 证书模板保存请求。
 *
 * @author jst
 * @since 1.0.0
 */
public class CertTemplateReqDTO {

    /** 模板名称。 */
    @NotBlank(message = "模板名称不能为空")
    @Size(max = 128, message = "模板名称长度不能超过128个字符")
    private String templateName;

    /** 底图 URL 或 OSS objectKey。 */
    @Size(max = 255, message = "底图地址长度不能超过255个字符")
    private String bgImage;

    /** 占位变量布局 JSON。 */
    @Size(max = 4000, message = "布局配置长度不能超过4000个字符")
    private String layoutJson;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getBgImage() {
        return bgImage;
    }

    public void setBgImage(String bgImage) {
        this.bgImage = bgImage;
    }

    public String getLayoutJson() {
        return layoutJson;
    }

    public void setLayoutJson(String layoutJson) {
        this.layoutJson = layoutJson;
    }
}
