package ua.nix.balaniuk.javacodeset.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.nix.balaniuk.javacodeset.entity.EstimateEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface EstimateRepository extends JpaRepository<EstimateEntity, UUID> {
    List<EstimateEntity> findAllByCodeBlockId(UUID codeBlockId);
}
