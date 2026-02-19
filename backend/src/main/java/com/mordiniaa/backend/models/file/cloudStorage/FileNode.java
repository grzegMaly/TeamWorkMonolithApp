package com.mordiniaa.backend.models.file.cloudStorage;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class FileNode {

    @Version
    private Long version;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private UUID parentId;
    private String storageKey;
    private String materializedPath;

    // For Files
    private Long size = 0L;

    // For Dirs
    private Long subTreeSize = 0L;

    @Enumerated(EnumType.STRING)
    private NodeType nodeType;
    private boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_storage_id", referencedColumnName = "user_id")
    private UserStorage userStorage;

    public FileNode(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public void setMaterializedPath(String path) {
        this.materializedPath = path;
    }

    public void setMaterializedPath(FileNode parent) {
        String parentPath = parent.getMaterializedPath();
        if (parentPath == null) {
            throw new RuntimeException();
        }
        this.materializedPath = parentPath + "/" + this.id;
    }
}
