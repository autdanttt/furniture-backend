package org.frogcy.furniturecustomer.otp;

import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecommon.entity.Otp;
import org.frogcy.furniturecommon.entity.OtpType;

public interface OtpService {
    Otp generateOtp(Customer customer, OtpType otpType);
    boolean verifyOtp(Customer customer, String code, OtpType otpType);
}
