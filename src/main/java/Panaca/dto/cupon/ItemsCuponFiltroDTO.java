package Panaca.dto.cupon;

import Panaca.model.enums.TipoCupon;
import Panaca.model.enums.EstadoCupon;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ItemsCuponFiltroDTO(
        String nombre,
        LocalDateTime fechaVencimiento,
        LocalDateTime fechaApertura,
        Float descuento,
        TipoCupon tipo,
        EstadoCupon estado
) {}
