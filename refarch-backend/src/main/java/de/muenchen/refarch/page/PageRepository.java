package de.muenchen.refarch.page;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageRepository extends JpaRepository<Page, UUID> {
    boolean existsByLinkId(UUID linkId);
}
