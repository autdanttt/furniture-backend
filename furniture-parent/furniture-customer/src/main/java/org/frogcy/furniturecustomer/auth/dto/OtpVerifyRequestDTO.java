package org.frogcy.furniturecustomer.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerifyRequestDTO {
    @NotNull
    @Email
    private String email;
    @NotNull
    @Length(min= 6, max = 6, message = "Otp must be 6 characters")
    private String otp;
}
