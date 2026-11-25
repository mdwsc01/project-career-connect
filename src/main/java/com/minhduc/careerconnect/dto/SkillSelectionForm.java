package com.minhduc.careerconnect.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Form sinh viên thêm kỹ năng vào hồ sơ (task T4E796B). */
public class SkillSelectionForm {

    /** Tên kỹ năng chọn từ ô tìm nhanh (phải có trong danh mục, đang hiện). */
    @NotBlank(message = "Vui lòng chọn kỹ năng")
    private String skillName;

    @NotNull(message = "Vui lòng chọn mức tự đánh giá")
    @Min(value = 1, message = "Mức phải từ 1 đến 5")
    @Max(value = 5, message = "Mức phải từ 1 đến 5")
    private Integer level;

    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }

    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
}
