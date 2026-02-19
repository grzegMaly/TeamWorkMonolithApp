package com.mordiniaa.backend.models.file.cloudStorage;

import java.util.UUID;

public interface FileNodeBaseMeta {

    UUID getId();

    UUID getParentId();

    String getStorageKey();

    NodeType getNodeType();
}
