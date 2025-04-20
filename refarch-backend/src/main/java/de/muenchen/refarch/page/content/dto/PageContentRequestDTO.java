package de.muenchen.refarch.page.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record PageContentRequestDTO(
        @NotNull(message = "Language ID is required") UUID languageId,

        @NotBlank(message = "Title is required") String title,

        @NotBlank(message = "Content is required") String content,

        String shortDescription,

        String keywords) {
}
