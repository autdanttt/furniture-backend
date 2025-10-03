package org.frogcy.furniturecustomer.email;

import io.jsonwebtoken.Claims;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecommon.entity.Otp;
import org.frogcy.furniturecommon.entity.OtpType;
import org.frogcy.furniturecustomer.auth.dto.OtpVerifyRequestDTO;
import org.frogcy.furniturecustomer.customer.CustomerRepository;
import org.frogcy.furniturecustomer.customer.dto.CustomerNotFoundException;
import org.frogcy.furniturecustomer.otp.OtpService;
import org.frogcy.furniturecustomer.security.jwt.JwtUtility;
import org.frogcy.furniturecustomer.security.jwt.JwtValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

@Service
public class EmailService {

    @Autowired
    private JwtUtility jwtUtil;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;
    @Autowired
    private OtpService otpService;


    public void sendVerificationEmail(String to, String token) {
        String subject = "Xác thực tài khoản";
        String link = "http://localhost/api/oauth/verify?token=" + token;
        String html = "<p>Nhấn vào <a href=\"" + link + "\">đây</a> để xác thực tài khoản.</p>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "Hoang Ha website");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Gửi email thất bại: " + e.getMessage(), e);
        }
    }

    public void verifyEmail(String token) throws JwtValidationException {

        Claims claims = jwtUtil.validateToken(token);

        // Kiểm tra type trong claims
        String type = (String) claims.get("type");
        if (!"email_verification".equals(type)) {
            throw new IllegalArgumentException("Token không hợp lệ cho xác thực email.");
        }

        String email = claims.getSubject();
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));

        if (customer.isVerified()) {
            throw new IllegalStateException("Tài khoản đã được xác thực trước đó.");
        }

        customer.setVerified(true);
        customerRepository.save(customer);
    }


    public void sendOtpVerifyEmail(String to, String otpCode) {
        String subject = "Mã xác thực email";
        String html = "<p>Xin chào,</p>" +
                "<p>Mã xác thực để xác thực email của bạn là:</p>" +
                "<h2>" + otpCode + "</h2>" +
                "<p>Mã này có hiệu lực trong vòng <strong>10 phút</strong>.</p>" +
                "<p>Nếu bạn đã xác thưc, vui lòng bỏ qua email này.</p>" +
                "<br><p>Trân trọng,<br>Hoang Ha website</p>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "Hoang Ha website");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Gửi email thất bại: " + e.getMessage(), e);
        }
    }

    public String verifyEmailByOtp(OtpVerifyRequestDTO dto) {
        Customer customer = customerRepository.findCustomerByEmailAndVerifiedIsFalse(dto.getEmail()).orElseThrow(
                () -> new CustomerNotFoundException("Customer not found with email " + dto.getEmail())
        );

        if(otpService.verifyOtp(customer, dto.getOtp(), OtpType.REGISTER)){
            return "Otp verification success";
        }
        return "Otp verification failed. Try again later";
    }
}
