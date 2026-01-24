package com.mordiniaa.backend.services.notes.user;

import com.mordiniaa.backend.repositories.mongo.user.UserRepresentationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private UserRepresentationRepository userRepresentationRepository;

    public void addProfileImage() {

    }

    public void setDefaultProfileImage() {

    }
}
