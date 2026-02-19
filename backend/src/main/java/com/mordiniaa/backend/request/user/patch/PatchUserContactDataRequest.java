package com.mordiniaa.backend.request.user.patch;

import com.mordiniaa.backend.request.user.ContactDataRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatchUserContactDataRequest implements ContactDataRequest {

    @Pattern(regexp = "\\d{1,3}")
    private String countryCallingCode;

    @Pattern(regexp = "\\d{9,10}")
    private String phoneNumber;

    @Email
    @Size(max = 254)
    private String email;
}
