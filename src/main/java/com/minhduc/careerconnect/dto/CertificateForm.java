package com.minhduc.careerconnect.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * Thông tin chứng chỉ; file minh chứng được gửi riêng (MultipartFile) - task T4E796D.
 */
public class CertificateForm {

    @NotBlank(message = "Vui lòng nhập tên chứng chỉ")
    @Size(max = 200, message = "Tên chứng chỉ tối đa 200 ký tự")
    private String name;

    @NotBlank(message = "Vui lòng nhập đơn vị cấp")
    @Size(max = 150, message = "Đơn vị cấp tối đa 150 ký tự")
    private String issuer;

    @NotNull(message = "Vui lòng chọn ngày cấp")
    @PastOrPresent(message = "Ngày cấp không được ở tương lai")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate issuedDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDate issuedDate) {
        this.issuedDate = issuedDate;
    }
}
