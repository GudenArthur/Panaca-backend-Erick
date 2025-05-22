package Panaca.dto.PQR;

import Panaca.model.enums.CategoriaPQR;
import Panaca.model.enums.EstadoPQR;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public record MostrarPQRDTO(
        String id,
        ObjectId idUsuario,
        LocalDateTime fechaCreacion,
        EstadoPQR estadoPQR,
        CategoriaPQR categoriaPQR,
        String descripcion,
        String respuesta,
        LocalDateTime fechaRespuesta
) {}
