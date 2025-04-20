package de.muenchen.refarch.post;

import de.muenchen.refarch.common.EntityCopyUtils;
import de.muenchen.refarch.link.Link;
import de.muenchen.refarch.post.content.PostContent;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "posts")
@Getter
@Setter
@SuppressWarnings("PMD.ShortClassName")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "link_id")
    @Getter(AccessLevel.NONE)
    private Link link;

    @Column(length = 510)
    private String thumbnail;

    @Column(name = "comments_enabled")
    private boolean commentsEnabled = true;

    @Column(name = "published")
    private boolean published = false;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter(AccessLevel.NONE)
    private Set<PostContent> contents = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Link getLink() {
        return EntityCopyUtils.copyLink(link);
    }

    public Set<PostContent> getContents() {
        return contents == null ? Collections.emptySet() : Collections.unmodifiableSet(contents);
    }

    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof Post other && Objects.equals(id, other.id));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void addContent(final PostContent content) {
        if (content != null) {
            contents.add(content);
            content.setPost(this);
        }
    }

    public void removeContent(final PostContent content) {
        if (content != null) {
            contents.remove(content);
            content.setPost(null);
        }
    }
}
