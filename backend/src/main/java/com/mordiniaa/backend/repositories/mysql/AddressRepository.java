package com.mordiniaa.backend.repositories.mysql;

import com.mordiniaa.backend.models.user.mysql.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findByIdAndUser_UserId(Long id, UUID userUserId);
}
