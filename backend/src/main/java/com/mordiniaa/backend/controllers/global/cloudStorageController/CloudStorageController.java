package com.mordiniaa.backend.controllers.global.cloudStorageController;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/storage")
public class CloudStorageController {

    @PostMapping
    public void upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("parentId") UUID parentId
            ) {
    }
}
