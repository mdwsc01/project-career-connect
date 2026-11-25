package com.minhduc.careerconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Form thêm/sửa dự án cá nhân (task T4E796C).
 */
public class ProjectForm {

    @NotBlank(message = "Vui lòng nhập tên dự án")
    @Size(max = 200, message = "Tên dự án tối đa 200 ký tự")
    private String name;

    @NotBlank(message = "Vui lòng nhập vai trò của bạn trong dự án")
    @Size(max = 100, message = "Vai trò tối đa 100 ký tự")
    private String role;

    @NotBlank(message = "Vui lòng nhập công nghệ sử dụng")
    @Size(max = 255, message = "Công nghệ tối đa 255 ký tự")
    private String techStack;

    /**
     * Bắt buộc, phải là địa chỉ repo trên github.com.
     */
    @NotBlank(message = "Vui lòng nhập link GitHub")
    @Size(max = 500, message = "Link tối đa 500 ký tự")
    @Pattern(regexp = "^https://(www\\.)?github\\.com/[A-Za-z0-9_.-]+(/[A-Za-z0-9_.-]+)*/?$",
            message = "Link GitHub không hợp lệ (VD: https://github.com/ten-ban/ten-du-an)")
    private String githubUrl;

    /**
     * Không bắt buộc; nếu nhập phải là URL http/https hợp lệ.
     */
    @Size(max = 500, message = "Link tối đa 500 ký tự")
    @Pattern(regexp = "^$|^https?://[A-Za-z0-9.-]+\\.[A-Za-z]{2,}(:\\d+)?(/\\S*)?$",
            message = "Link demo không hợp lệ (VD: https://du-an-cua-ban.vercel.app)")
    private String demoUrl;

    @Size(max = 2000, message = "Mô tả tối đa 2000 ký tự")
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTechStack() {
        return techStack;
    }

    public void setTechStack(String techStack) {
        this.techStack = techStack;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getDemoUrl() {
        return demoUrl;
    }

    public void setDemoUrl(String demoUrl) {
        this.demoUrl = demoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
