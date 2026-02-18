package com.mordiniaa.backend.services.user;

import com.mordiniaa.backend.config.StorageProperties;
import com.mordiniaa.backend.models.file.imageStorage.ImageMetadata;
import com.mordiniaa.backend.models.user.DbUser;
import com.mordiniaa.backend.models.user.mysql.User;
import com.mordiniaa.backend.repositories.mongo.ImageMetadataRepository;
import com.mordiniaa.backend.repositories.mongo.user.UserRepresentationRepository;
import com.mordiniaa.backend.repositories.mysql.UserRepository;
import com.mordiniaa.backend.services.storage.StorageProvider;
import com.mordiniaa.backend.services.storage.profileImagesStorage.ImagesStorageService;
import com.mordiniaa.backend.utils.CloudStorageServiceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRepresentationRepository userRepresentationRepository;
    private final MongoUserService mongoUserService;
    private final StorageProperties storageProperties;
    private final CloudStorageServiceUtils cloudStorageServiceUtils;
    private final StorageProvider storageProvider;
    private final ImageMetadataRepository imageMetadataRepository;
    private final MongoTemplate mongoTemplate;
    private final ImagesStorageService imagesStorageService;

    public User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section
    }

    public void addProfileImage(UUID userId, MultipartFile file) {

        mongoUserService.checkUserAvailability(userId);
        DbUser user = userRepresentationRepository.findByUserId(userId)
                .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section

        imagesStorageService.addProfileImage(user, file);
    }

    public void setDefaultProfileImage() {

    }
}
