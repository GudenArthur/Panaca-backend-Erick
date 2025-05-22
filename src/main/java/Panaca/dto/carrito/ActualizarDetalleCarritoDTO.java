package Panaca.dto.carrito;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public record ActualizarDetalleCarritoDTO(

        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        int cantidad,

        @NotNull(message = "La fecha de agregación es obligatoria")
        @PastOrPresent(message = "La fecha de agregación no puede ser en el futuro")
        LocalDate fechaUso
) {}
