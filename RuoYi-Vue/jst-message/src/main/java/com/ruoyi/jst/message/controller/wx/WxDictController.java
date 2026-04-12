package com.ruoyi.jst.message.controller.wx;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.jst.message.service.NoticeService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 小程序业务字典公开接口 Controller。
 *
 * @author jst
 * @since 1.0.0
 */
@Anonymous
@Validated
@RestController
@RequestMapping("/jst/wx/dict")
public class WxDictController extends BaseController {

    private static final String CONTEST_CATEGORY_DICT_TYPE = "jst_contest_category";
    private static final String COURSE_CATEGORY_DICT_TYPE = "jst_course_category";

    @Autowired
    private NoticeService noticeService;

    /**
     * 查询业务字典下拉项。
     *
     * @param dictType 字典类型
     * @return 字典项列表
     * @关联表 sys_dict_data
     * @关联状态机 无
     * @关联权限 @Anonymous
     */
    @Anonymous
    @GetMapping("/{dictType}")
    public AjaxResult list(
            @PathVariable("dictType")
            @NotBlank(message = "dictType 不能为空")
            @Pattern(regexp = "[a-z0-9_]+", message = "dictType 格式非法")
            String dictType) {
        List<Map<String, Object>> list = noticeService.selectDictOptions(dictType);
        return AjaxResult.success(list);
    }

    /**
     * 查询赛事分类字典。
     *
     * @return 字典项列表（label/value）
     * @关联表 sys_dict_data
     * @关联状态机 无
     * @关联权限 @Anonymous
     */
    @Anonymous
    @GetMapping("/contest-categories")
    public AjaxResult contestCategories() {
        return AjaxResult.success(onlyLabelValue(noticeService.selectDictOptions(CONTEST_CATEGORY_DICT_TYPE)));
    }

    /**
     * 查询课程分类字典。
     *
     * @return 字典项列表（label/value）
     * @关联表 sys_dict_data
     * @关联状态机 无
     * @关联权限 @Anonymous
     */
    @Anonymous
    @GetMapping("/course-categories")
    public AjaxResult courseCategories() {
        return AjaxResult.success(onlyLabelValue(noticeService.selectDictOptions(COURSE_CATEGORY_DICT_TYPE)));
    }

    private List<Map<String, Object>> onlyLabelValue(List<Map<String, Object>> source) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (source == null || source.isEmpty()) {
            return result;
        }
        for (Map<String, Object> item : source) {
            Map<String, Object> simple = new LinkedHashMap<>();
            simple.put("label", item.get("label"));
            simple.put("value", item.get("value"));
            result.add(simple);
        }
        return result;
    }
}
