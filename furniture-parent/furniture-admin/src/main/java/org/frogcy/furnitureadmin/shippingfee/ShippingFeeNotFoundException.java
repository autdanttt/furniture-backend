package org.frogcy.furnitureadmin.shippingfee;

public class ShippingFeeNotFoundException extends RuntimeException {
    public ShippingFeeNotFoundException(String shippingFeeNotFound) {
        super(shippingFeeNotFound);
    }
}
