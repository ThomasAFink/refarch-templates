package de.muenchen.refarch.user.bio;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBioRepository extends JpaRepository<UserBio, UUID> {
    Optional<UserBio> findByUserIdAndLanguageId(UUID userId, UUID languageId);

    boolean existsByUserIdAndLanguageId(UUID userId, UUID languageId);
}
