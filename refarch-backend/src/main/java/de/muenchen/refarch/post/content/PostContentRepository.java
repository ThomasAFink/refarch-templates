package de.muenchen.refarch.post.content;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostContentRepository extends JpaRepository<PostContent, UUID> {
    Optional<PostContent> findByPostIdAndLanguageId(UUID postId, UUID languageId);

    boolean existsByPostIdAndLanguageId(UUID postId, UUID languageId);
}