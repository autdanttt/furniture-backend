package org.frogcy.furnitureadmin.auth;


import jakarta.transaction.Transactional;
import org.frogcy.furnitureadmin.security.jwt.JwtUtility;
import org.frogcy.furniturecommon.entity.User;
import org.frogcy.furniturecommon.entity.UserRefreshToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
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


    public AuthToken generateToken(User user){
        String accessToken = jwtUtil.generateAccessToken(user);

        String randomUUID = UUID.randomUUID().toString();

        UserRefreshToken userRefreshToken = new UserRefreshToken();
        userRefreshToken.setUser(user);
        userRefreshToken.setToken(passwordEncoder.encode(randomUUID));

        long refreshTokenExpirationInMillis = System.currentTimeMillis() + refreshTokenExpiration * 60000;
        userRefreshToken.setExpiryTime(new Date(refreshTokenExpirationInMillis));

        refreshTokenRepo.save(userRefreshToken);

        return new AuthToken(accessToken, randomUUID);
    }


    public AuthToken refreshTokens(RefreshTokenRequest request) throws RefreshTokenExpiredException {

        String rawRefreshToken = request.getRefreshToken();
        log.info("Refresh token: {}", rawRefreshToken);

        List<UserRefreshToken> listUserRefreshToken = refreshTokenRepo.findByEmail(request.getEmail());
        log.info("Refresh token found: {}", listUserRefreshToken);

        UserRefreshToken foundUserRefreshToken = null;

        for (UserRefreshToken token : listUserRefreshToken){
            if(passwordEncoder.matches(rawRefreshToken, token.getToken())){
                log.info("Matched refresh token: " + token.getToken());

                foundUserRefreshToken = token;
            }
        }
        if (foundUserRefreshToken == null){
            throw new RefreshTokenNotFoundException("Refresh token not found");
        }

        Date now = new Date();


        if(foundUserRefreshToken.getExpiryTime().before(now)){
            throw new RefreshTokenExpiredException();
        }

        AuthToken response = generateToken(foundUserRefreshToken.getUser());
        refreshTokenRepo.delete(foundUserRefreshToken);
        return response;
    }
}
