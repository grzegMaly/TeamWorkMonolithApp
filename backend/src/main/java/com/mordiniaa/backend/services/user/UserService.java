package com.mordiniaa.backend.services.user;

import com.mordiniaa.backend.models.user.mysql.User;
import com.mordiniaa.backend.repositories.mongo.user.UserRepresentationRepository;
import com.mordiniaa.backend.repositories.mysql.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private UserRepresentationRepository userRepresentationRepository;

    public User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section
    }

    public void addProfileImage(UUID userId, MultipartFile file) {

    }

    public void setDefaultProfileImage() {

    }
}
