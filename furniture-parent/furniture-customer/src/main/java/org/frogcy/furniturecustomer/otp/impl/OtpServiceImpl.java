package org.frogcy.furniturecustomer.otp.impl;

import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecommon.entity.Otp;
import org.frogcy.furniturecommon.entity.OtpType;
import org.frogcy.furniturecustomer.customer.CustomerRepository;
import org.frogcy.furniturecustomer.otp.OtpRepository;
import org.frogcy.furniturecustomer.otp.OtpService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpServiceImpl implements OtpService {
    private final OtpRepository otpRepository;
    private final CustomerRepository customerRepository;

    public OtpServiceImpl(OtpRepository otpRepository, CustomerRepository customerRepository) {
        this.otpRepository = otpRepository;
        this.customerRepository = customerRepository;
    }
    @Override
    public Otp generateOtp(Customer customer, OtpType otpType) {
        String code = String.format("%06d", new Random().nextInt(999999)); // OTP 6 số

        Otp otp = new Otp();
        otp.setCustomer(customer);
        otp.setCode(code);
        otp.setType(otpType);
        otp.setExpiresAt(new Date(System.currentTimeMillis() + (10 * 60000))); // Hết hạn sau 5 phút
        otp.setCreatedAt(new Date());
        return otpRepository.save(otp);
    }

    @Override
    public boolean verifyOtp(Customer customer, String code, OtpType otpType) {
        Optional<Otp> otpOpt = otpRepository.findTopByCustomerIdAndTypeAndUsedIsFalseOrderByCreatedAtDesc(customer.getId(), otpType);

        if (otpOpt.isEmpty()) {
            return false;
        }

        Otp otp = otpOpt.get();

        if (otp.isUsed() || otp.getExpiresAt().before(new Date())) {
            return false;
        }

        if (!otp.getCode().equals(code)) {
            return false;
        }

        otp.setUsed(true);
        otpRepository.save(otp);

        // Update user verified nếu type là register
        if (OtpType.REGISTER.equals(otpType)) {
            Customer registeredCustomer = otp.getCustomer();
            registeredCustomer.setVerified(true);
            customerRepository.save(registeredCustomer);
        }

        return true;
    }
}
