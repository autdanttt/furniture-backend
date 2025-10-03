package org.frogcy.furniturecustomer.customer;

import jakarta.validation.Valid;
import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecustomer.auth.dto.CustomerRegisterDTO;
import org.frogcy.furniturecustomer.auth.dto.OtpVerifyRequestDTO;
import org.frogcy.furniturecustomer.auth.dto.ResetPasswordByOtpRequest;
import org.frogcy.furniturecustomer.customer.dto.CustomerResponseDTO;
import org.frogcy.furniturecustomer.customer.dto.CustomerUpdateDTO;
import org.springframework.web.multipart.MultipartFile;

public interface CustomerService {
    Customer getByEmail(String email);

    void registerUser(@Valid CustomerRegisterDTO dto);

    void forgotPassword(String email);

    String resetPasswordByOtp(ResetPasswordByOtpRequest dto);

    String verifyEmailByOtp(@Valid OtpVerifyRequestDTO dto);

    CustomerResponseDTO updateInformation(@Valid CustomerUpdateDTO dto, MultipartFile avatar);
}
