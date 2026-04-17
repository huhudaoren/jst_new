package com.ruoyi.jst.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Date;

/**
 * 学生自建参赛档案-入参
 * <p>
 * 小程序"我的档案"页用户自行新建档案时的入参。
 * 创建即认领，落库时 claim_status=manual_claimed，同步写 participant_user_map。
 *
 * @author jst
 * @since 1.0.0
 */
public class SelfCreateParticipantReqDTO {

    @NotBlank(message = "参赛人姓名不能为空")
    @Size(max = 64, message = "姓名最长64字符")
    private String name;

    /** 性别: 0未知 1男 2女 */
    private Integer gender;

    private Date birthday;

    private Integer age;

    @Size(max = 128, message = "学校名最长128字符")
    private String school;

    @Size(max = 64, message = "班级名最长64字符")
    private String className;

    @Size(max = 64, message = "监护人姓名最长64字符")
    private String guardianName;

    @Size(max = 11, message = "监护人手机号最长11字符")
    private String guardianMobile;

    @Size(max = 32, message = "证件类型最长32字符")
    private String idCardType;

    @Size(max = 64, message = "证件号最长64字符")
    private String idCardNo;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }
    public Date getBirthday() { return birthday; }
    public void setBirthday(Date birthday) { this.birthday = birthday; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getGuardianName() { return guardianName; }
    public void setGuardianName(String guardianName) { this.guardianName = guardianName; }
    public String getGuardianMobile() { return guardianMobile; }
    public void setGuardianMobile(String guardianMobile) { this.guardianMobile = guardianMobile; }
    public String getIdCardType() { return idCardType; }
    public void setIdCardType(String idCardType) { this.idCardType = idCardType; }
    public String getIdCardNo() { return idCardNo; }
    public void setIdCardNo(String idCardNo) { this.idCardNo = idCardNo; }
}
