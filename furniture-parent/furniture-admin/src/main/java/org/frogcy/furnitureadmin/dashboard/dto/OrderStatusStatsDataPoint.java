package org.frogcy.furnitureadmin.dashboard.dto;

public record OrderStatusStatsDataPoint(
        String status, // Tên của trạng thái, ví dụ "DELIVERED"
        Long orderCount  // Số lượng đơn hàng của trạng thái đó
) {}