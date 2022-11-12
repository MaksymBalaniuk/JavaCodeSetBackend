package ua.nix.balaniuk.javacodeset.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.nix.balaniuk.javacodeset.entity.CodeBlockEntity;
import ua.nix.balaniuk.javacodeset.enumeration.CodeBlockType;
import ua.nix.balaniuk.javacodeset.enumeration.EstimateType;

import java.util.List;
import java.util.UUID;

@Repository
public interface CodeBlockRepository extends JpaRepository<CodeBlockEntity, UUID> {
    List<CodeBlockEntity> findAllByOrderByUpdatedDesc();
    List<CodeBlockEntity> findAllByUserId(UUID userId);
    List<CodeBlockEntity> findAllByUserIdOrderByUpdatedDesc(UUID userId);
    List<CodeBlockEntity> findAllByType(CodeBlockType codeBlockType);

    @Query("SELECT codeBlock FROM CodeBlock codeBlock " +
            "INNER JOIN Estimate estimate " +
            "ON codeBlock.id = estimate.codeBlock.id " +
            "AND estimate.user.id = ?1 AND estimate.type = ?2 " +
            "ORDER BY codeBlock.updated DESC")
    List<CodeBlockEntity> findAllCodeBlocksByUserIdAndEstimateType(UUID userId, EstimateType estimateType);
}
