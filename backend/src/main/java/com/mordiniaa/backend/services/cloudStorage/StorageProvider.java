package com.mordiniaa.backend.services.cloudStorage;


import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface StorageProvider {

    void upload(String key, InputStream stream, long size) throws IOException;

    void delete(String storageKey);

    InputStream downloadFile(String storageKey);

    StreamingResponseBody downloadDir(Map.Entry<String, Map<String, Object>> dirTree);
}
