package com.mordiniaa.backend.services.cloudStorage;

import com.mordiniaa.backend.dto.file.FileNodeDto;
import com.mordiniaa.backend.mappers.file.FIleNodeMapper;
import com.mordiniaa.backend.models.file.*;
import com.mordiniaa.backend.repositories.mysql.FileNodeRepository;
import com.mordiniaa.backend.utils.CloudStorageServiceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CloudStorageServiceGetResource {

    private final FileNodeRepository fileNodeRepository;
    private final CloudStorageServiceUtils cloudStorageServiceUtils;
    private final FIleNodeMapper fileNodeMapper;
    private final StorageProvider storageProvider;

    @Transactional
    public List<FileNodeDto> getResourceListRootLvl(UUID userId) {

        UserStorage userStorage = cloudStorageServiceUtils.getOrCreateUserStorage(userId);
        FileNode rootNode = userStorage.getRootNode();

        if (rootNode == null) {
            return List.of();
        }

        return fileNodeRepository.findFileNodesByParentIdAndUserStorage_UserId(rootNode.getId(), userId)
                .stream()
                .map(node -> fileNodeMapper.toDto(node, "/"))
                .toList();
    }

    public List<FileNodeDto> getResourceList(UUID userId, UUID dirId) {

        FileNode requestedDir = fileNodeRepository.findDirByIdAndOwnerId(dirId, userId)
                .orElseThrow(RuntimeException::new); // TODO: Change In Exceptions Section

        List<UUID> ids = Arrays.stream(requestedDir.getMaterializedPath().split("/"))
                .filter(s -> !s.isBlank())
                .map(UUID::fromString)
                .toList();


        Map<UUID, FileNodeBreadcrumb> breadcrumbs = fileNodeRepository.getFileNodeBreadcrumbs(new HashSet<>(ids), userId)
                .stream()
                .collect(Collectors.toMap(
                        FileNodeBreadcrumb::getId,
                        Function.identity()
                ));

        StringBuilder sb = new StringBuilder();
        for (UUID id : ids) {
            FileNodeBreadcrumb node = breadcrumbs.get(id);
            if (node != null)
                sb.append("/").append(node.getName());
        }

        String path = sb.toString();
        return fileNodeRepository.findFileNodesByParentIdAndUserStorage_UserId(requestedDir.getId(), userId)
                .stream()
                .map(node -> fileNodeMapper.toDto(node, path))
                .toList();
    }
}
