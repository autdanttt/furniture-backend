package org.frogcy.furniturecustomer.auth;


import jakarta.transaction.Transactional;
import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecommon.entity.CustomerRefreshToken;
import org.frogcy.furniturecustomer.auth.dto.AuthToken;
import org.frogcy.furniturecustomer.security.jwt.JwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TokenService {
    private final Logger log = LoggerFactory.getLogger(TokenService.class);
    @Value("${app.security.jwt.refresh-token.expiration}")
    private int refreshTokenExpiration;

    @Autowired
    RefreshTokenRepository refreshTokenRepo;

    @Autowired
    JwtUtility jwtUtil;

    @Autowired
    PasswordEncoder passwordEncoder;

    public AuthToken generateToken(Customer customer){
        String accessToken = jwtUtil.generateAccessToken(customer);

        String randomUUID = UUID.randomUUID().toString();

        CustomerRefreshToken customerRefreshToken = new CustomerRefreshToken();
        customerRefreshToken.setCustomer(customer);
        customerRefreshToken.setToken(passwordEncoder.encode(randomUUID));

        long refreshTokenExpirationInMillis = System.currentTimeMillis() + refreshTokenExpiration * 60000;
        customerRefreshToken.setExpiryTime(new Date(refreshTokenExpirationInMillis));

        refreshTokenRepo.save(customerRefreshToken);

        return new AuthToken(accessToken, randomUUID);
    }


    public AuthToken refreshTokens(RefreshTokenRequest request) throws RefreshTokenExpiredException {

        String rawRefreshToken = request.getRefreshToken();
        log.info("Refresh token: {}", rawRefreshToken);

        List<CustomerRefreshToken> listCustomerRefreshToken = refreshTokenRepo.findByEmail(request.getEmail());
        log.info("Refresh token found: {}", listCustomerRefreshToken);

        CustomerRefreshToken foundCustomerRefreshToken = null;

        for (CustomerRefreshToken token : listCustomerRefreshToken){
            if(passwordEncoder.matches(rawRefreshToken, token.getToken())){
                log.info("Matched refresh token: " + token.getToken());

                foundCustomerRefreshToken = token;
            }
        }
        if (foundCustomerRefreshToken == null){
            throw new RefreshTokenNotFoundException("Refresh token not found");
        }

        Date now = new Date();


        if(foundCustomerRefreshToken.getExpiryTime().before(now)){
            throw new RefreshTokenExpiredException();
        }

        AuthToken response = generateToken(foundCustomerRefreshToken.getCustomer());
        refreshTokenRepo.delete(foundCustomerRefreshToken);
        return response;
    }
}
