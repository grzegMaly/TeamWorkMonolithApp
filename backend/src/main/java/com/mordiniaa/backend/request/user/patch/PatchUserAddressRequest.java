package com.mordiniaa.backend.request.user.patch;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatchUserAddressRequest {

    @Size(min = 5, max = 40)
    private String street;

    @Size(min = 2, max = 30)
    private String city;

    @Size(min = 2, max = 30)
    private String country;

    @Pattern(regexp = "\\d{2}-\\d{3}")
    private String zipCode;

    @Size(min = 2, max = 20)
    private String district;
}
