package de.muenchen.refarch.link;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkRepository extends JpaRepository<Link, UUID> {
    List<Link> findByScope(LinkScope scope);
}
