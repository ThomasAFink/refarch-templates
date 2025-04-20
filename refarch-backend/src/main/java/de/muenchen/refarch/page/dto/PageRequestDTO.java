package de.muenchen.refarch.page.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record PageRequestDTO(
        @NotNull(message = "Link ID is required") UUID linkId,
        String thumbnail,
        @NotNull(message = "Comments enabled flag is required") Boolean commentsEnabled,
        @NotNull(message = "Published flag is required") Boolean published) {
}
