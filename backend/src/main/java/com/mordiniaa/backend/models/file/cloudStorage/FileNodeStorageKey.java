package com.mordiniaa.backend.models.file.cloudStorage;

import java.util.UUID;

public interface FileNodeStorageKey {

    UUID getId();

    NodeType getNodeType();

    String getName();

    String getStorageKey();
}
