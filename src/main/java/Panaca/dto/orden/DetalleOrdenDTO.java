package Panaca.dto.orden;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record DetalleOrdenDTO(

        @NotNull(message = "El ID del evento no puede ser nulo")
        String idEvento,

        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        int cantidad,

        @NotNull(message = "La fecha de agregación es obligatoria")
        @PastOrPresent(message = "La fecha de agregación no puede ser en el futuro")
        LocalDate fechaUso
) {}
