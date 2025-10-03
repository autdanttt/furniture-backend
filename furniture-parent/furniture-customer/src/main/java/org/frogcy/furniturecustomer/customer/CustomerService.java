package org.frogcy.furniturecustomer.customer;

import jakarta.validation.Valid;
import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecustomer.auth.dto.CustomerRegisterDTO;
import org.frogcy.furniturecustomer.auth.dto.OtpVerifyRequestDTO;
import org.frogcy.furniturecustomer.auth.dto.ResetPasswordByOtpRequest;

public interface CustomerService {
    Customer getByEmail(String email);

    void registerUser(@Valid CustomerRegisterDTO dto);

    void forgotPassword(String email);

    String resetPasswordByOtp(ResetPasswordByOtpRequest dto);

    String verifyEmailByOtp(@Valid OtpVerifyRequestDTO dto);
}
