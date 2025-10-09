package org.frogcy.furnitureadmin.shippingfee;

public class ProvinceNotFoundException extends RuntimeException {
    public ProvinceNotFoundException(String provinceNotFound) {
        super(provinceNotFound);
    }
}
