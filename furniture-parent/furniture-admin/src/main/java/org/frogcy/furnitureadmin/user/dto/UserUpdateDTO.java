package org.frogcy.furnitureadmin.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDTO {
    @NotNull
    private Integer id;
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
