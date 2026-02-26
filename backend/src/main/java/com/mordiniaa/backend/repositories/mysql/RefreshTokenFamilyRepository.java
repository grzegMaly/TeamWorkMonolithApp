package com.mordiniaa.backend.repositories.mysql;

import com.mordiniaa.backend.security.model.RefreshTokenFamily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenFamilyRepository extends JpaRepository<RefreshTokenFamily, Long> {
    Optional<RefreshTokenFamily> findRefreshTokenFamilyByIdAndUserIdAndRevokedFalse(Long id, UUID userId);
}
