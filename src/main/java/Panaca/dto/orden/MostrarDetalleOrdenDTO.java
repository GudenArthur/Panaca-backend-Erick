package Panaca.dto.orden;

import Panaca.model.enums.TipoEvento;

import java.time.LocalDate;

public record MostrarDetalleOrdenDTO(
        String idEvento,
        String nombreEvento,
        TipoEvento tipoEvento,
        LocalDate fechaUso,
        Float precio,
        int cantidad
) {}