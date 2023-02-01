package com.paulgougassian.repository;

import com.paulgougassian.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByUuid(UUID uuid);

    Optional<Company> findByEmail(String email);

    Optional<Company> findByEmailAndPassword(String email, String password);
}
