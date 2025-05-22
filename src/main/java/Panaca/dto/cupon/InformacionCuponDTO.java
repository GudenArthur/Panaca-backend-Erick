package Panaca.dto.cupon;

import Panaca.model.enums.EstadoCupon;
import Panaca.model.enums.TipoCupon;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record InformacionCuponDTO(
        String nombre,
        String codigo,
        float porcentajeDescuento,
        LocalDateTime fechaVencimiento,
        LocalDateTime fechaApertura,
        TipoCupon tipoCupon,
        EstadoCupon estadoCupon
) {}