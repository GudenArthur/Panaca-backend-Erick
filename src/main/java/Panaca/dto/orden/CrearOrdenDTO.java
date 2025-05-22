package Panaca.dto.orden;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record CrearOrdenDTO(

        @NotBlank(message = "El ID del cliente no puede estar vacío.")
        String idCliente,

        String codigoCupon,  // Este campo puede ser nulo si no se usa un cupón

        String codigoPasarela,

        @NotNull(message = "La fecha de la orden es obligatoria.")
        @FutureOrPresent
        LocalDate fecha,

        @NotNull(message = "La lista de detalles de la orden no puede estar vacía.")
        @Size(min = 1, message = "La orden debe tener al menos un detalle.")
        List<DetalleOrdenDTO> detalleOrden

) {}