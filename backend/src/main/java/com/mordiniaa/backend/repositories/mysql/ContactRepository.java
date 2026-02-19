package com.mordiniaa.backend.repositories.mysql;

import com.mordiniaa.backend.models.user.mysql.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
}
