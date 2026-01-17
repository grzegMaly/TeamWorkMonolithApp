package com.mordiniaa.backend.request.user.patch;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatchUserDataRequest {

    private String firstname;
    private String lastname;
}
