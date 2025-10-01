package org.frogcy.furnitureadmin.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.frogcy.furnitureadmin.security.CustomUserDetails;
import org.frogcy.furnitureadmin.user.UserService;
import org.frogcy.furniturecommon.entity.Role;
import org.frogcy.furniturecommon.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private UserService service;


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
            newRefreshCookie.setPath("/admin/api/oauth/token/refresh");
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

            User user = service.getByEmail(email);

            AuthUserDTO loginDTO = new AuthUserDTO();
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

            AuthToken token = tokenService.generateToken(userDetails.getUser());

            AuthResponse response = new AuthResponse();
            response.setAccessToken(token.getAccessToken());
            response.setUser(loginDTO);

            Cookie refreshTokenCookie = new Cookie("refreshToken", token.getRefreshToken());
            refreshTokenCookie.setHttpOnly(true);
//            refreshTokenCookie.setSecure(true); only HTTPS
            refreshTokenCookie.setPath("/admin/api/oauth/token/refresh");
            refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngay
            httpServletResponse.addCookie(refreshTokenCookie);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
