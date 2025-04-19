package de.muenchen.refarch.globalsettings;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalSettingsRepository extends JpaRepository<GlobalSettings, UUID> {
    // Since we only have one global settings record, we don't need additional methods
}
