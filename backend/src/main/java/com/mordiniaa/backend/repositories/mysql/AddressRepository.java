package com.mordiniaa.backend.repositories.mysql;

import com.mordiniaa.backend.models.user.mysql.Address;
import com.mordiniaa.backend.models.user.mysql.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findByIdAndUser(Long id, User user);
}
