package com.mordiniaa.backend.services.storage;


import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface StorageProvider {

    void upload(String resourceName, String storageKey, InputStream stream) throws IOException;

    void uploadImage(String resourceName, String storageKey, String ext, int maxWidth, int maxHeight, InputStream stream) throws IOException;

    void delete(String resourceName, String storageKey);

    InputStream downloadFile(String resourceName, String storageKey);

    StreamingResponseBody downloadDir(String resourceName, Map.Entry<String, Map<String, Object>> dirTree);
}
