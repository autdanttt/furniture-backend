package org.frogcy.furniturecustomer.customer.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.frogcy.furniturecommon.entity.Gender;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUpdateDTO {
    @NotNull
    private Integer id;
    @NotNull
    @Length(min = 1, max = 30)
    private String firstName;
    @NotNull
    @Length(min = 1, max = 30)
    private String lastName;
    @NotNull
    private Gender gender;
    @NotNull
    @Pattern(
            regexp = "^(0|\\+84|84)(3[2-9]|5[2689]|7[06-9]|8[1-9]|9[0-9])[0-9]{7}$",
            message = "Số điện thoại không hợp lệ"
    )
    private String phoneNumber;
}
