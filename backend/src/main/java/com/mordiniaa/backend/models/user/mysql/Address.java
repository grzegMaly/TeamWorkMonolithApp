package com.mordiniaa.backend.models.user.mysql;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Address {

    private String street;
    private String city;
    private String country;
    private String zipCode;
    private String district;
}
