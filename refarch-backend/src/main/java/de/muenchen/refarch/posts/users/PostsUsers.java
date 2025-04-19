package de.muenchen.refarch.posts.users;

import de.muenchen.refarch.common.EntityCopyUtils;
import de.muenchen.refarch.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "posts_users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostsUsers {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "post_link_id", nullable = false)
    private UUID postLinkId;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @Getter(AccessLevel.NONE)
    private User user;

    public User getUser() {
        return EntityCopyUtils.copyUser(user);
    }
}
