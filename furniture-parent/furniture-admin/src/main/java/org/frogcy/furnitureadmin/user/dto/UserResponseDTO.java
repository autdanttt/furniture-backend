package org.frogcy.furnitureadmin.user.dto;

import lombok.*;
import org.frogcy.furniturecommon.entity.Role;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserResponseDTO {
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private boolean enabled;
    private Set<Role> roles;
}
