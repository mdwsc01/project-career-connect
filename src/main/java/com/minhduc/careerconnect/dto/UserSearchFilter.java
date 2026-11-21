package com.minhduc.careerconnect.dto;

/**
 * Tham số tìm kiếm / lọc / phân trang ở trang quản trị tài khoản (task T4E7967).
 */
public class UserSearchFilter {

    /**
     * Từ khoá tìm theo họ tên hoặc email.
     */
    private String q;
    /**
     * STUDENT, PARTNER, ADMIN hoặc rỗng = tất cả.
     */
    private String role;
    /**
     * ACTIVE, LOCKED hoặc rỗng = tất cả.
     */
    private String status;
    /**
     * Trang hiện tại, bắt đầu từ 1.
     */
    private int page = 1;

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = Math.max(page, 1);
    }
}
