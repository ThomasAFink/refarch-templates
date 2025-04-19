package de.muenchen.refarch.comment;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByPostId(UUID postId);

    List<Comment> findByPageId(UUID pageId);

    List<Comment> findByUserId(UUID userId);

    List<Comment> findByPostIdOrderByCreatedAtDesc(UUID postId);

    List<Comment> findByPageIdOrderByCreatedAtDesc(UUID pageId);

    void deleteByPostIdAndUserId(UUID postId, UUID userId);

    void deleteByPageIdAndUserId(UUID pageId, UUID userId);
}
