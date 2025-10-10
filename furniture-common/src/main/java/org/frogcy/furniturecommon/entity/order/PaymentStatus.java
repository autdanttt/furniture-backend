package org.frogcy.furniturecommon.entity.order;

public enum PaymentStatus {
    PENDING,    // chưa thanh toán
    PAID,       // đã thanh toán
    REFUNDING,  // đang hoàn tiền
    REFUNDED,   // hoàn tiền xong
    FAILED,
    COMPLETED,
}
