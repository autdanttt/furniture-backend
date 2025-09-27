package org.frogcy.furnitureadmin.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserDTO {
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private Set<RoleDTO> roles = new HashSet<>();

}
