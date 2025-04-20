package de.muenchen.refarch.homepage.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record HomepageRequestDTO(
        @NotNull(message = "Link ID is required") UUID linkId,
        String thumbnail) {
}
