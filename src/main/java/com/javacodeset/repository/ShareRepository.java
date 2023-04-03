package com.javacodeset.repository;

import com.javacodeset.entity.ShareEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShareRepository extends JpaRepository<ShareEntity, UUID> {

    @Query("SELECT share FROM Share share " +
            "WHERE share.toUser.id = ?1")
    List<ShareEntity> findAllSharesToUserId(UUID userId);
}