package com.mordiniaa.backend.services.storage.profileImagesStorage;

import com.mordiniaa.backend.config.StorageProperties;
import com.mordiniaa.backend.models.file.imageStorage.ImageMetadata;
import com.mordiniaa.backend.repositories.mongo.ImageMetadataRepository;
import com.mordiniaa.backend.services.storage.StorageProvider;
import com.mordiniaa.backend.utils.MongoIdUtils;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ImagesStorageService {

    private final MongoIdUtils mongoIdUtils;
    private final ImageMetadataRepository imageMetadataRepository;
    private final StorageProvider storageProvider;
    private final StorageProperties storageProperties;

    public ResponseEntity<StreamingResponseBody> getProfileImage(String key) {

        if (key == null || key.equals("defaultProfileImage"))
            return defaultImage();

        ObjectId objectId = mongoIdUtils.getObjectId(key);

        ImageMetadata meta = imageMetadataRepository.findById(objectId)
                .orElse(null);

        if (meta == null)
            return defaultImage();

        StreamingResponseBody body = outputStream -> {
            try (InputStream in = storageProvider.downloadFile(
                    storageProperties.getProfileImages().getPath(),
                    meta.storedName()
            )) {
                in.transferTo(outputStream);
            }
        };

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(meta.mimeType()))
                .body(body);
    }

    private ResponseEntity<StreamingResponseBody> defaultImage() {

        ClassPathResource resource = new ClassPathResource("static/images/defaultProfileImage.png");

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
}
