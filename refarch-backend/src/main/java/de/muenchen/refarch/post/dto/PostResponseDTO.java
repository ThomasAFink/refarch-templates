package de.muenchen.refarch.post.dto;

import de.muenchen.refarch.post.content.dto.PostContentResponseDTO;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public record PostResponseDTO(
        UUID id,
        UUID linkId,
        String thumbnail,
        boolean commentsEnabled,
        boolean published,
        Set<PostContentResponseDTO> contents,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
    /**
     * Creates a new PostResponseDTO with defensive copy of contents.
     */
    public PostResponseDTO {
        contents = defensiveCopyContents(contents);
    }

    /**
     * Returns an unmodifiable view of the contents set.
     * If the contents are null, returns an empty set.
     *
     * @return An unmodifiable set of post contents
     */
    @Override
    public Set<PostContentResponseDTO> contents() {
        return defensiveCopyContents(contents);
    }

    /**
     * Creates a defensive copy of the contents set.
     *
     * @param contentsToDefend The set to create a defensive copy of
     * @return An unmodifiable defensive copy of the set, or an empty set if input is null
     */
    private static Set<PostContentResponseDTO> defensiveCopyContents(final Set<PostContentResponseDTO> contentsToDefend) {
        return contentsToDefend == null ? Collections.emptySet()
                : Collections.unmodifiableSet(new HashSet<>(contentsToDefend));
    }

}
