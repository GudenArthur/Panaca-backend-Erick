package Panaca.dto.PQR;

import Panaca.model.enums.EstadoPQR;

import java.time.LocalDateTime;

public record ItemPQRDTO(
        String id,
        String idUsuario,
        EstadoPQR estadoPQR,
        LocalDateTime fechaCreacion
) {}
