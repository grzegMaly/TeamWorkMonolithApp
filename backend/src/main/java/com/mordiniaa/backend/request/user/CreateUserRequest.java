package com.mordiniaa.backend.request.user;

import com.mordiniaa.backend.models.user.mysql.AppRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateUserRequest {

    @NotBlank
    @Size(min = 3, max = 25)
    private String firstname;

    @NotBlank
    @Size(min = 3, max = 25)
    private String lastname;

    @Size(min = 6, max = 20)
    private String username;

    private AppRole role;

    @Valid
    @NotNull
    private ContactData contactData;

    @Valid
    @NotNull
    private Address address;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Address implements AddressRequest {

        @NotBlank
        @Size(min = 5, max = 40)
        private String street;

        @NotBlank
        @Size(min = 2, max = 30)
        private String city;

        @NotBlank
        @Size(min = 2, max = 30)
        private String country;

        @NotBlank
        @Pattern(regexp = "\\d{2}-\\d{3}")
        private String zipCode;

        @NotBlank
        @Size(min = 2, max = 20)
        private String district;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ContactData implements ContactDataRequest {

        @NotBlank
        @Pattern(regexp = "\\d{1,3}")
        private String countryCallingCode;

        @NotBlank
        @Pattern(regexp = "\\d{9,10}")
        private String phoneNumber;

        @NotBlank
        @Email
        @Size(max = 254)
        private String email;
    }
}
