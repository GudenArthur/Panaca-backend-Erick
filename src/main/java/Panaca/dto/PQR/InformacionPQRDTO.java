package Panaca.dto.PQR;

import Panaca.model.enums.CategoriaPQR;
import Panaca.model.enums.EstadoPQR;

import java.time.LocalDateTime;

public record InformacionPQRDTO(
        String id,
        String nombreUsuario,
        String telefonoUsuario,
        String emailUsuario,
        LocalDateTime fechaCreacion,
        EstadoPQR estadoPQR,
        CategoriaPQR categoriaPQR,
        String descripcion,
        String respuesta,
        LocalDateTime fechaRespuesta
) {}