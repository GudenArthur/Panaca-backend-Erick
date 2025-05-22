package Panaca.dto.evento;

import Panaca.model.enums.TipoEvento;

public record ItemEventoDTO(
        String id,
        String nombre,
        String descripcion,
        String imagenPortada,
        TipoEvento tipoEvento,
        Float precio

) {}