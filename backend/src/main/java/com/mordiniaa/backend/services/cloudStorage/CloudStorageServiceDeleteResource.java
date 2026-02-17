package com.mordiniaa.backend.services.cloudStorage;

import com.mordiniaa.backend.models.file.*;
import com.mordiniaa.backend.repositories.mysql.FileNodeRepository;
import com.mordiniaa.backend.repositories.mysql.UserStorageRepository;
import com.mordiniaa.backend.utils.CloudStorageServiceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CloudStorageServiceDeleteResource {

    private final FileNodeRepository fileNodeRepository;
    private final StorageProvider storageProvider;
    private final UserStorageRepository userStorageRepository;
    private final CloudStorageServiceUtils cloudStorageServiceUtils;

    @Transactional
    public void deleteFileNode(UUID userId, UUID nodeId) {

        FileNode resource = fileNodeRepository
                .findNodeByIdAndUserId(nodeId, userId)
                .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section

        UserStorage userStorage = resource.getUserStorage();
        long resourceSize = resource.getNodeType().equals(NodeType.DIRECTORY)
                ? resource.getSubTreeSize()
                : resource.getSize();
        long newUsedValue = userStorage.getUsedBytes() - resourceSize;

        resource.setDeleted(true);
        userStorage.setUsedBytes(Math.max(newUsedValue, 0));

        fileNodeRepository.save(resource);
        userStorageRepository.save(userStorage);

        FileNode resourceParent = resource.getParentId() == null
                ? null
                : fileNodeRepository.findNodeByIdAndUserId(resource.getParentId(), userId).orElse(null);

        if (resourceParent != null) {
            Set<UUID> ids = cloudStorageServiceUtils.collectParentChain(resourceParent, userId);
            fileNodeRepository.decreaseTreeSize(ids, userId, resourceSize);
        }
    }
}
