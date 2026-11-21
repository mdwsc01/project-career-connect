package com.minhduc.careerconnect.dto;

import java.util.List;

/**
 * Một trang kết quả kèm thông tin phân trang.
 */
public record PageResult<T>(List<T> items, int page, int pageSize, long totalItems) {

    public int totalPages() {
        return (int) Math.max(1, (totalItems + pageSize - 1) / pageSize);
    }

    public boolean hasPrevious() {
        return page > 1;
    }

    public boolean hasNext() {
        return page < totalPages();
    }
}
