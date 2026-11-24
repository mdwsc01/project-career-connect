package com.minhduc.careerconnect.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

/** Form sửa thông tin cơ bản ở trang "Hồ sơ của tôi" (task T4E7968). */
public class MyProfileForm {

    @NotBlank(message = "Vui lòng nhập họ tên")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String fullName;

    /** Ô input type="date" gửi lên dạng yyyy-MM-dd; sai định dạng sẽ báo lỗi typeMismatch (messages.properties). */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Past(message = "Ngày sinh phải trước ngày hôm nay")
    private LocalDate dateOfBirth;

    /** Không bắt buộc; nếu nhập: số di động Việt Nam 10 số, bắt đầu 03/05/07/08/09 (hoặc +84). */
    @Pattern(regexp = "^$|^(0|\\+84)(3|5|7|8|9)\\d{8}$",
             message = "Số điện thoại không hợp lệ (VD: 0912345678 hoặc +84912345678)")
    private String phone;

    @Size(max = 2000, message = "Giới thiệu tối đa 2000 ký tự")
    private String summary;

    @NotBlank(message = "Vui lòng nhập vị trí mong muốn")
    @Size(max = 150, message = "Vị trí mong muốn tối đa 150 ký tự")
    private String desiredPosition;

    @Size(max = 100, message = "Nơi làm việc mong muốn tối đa 100 ký tự")
    private String desiredWorkLocation;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getDesiredPosition() { return desiredPosition; }
    public void setDesiredPosition(String desiredPosition) { this.desiredPosition = desiredPosition; }

    public String getDesiredWorkLocation() { return desiredWorkLocation; }
    public void setDesiredWorkLocation(String desiredWorkLocation) { this.desiredWorkLocation = desiredWorkLocation; }
}
