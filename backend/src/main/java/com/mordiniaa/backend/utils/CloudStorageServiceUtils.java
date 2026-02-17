package com.mordiniaa.backend.utils;

import com.mordiniaa.backend.models.file.FileNode;
import com.mordiniaa.backend.models.file.FileNodeBaseMeta;
import com.mordiniaa.backend.models.file.UserStorage;
import com.mordiniaa.backend.repositories.mysql.FileNodeRepository;
import com.mordiniaa.backend.repositories.mysql.UserStorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CloudStorageServiceUtils {

    private final UserStorageRepository userStorageRepository;
    private final FileNodeRepository fileNodeRepository;

    public boolean containsPathSeparator(String filename) {
        return filename.contains("/") || filename.contains("\\");
    }
}
