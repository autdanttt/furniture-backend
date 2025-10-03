package org.frogcy.furniturecustomer.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String accessToken;
    private AuthCustomerDTO customer;
}