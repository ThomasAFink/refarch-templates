package de.muenchen.refarch.auth.token;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenAndIsValidTrue(String token);

    List<RefreshToken> findByUserIdAndIsValidTrueAndExpiresAtAfter(UUID userId, LocalDateTime now);

    void deleteByUserIdAndIsValidTrue(UUID userId);

    void deleteByExpiresAtBefore(LocalDateTime now);
}
