package de.muenchen.refarch.language;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageRepository extends JpaRepository<Language, UUID> {
    boolean existsByAbbreviation(String abbreviation);
}
