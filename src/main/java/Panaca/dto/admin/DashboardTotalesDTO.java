package Panaca.dto.admin;

public record DashboardTotalesDTO(
    long totalOrdenes,
    double ingresosTotales,
    long totalDonaciones,
    int montoDonaciones,
    long nuevosUsuarios
) {}
