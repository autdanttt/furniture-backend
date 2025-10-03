package org.frogcy.furniturecustomer.auth;

import io.jsonwebtoken.Claims;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecustomer.customer.CustomerRepository;
import org.frogcy.furniturecustomer.security.jwt.JwtUtility;
import org.frogcy.furniturecustomer.security.jwt.JwtValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

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


    public void sendVerificationEmail(String to, String token) {
        String subject = "Xác thực tài khoản";
        String link = "http://localhost/api/oauth/verify?token=" + token;
        String html = "<p>Nhấn vào <a href=\"" + link + "\">đây</a> để xác thực tài khoản.</p>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "Chat App");
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
}
