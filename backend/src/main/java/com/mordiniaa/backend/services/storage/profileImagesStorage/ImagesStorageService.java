package com.mordiniaa.backend.services.storage.profileImagesStorage;

import com.mordiniaa.backend.config.StorageProperties;
import com.mordiniaa.backend.models.file.imageStorage.ImageMetadata;
import com.mordiniaa.backend.models.user.DbUser;
import com.mordiniaa.backend.models.user.mongodb.UserRepresentation;
import com.mordiniaa.backend.repositories.mongo.ImageMetadataRepository;
import com.mordiniaa.backend.repositories.mysql.UserRepository;
import com.mordiniaa.backend.services.storage.StorageProvider;
import com.mordiniaa.backend.utils.CloudStorageServiceUtils;
import com.mordiniaa.backend.utils.MongoIdUtils;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImagesStorageService {

    @Value("${storage.profileImages.defaultImageKey}")
    private String defaultImageKey;

    @Value("${storage.profileImages.defaultImagePath}")
    private String defaultImagePath;

    private final MongoIdUtils mongoIdUtils;
    private final ImageMetadataRepository imageMetadataRepository;
    private final StorageProvider storageProvider;
    private final StorageProperties storageProperties;
    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;
    private final CloudStorageServiceUtils cloudStorageServiceUtils;

    public ResponseEntity<StreamingResponseBody> getProfileImage(String key) {

        if (key == null || key.equals(defaultImageKey))
            return defaultImage();

        ObjectId objectId = mongoIdUtils.getObjectId(key);

        ImageMetadata meta = imageMetadataRepository.findById(objectId)
                .orElse(null);

        if (meta == null)
            return defaultImage();

        StreamingResponseBody body = outputStream -> {
            try (InputStream in = storageProvider.downloadFile(
                    storageProperties.getProfileImages().getPath(),
                    meta.getStoredName()
            )) {
                in.transferTo(outputStream);
            }
        };

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(meta.getMimeType()))
                .body(body);
    }

    private ResponseEntity<StreamingResponseBody> defaultImage() {

        ClassPathResource resource = new ClassPathResource(defaultImagePath);

        if (resource.exists())
            throw new RuntimeException("Default avatar not found in resources"); // TODO: Change In Exceptions Section

        StreamingResponseBody body = os -> {
            try (InputStream in = resource.getInputStream()) {
                in.transferTo(os);
            }
        };

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(body);
    }

    public void addProfileImage(DbUser user, MultipartFile file) {

        StorageProperties.ProfileImages profileImages = storageProperties.getProfileImages();
        String mimetype = baseImageValidation(file, profileImages.getMimeTypes());

        ImageMetadata metadata = imageMetadataRepository.findImageMetadataByOwnerId(user.getUserId())
                .orElse(null);
        if (metadata != null)
            imageMetadataRepository.deleteById(metadata.getId());

        String originalName = file.getOriginalFilename();
        String ext = getFileExtension(mimetype);
        String storedName = cloudStorageServiceUtils.buildStorageKey().concat(ext.isEmpty() ? "" : ".".concat(ext));
        String imageKey = user.getImageKey();
        String profileImagesPath = profileImages.getPath();

        try {
            addImage(profileImagesPath, storedName, ext, profileImages.getProfileWidth(), profileImages.getProfileHeight(), file);
            if (metadata != null && imageKey != null && !imageKey.equals("defaultProfileImage")) {
                storageProvider.delete(
                        profileImagesPath,
                        metadata.getStoredName()
                );
            }
        } catch (Exception e) {
            if (metadata != null) {
                imageMetadataRepository.save(metadata);
            }
            throw new RuntimeException(e);
        }

        ImageMetadata savedMeta = imageMetadataRepository.save(ImageMetadata.builder()
                .originalName(originalName)
                .storedName(storedName)
                .ownerId(user.getUserId())
                .mimeType(file.getContentType())
                .size(file.getSize())
                .build()
        );

        updateUserImageKey(user.getUserId(), savedMeta.getId().toHexString());
    }

    public void addImage(String profileImagesPath, String storedName, String ext, int width, int height, MultipartFile file) {

        boolean uploaded = false;
        try (InputStream in = file.getInputStream()) {
            storageProvider.uploadImage(
                    profileImagesPath,
                    storedName,
                    ext,
                    width,
                    height,
                    in
            );
            uploaded = true;
        } catch (Exception e) {
            if (uploaded) {
                storageProvider.delete(
                        profileImagesPath,
                        storedName
                );
            }
            throw new RuntimeException(); //TODO: Change In Exceptions Section
        }
    }

    private String baseImageValidation(MultipartFile file, List<String> mimeTypes) {
        if (file.isEmpty())
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        String mimetype = file.getContentType();
        if (mimetype == null || !mimeTypes.contains(mimetype))
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        String originalName = file.getOriginalFilename();
        if (originalName == null || cloudStorageServiceUtils.containsPathSeparator(originalName))
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        return mimetype;
    }

    private String getFileExtension(String mimetype) {
        return mimetype.split("/")[1];
    }

    private void updateUserImageKey(UUID userId, String imageKey) {
        Query query = Query.query(
                Criteria.where("userId").is(userId)
        );
        Update update = new Update()
                .set("imageKey", imageKey);

        mongoTemplate.updateFirst(query, update, UserRepresentation.class);
        userRepository.updateImageKeyByUserId(imageKey, userId);
    }
}
