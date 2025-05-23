package de.muenchen.refarch.homepage.content;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomepageContentRepository extends JpaRepository<HomepageContent, UUID> {
    Optional<HomepageContent> findByHomepageIdAndLanguageId(UUID homepageId, UUID languageId);

    boolean existsByHomepageIdAndLanguageId(UUID homepageId, UUID languageId);
}
