package com.ruoyi.jst.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ChannelParticipantUpdateReqDTO {

    @NotBlank(message = "name can not be blank")
    @Size(max = 64, message = "name length must be <= 64")
    private String name;

    @Pattern(
            regexp = "^(|1[3-9]\\d{9}|1\\d{2}\\*{4}\\d{4})$",
            message = "guardianMobile must be a valid mobile or masked mobile")
    private String guardianMobile;

    @Size(max = 128, message = "school length must be <= 128")
    private String school;

    @Size(max = 64, message = "className length must be <= 64")
    private String className;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGuardianMobile() {
        return guardianMobile;
    }

    public void setGuardianMobile(String guardianMobile) {
        this.guardianMobile = guardianMobile;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
