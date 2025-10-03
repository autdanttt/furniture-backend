package org.frogcy.furniturecustomer.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class CustomerRegisterDTO {
    @NotNull(message = "Email cannot be null")
    @Email
    private String email;
    @NotNull
    @Length(min = 1, max = 20, message =  "First name must be between 1-20")
    private String firstName;
    @NotNull
    @Length(min = 1, max = 20, message =  "Last name must be between 1-20")
    private String lastName;
    @NotNull(message = "Password cannot be null")
    @Length(min = 8, max = 64, message = "Password must be between 8 - 64")
    private String password;
}
