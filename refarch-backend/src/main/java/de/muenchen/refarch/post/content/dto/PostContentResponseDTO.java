package de.muenchen.refarch.post.content.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostContentResponseDTO(
        UUID id,
        UUID postId,
        UUID languageId,
        String title,
        String content,
        String shortDescription,
        String keywords,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
