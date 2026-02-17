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
}
