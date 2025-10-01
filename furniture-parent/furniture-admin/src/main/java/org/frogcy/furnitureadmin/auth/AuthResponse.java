package org.frogcy.furnitureadmin.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String accessToken;
    private AuthUserDTO user;
}
