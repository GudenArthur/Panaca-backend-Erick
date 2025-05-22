package Panaca.dto.PQR;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record ResponderPQRDTO(
        String id,

        @NotBlank(message = "La respuesta no puede estar vacía.")
        String respuesta
) {}
