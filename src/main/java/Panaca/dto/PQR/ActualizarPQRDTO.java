package Panaca.dto.PQR;

import Panaca.model.enums.EstadoPQR;

import java.time.LocalDateTime;

public record ActualizarPQRDTO(
        String id,
        EstadoPQR estadoPQR,
        String respuesta,
        LocalDateTime fechaRespuesta
) {}