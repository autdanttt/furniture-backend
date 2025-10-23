package org.frogcy.furnitureadmin.dashboard.dto;

public record BestSellingProductDataPoint(
        Integer productId,
        String productName,
        String mainImageUrl, // URL của ảnh chính
        Long totalQuantity // Tổng số lượng đã bán
) {}