package Panaca.dto.PQR;

import Panaca.model.enums.CategoriaPQR;
import Panaca.model.enums.EstadoPQR;

import java.time.LocalDateTime;

public record ResumenPQRDTO(
        String id,
        EstadoPQR estadoPQR,
        CategoriaPQR categoriaPQR,
        String descripcion,
        LocalDateTime fechaCreacion
) {}
