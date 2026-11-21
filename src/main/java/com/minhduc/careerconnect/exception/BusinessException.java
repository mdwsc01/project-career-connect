package com.minhduc.careerconnect.exception;

/**
 * Lỗi nghiệp vụ: thao tác không hợp lệ theo quy tắc của hệ thống
 * (ví dụ: gửi duyệt khi hồ sơ chưa đủ thông tin, email đã tồn tại).
 * Thông điệp sẽ được hiện cho người dùng, nên viết bằng tiếng Việt dễ hiểu.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
