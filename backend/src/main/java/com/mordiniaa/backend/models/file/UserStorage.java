package com.mordiniaa.backend.models.file;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserStorage {

    @Version
    private long version;

    @Id
    @Column(nullable = false, unique = true, name = "user_id")
    private UUID userId;

    @Column(unique = true)
    private UUID resourceId;

    private Long usedBytes = 0L;
    private Long quotaBytes = 50_000_000_000L;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "root_node_id")
    private FileNode rootNode;

    @OneToMany(mappedBy = "userStorage", fetch = FetchType.LAZY)
    private List<FileNode> storedFiles = new ArrayList<>();

    public UserStorage(UUID userId) {
        this.userId = userId;
    }

    @PrePersist
    public void createRootIfNeeded() {
        if (rootNode == null) {
            FileNode root = new FileNode();
            root.setNodeType(NodeType.ROOT);
            root.setMaterializedPath("/");
            root.setUserStorage(this);
            this.rootNode = root;
        }
    }
}
