package org.frogcy.furnitureadmin.dashboard.dto;

// Một interface để hứng kết quả trực tiếp từ query
public interface StatsProjection {
    String getLabel();
    Long getRevenue();
    Long getOrderCount();
}