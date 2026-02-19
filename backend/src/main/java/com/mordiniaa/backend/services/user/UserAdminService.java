package com.mordiniaa.backend.services.user;

import com.mordiniaa.backend.config.StorageProperties;
import com.mordiniaa.backend.dto.user.UserDto;
import com.mordiniaa.backend.events.user.events.UserCreatedEvent;
import com.mordiniaa.backend.events.user.events.UserUsernameChangedEvent;
import com.mordiniaa.backend.mappers.user.UserMapper;
import com.mordiniaa.backend.models.user.mysql.*;
import com.mordiniaa.backend.repositories.mysql.RoleRepository;
import com.mordiniaa.backend.repositories.mysql.UserRepository;
import com.mordiniaa.backend.request.user.CreateUserRequest;
import com.mordiniaa.backend.request.user.patch.PatchUserDataRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StorageProperties storageProperties;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final MongoUserService mongoUserService;
    private final UserService userService;

    @Transactional
    public UserDto createUser(CreateUserRequest createUserRequest) {

        String firstName = createUserRequest.getFirstname().trim();
        String lastName = createUserRequest.getLastname().trim();

        if (userRepository.existsUserByFirstNameAndLastName(firstName, lastName))
            throw new RuntimeException();

        String login = generateUniqueLogin(firstName, lastName);

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
        if (addr != null) {
            Address address = new Address();
            address.setCity(addr.getCity().trim());
            address.setDistrict(addr.getDistrict().trim());
            address.setCountry(addr.getCountry().trim());
            address.setStreet(addr.getStreet().trim());
            address.setZipCode(addr.getZipCode().trim());
            address.setUser(newUser);
            newUser.addAddress(address);
        }

        //Contact Data
        var contactData = createUserRequest.getContactData();
        if (contactData != null) {
            Contact contact = new Contact();
            contact.setEmail(contactData.getEmail().trim());
            contact.setCountryCallingCode(contactData.getCountryCallingCode().trim());
            contact.setPhoneNumber(contactData.getPhoneNumber().trim());
            contact.setUser(newUser);
            newUser.setContact(contact);
        }
        
        User savedUser = userRepository.save(newUser);
        applicationEventPublisher.publishEvent(
                new UserCreatedEvent(savedUser.getUserId())
        );

        return userMapper.toDto(savedUser);
    }

    @Transactional
    public void updateUserBasicData(UUID userId, PatchUserDataRequest request) {

        mongoUserService.checkUserAvailability(userId);

        String firstName = request.getFirstname();
        String lastName = request.getLastname();

        if (firstName == null && lastName == null)
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        User user = userService.getUser(userId);

        String newFirst = firstName != null ? firstName : user.getFirstName();
        String newLast  = lastName  != null ? lastName  : user.getLastName();

        String username = generateUniqueLogin(newFirst, newLast);

        if (firstName != null) user.setFirstName(firstName);
        if (lastName  != null) user.setLastName(lastName);

        user.setUsername(username);
        userRepository.save(user);

        applicationEventPublisher.publishEvent(
                new UserUsernameChangedEvent(userId, username)
        );
    }

    public void updateUserAddressData(UUID userId) {

        mongoUserService.checkUserAvailability(userId);
    }

    public void updateUserContactData(UUID userId) {

        mongoUserService.checkUserAvailability(userId);
    }

    public void deactivateUser(UUID userId) {

        mongoUserService.checkUserAvailability(userId);
    }

    private String generateUniqueLogin(String firstName, String lastName) {

        String raw = new Random().nextBoolean()
                ? firstName.substring(0, 3).concat(lastName.substring(0, 3))
                : lastName.substring(0, 3).concat(firstName.substring(0, 3));

        String fullLogin = raw + ThreadLocalRandom.current().nextInt(999);
        while (userRepository.existsByUsername(fullLogin)) {
            fullLogin = raw + ThreadLocalRandom.current().nextInt(999);
        }
        return fullLogin;
    }
}
