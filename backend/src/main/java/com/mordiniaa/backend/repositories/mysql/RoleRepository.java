package com.mordiniaa.backend.repositories.mysql;

import com.mordiniaa.backend.models.user.mysql.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
}
