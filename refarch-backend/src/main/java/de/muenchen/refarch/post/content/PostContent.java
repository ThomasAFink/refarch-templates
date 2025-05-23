package de.muenchen.refarch.post.content;

import de.muenchen.refarch.common.EntityCopyUtils;
import de.muenchen.refarch.language.Language;
import de.muenchen.refarch.post.Post;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(
        name = "posts_content_i18n",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = { "post_id", "language_id" })
        }
)
@Getter
@Setter
public class PostContent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "language_id", nullable = false)
    @Getter(AccessLevel.NONE)
    private Language language;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(name = "short_description")
    private String shortDescription;

    private String keywords;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Post getPost() {
        return EntityCopyUtils.copyPost(post);
    }

    public Language getLanguage() {
        return EntityCopyUtils.copyLanguage(language);
    }

}
