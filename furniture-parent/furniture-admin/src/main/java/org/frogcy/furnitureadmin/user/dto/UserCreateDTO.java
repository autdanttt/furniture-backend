package org.frogcy.furnitureadmin.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateDTO {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private Set<Integer> roleIds;
}
