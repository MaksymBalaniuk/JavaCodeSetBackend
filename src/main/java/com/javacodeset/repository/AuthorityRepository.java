package com.javacodeset.repository;

import com.javacodeset.entity.AuthorityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthorityRepository extends JpaRepository<AuthorityEntity, UUID> {
    Optional<AuthorityEntity> findByName(String authorityName);
}
