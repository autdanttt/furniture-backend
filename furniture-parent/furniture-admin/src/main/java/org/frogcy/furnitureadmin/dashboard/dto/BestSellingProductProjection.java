package org.frogcy.furnitureadmin.dashboard.dto;

public interface BestSellingProductProjection {
    Integer getProductId();
    String getProductName();
    String getMainImageUrl();
    Long getTotalQuantity();
}