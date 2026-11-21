package com.minhduc.careerconnect.exception;

/**
 * Không tìm thấy dữ liệu được yêu cầu (ví dụ: hồ sơ id=999 không tồn tại).
 * Được xử lý thành trang 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    /** Ví dụ: new ResourceNotFoundException("Hồ sơ", 5) -> "Không tìm thấy hồ sơ với mã 5" */
    public ResourceNotFoundException(String resourceName, Object id) {
        super("Không tìm thấy " + resourceName.toLowerCase() + " với mã " + id);
    }
}
