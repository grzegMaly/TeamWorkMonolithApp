package com.mordiniaa.backend.repositories.mysql;

import com.mordiniaa.backend.models.file.cloudStorage.UserStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserStorageRepository extends JpaRepository<UserStorage, UUID> {
}
