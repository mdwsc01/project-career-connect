package com.minhduc.careerconnect.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.minhduc.careerconnect.validation.ValidDateRange;

/** Form thêm/sửa học vấn (task T4E7969). */
@ValidDateRange
public class EducationForm {

    @NotBlank(message = "Vui lòng nhập tên trường")
    @Size(max = 200, message = "Tên trường tối đa 200 ký tự")
    private String school;

    @NotBlank(message = "Vui lòng nhập chuyên ngành")
    @Size(max = 150, message = "Chuyên ngành tối đa 150 ký tự")
    private String major;

    @NotNull(message = "Vui lòng chọn ngày bắt đầu")
    @PastOrPresent(message = "Ngày bắt đầu không được ở tương lai")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    /** Để trống nghĩa là đang học. */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    /** Không bắt buộc, thang 4. */
    @DecimalMin(value = "0.0", message = "GPA phải từ 0 đến 4")
    @DecimalMax(value = "4.0", message = "GPA phải từ 0 đến 4")
    @Digits(integer = 1, fraction = 2, message = "GPA tối đa 2 chữ số thập phân (VD: 3.25)")
    private BigDecimal gpa;

    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BigDecimal getGpa() { return gpa; }
    public void setGpa(BigDecimal gpa) { this.gpa = gpa; }
}
