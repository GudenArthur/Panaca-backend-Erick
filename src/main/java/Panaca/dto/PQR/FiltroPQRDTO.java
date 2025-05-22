package Panaca.dto.PQR;

import Panaca.model.enums.CategoriaPQR;
import Panaca.model.enums.EstadoPQR;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public record FiltroPQRDTO(
        EstadoPQR estadoPQR,
        CategoriaPQR categoriaPQR,
        ObjectId idUsuario,
        LocalDateTime fechaDesde,
        LocalDateTime fechaHasta
) {}