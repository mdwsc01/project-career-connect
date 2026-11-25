package com.minhduc.careerconnect.dto;

/** Thang tự đánh giá kỹ năng 1–5 kèm mô tả từng mức (task T4E796B). */
public enum SkillLevel {

    BEGINNER(1, "Mới làm quen", "Biết khái niệm, đã thử qua hoặc đang học."),
    BASIC(2, "Cơ bản", "Làm được việc đơn giản khi có hướng dẫn hoặc tài liệu."),
    INTERMEDIATE(3, "Khá", "Tự làm được phần lớn công việc thường gặp."),
    ADVANCED(4, "Thành thạo", "Xử lý được việc khó, có thể hướng dẫn người khác."),
    EXPERT(5, "Chuyên gia", "Hiểu sâu, tối ưu và giải quyết được vấn đề phức tạp.");

    private final int value;
    private final String label;
    private final String description;

    SkillLevel(int value, String label, String description) {
        this.value = value;
        this.label = label;
        this.description = description;
    }

    public int getValue() { return value; }
    public String getLabel() { return label; }
    public String getDescription() { return description; }

    public static SkillLevel of(int value) {
        for (SkillLevel level : values()) {
            if (level.value == value) {
                return level;
            }
        }
        throw new IllegalArgumentException("Mức kỹ năng không hợp lệ: " + value);
    }
}
