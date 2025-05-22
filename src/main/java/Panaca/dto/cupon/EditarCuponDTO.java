package Panaca.dto.cupon;

import Panaca.model.enums.EstadoCupon;
import Panaca.model.enums.TipoCupon;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record EditarCuponDTO (
        @NotBlank @Min(3) @Max(30) String codigo,
        @NotBlank @Min(5) @Max(50) String nombre,
        @NotBlank float porcentajeDescuento,
        @NotBlank @FutureOrPresent LocalDateTime fechaVencimiento,
        LocalDateTime fechaApertura,
        @NotBlank TipoCupon tipoCupon,
        @NotBlank EstadoCupon estadoCupon
){}