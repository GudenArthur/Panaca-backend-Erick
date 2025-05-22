package Panaca.dto.devolucion;

import Panaca.model.enums.TipoDevolucion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DevolucionRequestDTO(

        @NotBlank(message = "El ID de la cuenta es requerido")
        String cuentaId,

        @NotNull(message = "Debe especificar el tipo de devolución")
        TipoDevolucion tipo,

        @NotBlank(message = "La referencia de la orden o donación es requerida")
        String referenciaId

) {}