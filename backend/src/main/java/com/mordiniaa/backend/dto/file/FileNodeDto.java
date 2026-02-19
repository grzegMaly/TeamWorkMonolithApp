package com.mordiniaa.backend.dto.file;

import com.mordiniaa.backend.models.file.cloudStorage.NodeType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class FileNodeDto {

    private UUID id;
    private String parentPath;
    private String name;
    private String nodeType;
    private Long size;

    //Protection to not publish ROOT
    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType.equals(NodeType.FILE) ? "FILE" : "DIRECTORY";
    }
}
