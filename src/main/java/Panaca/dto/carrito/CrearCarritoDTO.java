package Panaca.dto.carrito;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record CrearCarritoDTO(

        @NotBlank (message = "El id del usuario no puede ser nulo o vacio")
        String idUsuario,

        @NotEmpty(message = "Los items del carrito no puede ser nula ni vacia.")
        List<DetalleCarritoDTO> itemsCarrito

) {}