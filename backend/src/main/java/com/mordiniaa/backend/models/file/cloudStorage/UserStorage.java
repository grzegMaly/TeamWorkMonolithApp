package com.mordiniaa.backend.models.file.cloudStorage;

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
@Table(indexes = @Index(name = "fx_user_id", columnList = "user_id", unique = true))
public class UserStorage {

    @Version
    private long version;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID resourceId;

    @Column(name = "user_id", unique = true, nullable = false)
    private UUID userId;

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
