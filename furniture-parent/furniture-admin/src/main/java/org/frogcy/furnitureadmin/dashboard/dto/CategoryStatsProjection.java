package org.frogcy.furnitureadmin.dashboard.dto;

// Projection để hứng kết quả từ repository
public interface CategoryStatsProjection {
    String getCategoryName();
    Long getOrderCount();
}