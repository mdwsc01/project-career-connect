package com.minhduc.careerconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Form thêm/sửa kỹ năng (task T4E796A).
 */
public class SkillForm {

    @NotBlank(message = "Vui lòng nhập tên kỹ năng")
    @Size(max = 100, message = "Tên kỹ năng tối đa 100 ký tự")
    private String name;

    @NotNull(message = "Vui lòng chọn nhóm kỹ năng")
    private SkillCategory category;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SkillCategory getCategory() {
        return category;
    }

    public void setCategory(SkillCategory category) {
        this.category = category;
    }
}
