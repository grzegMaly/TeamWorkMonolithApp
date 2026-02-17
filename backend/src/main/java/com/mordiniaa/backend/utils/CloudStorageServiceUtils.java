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

    @Transactional
    public UserStorage getOrCreateUserStorage(UUID userId) {
        return userStorageRepository.findById(userId)
                .orElseGet(() -> createNewStorageSafely(userId));
    }

    @Transactional
    public UserStorage createNewStorageSafely(UUID userId) {
        try {
            return userStorageRepository.save(new UserStorage(userId));
        } catch (DataIntegrityViolationException exception) {
            return userStorageRepository.findById(userId)
                    .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section
        }
    }

    public Set<UUID> collectParentChain(FileNode parent, UUID userId) {
        Set<UUID> ids = new HashSet<>();

        FileNode current = parent;
        while (current != null) {
            ids.add(current.getId());
            current = current.getParentId() == null
                    ? null
                    : fileNodeRepository.findDirByIdAndOwnerId(current.getParentId(), userId).orElse(null);
        }
        return ids;
    }

    public FileNodeBaseMeta getBaseFileProjection(UUID parentId, UUID userId) {
        return fileNodeRepository.findParentMetaProjection(parentId, userId)
                .orElse(null);
    }
}
