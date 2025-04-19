package de.muenchen.refarch.posts.users;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostsUsersRepository extends JpaRepository<PostsUsers, UUID> {
    List<PostsUsers> findByPostLinkId(UUID postLinkId);

    List<PostsUsers> findByUserId(UUID userId);

    void deleteByPostLinkIdAndUserId(UUID postLinkId, UUID userId);
}
