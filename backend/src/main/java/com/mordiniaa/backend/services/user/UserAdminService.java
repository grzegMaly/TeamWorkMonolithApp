package com.mordiniaa.backend.services.user;

import com.mordiniaa.backend.repositories.mongo.user.UserRepresentationRepository;
import com.mordiniaa.backend.request.user.CreateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private UserRepresentationRepository userRepresentationRepository;

    public void createUser(CreateUserRequest createUserRequest) {

    }

    public void updateUserData() {

    }

    public void updateUserAddress() {

    }

    public void deactivateUser() {

    }
}
