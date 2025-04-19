package de.muenchen.refarch.homepage;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomepageRepository extends JpaRepository<Homepage, UUID> {
}
