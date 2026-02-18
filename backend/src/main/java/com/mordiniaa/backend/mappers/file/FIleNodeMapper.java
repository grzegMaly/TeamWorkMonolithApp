package com.mordiniaa.backend.mappers.file;

import com.mordiniaa.backend.dto.file.FileNodeDto;
import com.mordiniaa.backend.models.file.cloudStorage.FileNode;
import com.mordiniaa.backend.models.file.cloudStorage.NodeType;
import org.springframework.stereotype.Component;

@Component
public class FIleNodeMapper {

    public FileNodeDto toDto(FileNode fileNode, String parentPath) {
        FileNodeDto dto = new FileNodeDto();
        dto.setNodeType(fileNode.getNodeType());
        dto.setId(fileNode.getId());
        dto.setParentPath(parentPath);
        dto.setSize(fileNode.getNodeType().equals(NodeType.DIRECTORY) ? fileNode.getSubTreeSize() : fileNode.getSize());
        dto.setName(fileNode.getName());
        return dto;
    }
}
