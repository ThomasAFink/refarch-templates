package de.muenchen.refarch.page.content;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageContentRepository extends JpaRepository<PageContent, UUID> {
    Optional<PageContent> findByPageIdAndLanguageId(UUID pageId, UUID languageId);

    boolean existsByPageIdAndLanguageId(UUID pageId, UUID languageId);
}
