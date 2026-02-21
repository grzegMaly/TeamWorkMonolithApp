package com.mordiniaa.backend.controllers.global;

import com.mordiniaa.backend.services.storage.profileImagesStorage.ImagesStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImagesStorageService imagesStorageService;

    @GetMapping
    public ResponseEntity<StreamingResponseBody> getImage() {
        return null;
    }

    @GetMapping("/profile/{key}")
    public ResponseEntity<StreamingResponseBody> getProfileImage(@PathVariable String key) {
        return imagesStorageService.getProfileImage(key);
    }
}
