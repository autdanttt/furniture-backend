package org.frogcy.furnitureadmin.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.frogcy.furniturecommon.entity.Gender;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDTO {
    private Integer id;

    private String email;

    private String firstName;

    private String lastName;

    private String avatarUrl;

    private Gender gender;

    private String phoneNumber;

    private boolean verified;

    private boolean enabled;

}
