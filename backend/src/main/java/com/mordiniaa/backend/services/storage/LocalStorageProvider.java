package com.mordiniaa.backend.services.storage;

import com.mordiniaa.backend.models.file.cloudStorage.FileNodeStorageKey;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.AbstractMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class LocalStorageProvider implements StorageProvider {

    @Override
    public void upload(String resourcePath, String storageKey, InputStream stream, long size) throws IOException {

        Path resource = Paths.get(resourcePath);
        Files.createDirectories(resource);

        Path target = resource.resolve(storageKey);
        Files.copy(stream, target, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void delete(String resourcePath, String storageKey) {

        Path target = Paths.get(resourcePath).resolve(storageKey);

        try {
            Files.deleteIfExists(target);
        } catch (IOException ex) {
            throw new RuntimeException(); // TODO: Change In Exceptions Section
        }
    }

    @Override
    public InputStream downloadFile(String resourcePath, String storageKey) {
        Path sourcePath = Paths.get(resourcePath).resolve(storageKey);
        try {
            return Files.newInputStream(sourcePath, StandardOpenOption.READ);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StreamingResponseBody downloadDir(String resourcePath, Map.Entry<String, Map<String, Object>> dirTree) {

        return outputStream -> {
            try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
                buildZip(resourcePath, dirTree, "", zos);
            }
        };
    }

    private void buildZip(
            String resourcePath,
            Map.Entry<String, Map<String, Object>> root,
            String basePath,
            ZipOutputStream zos
    ) throws IOException {

        String currentPath = basePath + root.getKey() + "/";
        zos.putNextEntry(new ZipEntry(currentPath));
        zos.closeEntry();

        for (Map.Entry<String, Object> entry : root.getValue().entrySet()) {
            if (entry.getValue() instanceof FileNodeStorageKey file) {
                ZipEntry zipEntry = new ZipEntry(currentPath + file.getName());
                zos.putNextEntry(zipEntry);

                try (InputStream in = downloadFile(resourcePath, file.getStorageKey())) {
                    in.transferTo(zos);
                }
                zos.closeEntry();
            } else if (entry.getValue() instanceof Map<?, ?> subTree) {

                @SuppressWarnings("unchecked")
                Map<String, Object> sb = (Map<String, Object>) subTree;

                buildZip(
                        resourcePath,
                        new AbstractMap.SimpleEntry<>(entry.getKey(), sb),
                        currentPath,
                        zos
                );
            }
        }
    }
}
