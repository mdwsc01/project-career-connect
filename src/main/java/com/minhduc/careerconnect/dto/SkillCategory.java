package com.minhduc.careerconnect.dto;

/**
 * 5 nhóm kỹ năng (task T4E796A). DB lưu tên hằng (VD: LANGUAGE), giao diện hiện nhãn tiếng Việt.
 */
public enum SkillCategory {

    LANGUAGE("Ngôn ngữ"),
    FRAMEWORK("Framework"),
    DATABASE("CSDL"),
    TOOL("Công cụ"),
    SOFT_SKILL("Kỹ năng mềm");

    private final String label;

    SkillCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
