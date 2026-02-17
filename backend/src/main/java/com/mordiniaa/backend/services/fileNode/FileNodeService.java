package com.mordiniaa.backend.services.fileNode;

import com.mordiniaa.backend.models.file.FileNode;
import com.mordiniaa.backend.repositories.mysql.FileNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileNodeService {

    private final FileNodeRepository fileNodeRepository;

    public FileNode getDirectory(UUID parentId, UUID userId) {
        return fileNodeRepository.findDirByIdAndOwnerId(parentId, userId)
                .orElse(null);
    }
}
