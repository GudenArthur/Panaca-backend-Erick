package Panaca.dto.evento;

import Panaca.model.enums.EstadoEvento;
import Panaca.model.enums.TipoEvento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CrearEventoDTO(

        @NotEmpty(message = "El nombre de la boleta es obligatorio.")
        String nombre,

        @NotEmpty(message = "La descripción de la boleta es obligatoria.")
        String descripcion,

        @NotEmpty(message = "La imagen de portada es obligatoria.")
        String imagenPortada,

        @NotNull(message = "El estado de la boleta es obligatoria.")
        EstadoEvento estado,

        @NotNull(message = "El tipo de boleta es obligatorio.")
        TipoEvento tipo,

        @DecimalMin(value = "0.0", inclusive = false, message = "El total debe ser mayor a 0.")
        @NotNull(message = "El precio de la boleta es obligatoria.")
        Float precio

) {}