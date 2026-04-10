package com.ruoyi.jst.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 批量创建临时参赛档案-单条入参
 * <p>
 * 渠道方批量创建临时档案时，前端传 JSON 数组，每条的结构
 *
 * @author jst
 * @since 1.0.0
 */
public class BatchCreateParticipantReqDTO {

    @NotBlank(message = "参赛人姓名不能为空")
    @Size(max = 64, message = "姓名最长64字符")
    private String name;

    private Integer gender;

    private Integer age;

    @Size(max = 128, message = "学校名最长128字符")
    private String school;

    @Size(max = 64, message = "班级名最长64字符")
    private String className;

    @Size(max = 11, message = "监护人手机号最长11字符")
    private String guardianMobile;

    @Size(max = 64, message = "监护人姓名最长64字符")
    private String guardianName;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getGuardianMobile() { return guardianMobile; }
    public void setGuardianMobile(String guardianMobile) { this.guardianMobile = guardianMobile; }
    public String getGuardianName() { return guardianName; }
    public void setGuardianName(String guardianName) { this.guardianName = guardianName; }
}
