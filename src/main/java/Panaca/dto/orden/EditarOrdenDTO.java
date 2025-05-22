package Panaca.dto.orden;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record EditarOrdenDTO (

        @NotNull(message = "Usuario no encontrado")
        String id,

        String codigoCupon,  // Este campo puede ser nulo si no se usa un cupón

        @NotNull(message = "La lista de detalles es obligatoria.")
        @Size(min = 1, message = "Debe contener al menos un ítem.")
        List<DetalleOrdenDTO> detalleOrden
){}