package org.frogcy.furniturecustomer.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordByOtpRequest {
    @NotNull
    private String email;
    @NotNull
    @Length(min = 6, max = 6, message = "Otp must be 6 characters")
    private String otp;
    @NotNull
    private String newPassword;
}
