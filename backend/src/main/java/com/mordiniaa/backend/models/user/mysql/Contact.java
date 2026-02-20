package com.mordiniaa.backend.models.user.mysql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "contacts")
@Entity(name = "Contact")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "country_calling_code", nullable = false, length = 3)
    private String countryCallingCode;

    @Column(name = "phone_number", nullable = false, length = 14)
    private String phoneNumber;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;
}
