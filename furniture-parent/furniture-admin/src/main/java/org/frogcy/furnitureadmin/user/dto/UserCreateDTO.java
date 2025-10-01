package org.frogcy.furnitureadmin.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateDTO {
    @NotNull
    @Email
    private String email;
    @NotNull
    @Length(min = 5, max = 30)
    private String password;
    @NotNull
    @Length(min = 1, max = 30)
    private String firstName;
    @NotNull
    @Length(min = 1, max = 30)
    private String lastName;
    private boolean enabled;
    private Set<Integer> roleIds;
}
