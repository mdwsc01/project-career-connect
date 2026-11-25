package com.minhduc.careerconnect.exception;

/**
 * File tải lên không hợp lệ (sai loại, quá lớn, rỗng...). Thông điệp hiện cho người dùng.
 */
public class InvalidFileException extends RuntimeException {

    public InvalidFileException(String message) {
        super(message);
    }
}
