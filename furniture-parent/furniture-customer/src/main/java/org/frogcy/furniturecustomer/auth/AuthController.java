package org.frogcy.furniturecustomer.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecommon.entity.Role;
import org.frogcy.furniturecustomer.auth.dto.*;
import org.frogcy.furniturecustomer.customer.CustomerService;
import org.frogcy.furniturecustomer.email.EmailService;
import org.frogcy.furniturecustomer.otp.OtpService;
import org.frogcy.furniturecustomer.security.CustomUserDetails;
import org.frogcy.furniturecustomer.security.jwt.JwtValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/oauth")
@Validated
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private CustomerService service;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private OtpService otpService;

    @PostMapping("/token/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenDTO token, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing refresh token");
        }

        try {
            AuthToken responseToken = tokenService.refreshTokens(new RefreshTokenRequest(token.getEmail(), refreshToken));

            // Có thể set lại cookie mới (gia hạn refresh token)
            Cookie newRefreshCookie = new Cookie("refreshToken", responseToken.getRefreshToken());
            newRefreshCookie.setHttpOnly(true);
//            newRefreshCookie.setSecure(true); // bật nếu dùng HTTPS
            newRefreshCookie.setPath("/api/oauth/token/refresh");
            newRefreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày
            response.addCookie(newRefreshCookie);

            Map<String, Object> result = new HashMap<>();
            result.put("accessToken", responseToken.getAccessToken());
            return ResponseEntity.ok(result);
        } catch (RefreshTokenExpiredException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest request, HttpServletResponse httpServletResponse){
        String email = request.getEmail();
        String password = request.getPassword();

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            Customer user = service.getByEmail(email);

            AuthCustomerDTO loginDTO = getCustomerDTO(user);

            AuthToken token = tokenService.generateToken(userDetails.getCustomer());

            AuthResponse response = new AuthResponse();
            response.setAccessToken(token.getAccessToken());
            response.setCustomer(loginDTO);

            Cookie refreshTokenCookie = new Cookie("refreshToken", token.getRefreshToken());
            refreshTokenCookie.setHttpOnly(true);
//            refreshTokenCookie.setSecure(true); only HTTPS
            refreshTokenCookie.setPath("/api/oauth/token/refresh");
            refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngay
            httpServletResponse.addCookie(refreshTokenCookie);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private static AuthCustomerDTO getCustomerDTO(Customer user) {
        AuthCustomerDTO loginDTO = new AuthCustomerDTO();
        loginDTO.setId(user.getId());
        loginDTO.setEmail(user.getEmail());
        loginDTO.setFirstName(user.getFirstName());
        loginDTO.setLastName(user.getLastName());
        loginDTO.setAvatarUrl(user.getAvatarUrl());

        Set<RoleDTO> rolesDTO = new HashSet<>();
        for (Role role : user.getRoles()) {
            RoleDTO roleDTO = new RoleDTO();
            roleDTO.setName(role.getName());
            roleDTO.setDescription(role.getDescription());
            rolesDTO.add(roleDTO);
        }
        loginDTO.setRoles(rolesDTO);
        return loginDTO;
    }


    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@RequestBody @Valid CustomerRegisterDTO dto){

        customerService.registerUser(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Đăng ký thành công. Vui lòng kiểm tra email để xác thực.",
                        "email", dto.getEmail()
                ));
    }



//    Verify with link
//    @GetMapping("/verify")
//    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
//        try {
//            emailService.verifyEmail(token);
//            return ResponseEntity.ok("Xác thực tài khoản thành công!");
//        } catch (JwtValidationException | IllegalArgumentException | IllegalStateException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }


    @PostMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestBody @Valid OtpVerifyRequestDTO dto) {
        String message = emailService.verifyEmailByOtp(dto);
        Map<String, String> result = new HashMap<>();
        result.put("message", message);

        return ResponseEntity.ok(result);
    }

}
