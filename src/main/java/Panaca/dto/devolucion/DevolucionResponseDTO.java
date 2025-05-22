package Panaca.dto.devolucion;

import Panaca.model.enums.EstadoDevolucion;
import Panaca.model.enums.TipoDevolucion;
import java.time.LocalDateTime;

public record DevolucionResponseDTO(

        String id,
        TipoDevolucion tipo,
        String referenciaId,
        LocalDateTime fechaSolicitud,
        EstadoDevolucion estado

) {}