package org.frogcy.furnitureadmin.dashboard.dto;

// DTO chứa dữ liệu thống kê cuối cùng cho mỗi category
public record CategoryStatsDataPoint(
        String categoryName,
        Long orderCount,
        Double percentage
) {}