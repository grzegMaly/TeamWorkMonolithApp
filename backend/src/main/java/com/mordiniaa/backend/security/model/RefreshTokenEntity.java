package com.mordiniaa.backend.security.model;

import com.mordiniaa.backend.utils.JsonStringListConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "RefreshToken")
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_rt_user", columnList = "user_id"),
        @Index(name = "idx_rt_family", columnList = "family_id"),
        @Index(name = "idx_rt_revoked", columnList = "revoked")
})
public class RefreshTokenEntity {

    @Version
    private Long version;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", updatable = false)
    private UUID userId;

    @Column(name = "hashed_token", updatable = false, nullable = false)
    private String hashedToken;

    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false, updatable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "family_id", columnDefinition = "BINARY(16)")
    private UUID familyId;

    @Column(name = "family_expires_at", nullable = false, updatable = false)
    private Instant familyExpiresAt;

    @Column(name = "replaced_by_id")
    private Long replacedById;

    @Convert(converter = JsonStringListConverter.class)
    @Column(columnDefinition = "json", nullable = false)
    private List<String> roles;
}
