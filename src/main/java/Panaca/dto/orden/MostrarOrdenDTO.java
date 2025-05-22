package Panaca.dto.orden;

import java.time.LocalDate;
import java.util.List;

public record MostrarOrdenDTO(
        String nombreUsuario,
        LocalDate fechaCompra,
        String cupon,
        Double total,
        List<MostrarDetalleOrdenDTO> detalles
) {}