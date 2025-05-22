package Panaca.dto.evento;

import Panaca.model.enums.EstadoEvento;
import Panaca.model.enums.TipoEvento;

public record EventoFiltradoDTO(
        String nombre,
        TipoEvento tipoEvento,
        EstadoEvento estadoEvento
) {}