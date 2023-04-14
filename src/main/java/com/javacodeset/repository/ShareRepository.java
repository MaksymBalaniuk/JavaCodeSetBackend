package com.javacodeset.repository;

import com.javacodeset.entity.ShareEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShareRepository extends JpaRepository<ShareEntity, UUID> {
    Boolean existsByToUserIdAndFromUserIdAndCodeBlockId(UUID toUserId, UUID fromUserId, UUID codeBlockId);
    List<ShareEntity> findAllByToUserId(UUID userId);
    List<ShareEntity> findAllByFromUserId(UUID userId);
}
