package com.javacodeset.repository;

import com.javacodeset.entity.EstimateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EstimateRepository extends JpaRepository<EstimateEntity, UUID> {
    List<EstimateEntity> findAllByCodeBlockId(UUID codeBlockId);
}
