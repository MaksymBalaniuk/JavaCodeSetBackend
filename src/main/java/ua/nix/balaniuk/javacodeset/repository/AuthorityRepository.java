package ua.nix.balaniuk.javacodeset.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.nix.balaniuk.javacodeset.entity.AuthorityEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthorityRepository extends JpaRepository<AuthorityEntity, UUID> {
    Optional<AuthorityEntity> findByName(String authorityName);
}
