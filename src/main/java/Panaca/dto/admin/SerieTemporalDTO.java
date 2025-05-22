package Panaca.dto.admin;

public record SerieTemporalDTO(
    String fecha,    // yyyy-MM-dd
    long count,      // número de eventos (ó órdenes ó donaciones)
    double monto     // total ingresado ese día
) {}
