package com.mordiniaa.backend.services.cloudStorage;

import com.mordiniaa.backend.models.file.FileNode;
import com.mordiniaa.backend.models.file.NodeType;
import com.mordiniaa.backend.models.file.UserStorage;
import com.mordiniaa.backend.repositories.mysql.FileNodeRepository;
import com.mordiniaa.backend.repositories.mysql.UserStorageRepository;
import com.mordiniaa.backend.services.fileNode.FileNodeService;
import com.mordiniaa.backend.utils.CloudStorageServiceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CloudStorageServiceCreateResource {

    private final FileNodeService fileNodeService;
    private final StorageProvider storageProvider;
    private final FileNodeRepository fileNodeRepository;
    private final UserStorageRepository userStorageRepository;
    private final CloudStorageServiceUtils cloudStorageServiceUtils;

    @Transactional
    public void createDir(UUID userId, UUID parentId, String dirName) {

        if (cloudStorageServiceUtils.containsPathSeparator(dirName))
            throw new RuntimeException(); // TODO: Change In Exceptions Section

        UserStorage userStorage = cloudStorageServiceUtils.getOrCreateUserStorage(userId);

        FileNode parent = fileNodeService.getDirectory(parentId, userId);
        FileNode dirNode = new FileNode(NodeType.DIRECTORY);
        if (parent != null) {
            dirNode.setParentId(parent.getId());
            dirNode.setMaterializedPath(parent);
        }

        dirNode.setName(dirName.trim());
        dirNode.setUserStorage(userStorage);
        dirNode.setStorageKey(null);
        fileNodeRepository.save(dirNode);
    }
}
