package Panaca.dto.carrito;

public record InformacionEventoCarritoDTO(
        DetalleCarritoDTO detalleCarritoDTO,
        String imagenPortada,
        String nombre
) {}