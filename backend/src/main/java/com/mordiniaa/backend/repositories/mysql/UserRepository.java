package com.mordiniaa.backend.repositories.mysql;

import com.mordiniaa.backend.models.user.mysql.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @Modifying
    @Query("update User u set u.imageKey = :imageKey where u.userId = :userId")
    void updateImageKeyByUserId(String imageKey, UUID userId);
}
