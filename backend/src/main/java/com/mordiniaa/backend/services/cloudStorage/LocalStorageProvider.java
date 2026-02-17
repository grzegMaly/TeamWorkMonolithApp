package com.mordiniaa.backend.services.cloudStorage;

import com.mordiniaa.backend.models.file.FileNodeStorageKey;
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

    private final Path root = Paths.get("file_storage");

    @Override
    public void upload(String key, InputStream stream, long size) throws IOException {

        Files.createDirectories(root);

        Path target = root.resolve(key);
        Files.copy(stream, target, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void delete(String storageKey) {
        Path target = root.resolve(storageKey);

        try {
            Files.deleteIfExists(target);
        } catch (IOException ex) {
            throw new RuntimeException(); // TODO: Change In Exceptions Section
        }
    }

    @Override
    public InputStream downloadFile(String storageKey) {
        Path sourcePath = root.resolve(storageKey);
        try {
            return Files.newInputStream(sourcePath, StandardOpenOption.READ);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StreamingResponseBody downloadDir(Map.Entry<String, Map<String, Object>> dirTree) {

        return outputStream -> {
            try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
                buildZip(dirTree, "", zos);
            }
        };
    }

    private void buildZip(
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

                try (InputStream in = downloadFile(file.getStorageKey())) {
                    in.transferTo(zos);
                }
                zos.closeEntry();
            } else if (entry.getValue() instanceof Map<?, ?> subTree) {

                @SuppressWarnings("unchecked")
                Map<String, Object> sb = (Map<String, Object>) subTree;

                buildZip(
                        new AbstractMap.SimpleEntry<>(entry.getKey(), sb),
                        currentPath,
                        zos
                );
            }
        }
    }
}
