package com.javacodeset.repository;

import com.javacodeset.entity.CodeBlockEntity;
import com.javacodeset.enumeration.EstimateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CodeBlockRepository extends JpaRepository<CodeBlockEntity, UUID> {
    List<CodeBlockEntity> findAllByUserId(UUID userId);

    @Query("SELECT codeBlock FROM CodeBlock codeBlock " +
            "INNER JOIN Estimate estimate " +
            "ON codeBlock.id = estimate.codeBlock.id " +
            "AND estimate.user.id = ?1 AND estimate.type = ?2")
    List<CodeBlockEntity> findAllCodeBlocksByUserIdAndEstimateType(UUID userId, EstimateType estimateType);
}
