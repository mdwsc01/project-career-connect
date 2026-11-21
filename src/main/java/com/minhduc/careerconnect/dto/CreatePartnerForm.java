package com.minhduc.careerconnect.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.minhduc.careerconnect.validation.PasswordsMatch;
import com.minhduc.careerconnect.validation.UniqueEmail;

/**
 * Form admin tạo tài khoản đối tác kèm công ty (task T4E7967).
 */
@PasswordsMatch
public class CreatePartnerForm {

    @NotBlank(message = "Vui lòng nhập họ tên người phụ trách")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String fullName;

    @NotBlank(message = "Vui lòng nhập email")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 255, message = "Email tối đa 255 ký tự")
    @UniqueEmail
    private String email;

    @NotBlank(message = "Vui lòng nhập mật khẩu ban đầu")
    @Size(min = 8, max = 72, message = "Mật khẩu phải từ 8 đến 72 ký tự")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "Mật khẩu phải có cả chữ và số")
    private String password;

    @NotBlank(message = "Vui lòng nhập lại mật khẩu")
    private String confirmPassword;

    @NotBlank(message = "Vui lòng nhập tên công ty")
    @Size(max = 200, message = "Tên công ty tối đa 200 ký tự")
    private String companyName;

    /**
     * Không bắt buộc; nếu nhập thì là 10 hoặc 13 chữ số (có thể có dấu gạch giữa).
     */
    @Pattern(regexp = "^$|^\\d{10}(-\\d{3})?$", message = "Mã số thuế gồm 10 chữ số hoặc dạng 0123456789-001")
    private String taxCode;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }
}
