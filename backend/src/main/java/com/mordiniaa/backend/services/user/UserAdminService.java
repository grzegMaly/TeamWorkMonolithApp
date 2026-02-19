package com.mordiniaa.backend.services.user;

import com.mordiniaa.backend.config.StorageProperties;
import com.mordiniaa.backend.dto.user.UserDto;
import com.mordiniaa.backend.events.user.events.UserCreatedEvent;
import com.mordiniaa.backend.mappers.user.UserMapper;
import com.mordiniaa.backend.models.user.mysql.*;
import com.mordiniaa.backend.repositories.mongo.user.UserRepresentationRepository;
import com.mordiniaa.backend.repositories.mysql.RoleRepository;
import com.mordiniaa.backend.repositories.mysql.UserRepository;
import com.mordiniaa.backend.request.user.CreateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StorageProperties storageProperties;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public UserDto createUser(CreateUserRequest createUserRequest) {

        String firstName = createUserRequest.getFirstname().trim();
        String lastName = createUserRequest.getLastname().trim();

        if (userRepository.existsUserByFirstNameAndLastName(firstName, lastName))
            throw new RuntimeException();

        String raw = new Random().nextBoolean()
                ? firstName.substring(0, 3).concat(lastName.substring(0, 3))
                : lastName.substring(0, 3).concat(firstName.substring(0, 3));

        String login = generateUniqueLogin(raw);

        Role userRole = roleRepository.findRoleByAppRole(AppRole.ROLE_USER)
                .orElseThrow(RuntimeException::new); //TODO: Change In Exceptions Section

        User newUser = new User();
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setRole(userRole);
        newUser.setUsername(login);
        newUser.setImageKey(storageProperties.getProfileImages().getDefaultImageKey());

        //Address
        var addr = createUserRequest.getAddress();
        Address address = new Address();
        address.setCity(addr.getCity().trim());
        address.setDistrict(addr.getDistrict().trim());
        address.setCountry(addr.getCountry().trim());
        address.setStreet(addr.getStreet().trim());
        address.setZipCode(addr.getZipCode().trim());
        address.setUser(newUser);

        //Contact Data
        var contactData = createUserRequest.getContactData();
        Contact contact = new Contact();
        contact.setEmail(contactData.getEmail().trim());
        contact.setCountryCallingCode(contactData.getCountryCallingCode().trim());
        contact.setPhoneNumber(contactData.getPhoneNumber().trim());
        contact.setUser(newUser);

        newUser.addAddress(address);
        newUser.setContact(contact);

        User savedUser = userRepository.save(newUser);
        applicationEventPublisher.publishEvent(
                new UserCreatedEvent(savedUser.getUserId())
        );

        return userMapper.toDto(savedUser);
    }

    private String generateUniqueLogin(String rawLogin) {

        String fullLogin = rawLogin + ThreadLocalRandom.current().nextInt(999);
        while (userRepository.existsByUsername(fullLogin)) {
            fullLogin = rawLogin + ThreadLocalRandom.current().nextInt(999);
        }
        return fullLogin;
    }

    public void updateUserData() {

    }

    public void updateUserAddress() {

    }

    public void deactivateUser() {

    }
}
