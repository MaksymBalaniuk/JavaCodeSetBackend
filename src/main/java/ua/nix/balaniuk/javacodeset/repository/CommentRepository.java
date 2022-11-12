package ua.nix.balaniuk.javacodeset.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.nix.balaniuk.javacodeset.entity.CommentEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {
    List<CommentEntity> findAllByCodeBlockIdOrderByUpdatedDesc(UUID codeBlockId);
}
