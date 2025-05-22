package Panaca.service.service;

import Panaca.dto.carrito.CrearCarritoDTO;
import Panaca.dto.carrito.DetalleCarritoDTO;
import Panaca.dto.carrito.InformacionEventoCarritoDTO;
import Panaca.model.documents.Carrito;
import Panaca.exceptions.CarritoException;

import java.time.LocalDate;
import java.util.List;

public interface CarritoService {

    void crearCarrito(CrearCarritoDTO carritoDTO) throws CarritoException;

    Carrito obtenerCarritoPorUsuario(String idUsuario) throws CarritoException;

    Carrito agregarItemsAlCarrito(String idUsuario, List<DetalleCarritoDTO> nuevosItemsDTO) throws CarritoException;

    Carrito eliminarItemDelCarrito(String idUsuario, String idEvento, LocalDate fechaUso) throws CarritoException;

    Carrito vaciarCarrito(String idUsuario) throws CarritoException;

    List<InformacionEventoCarritoDTO> listarProductosEnCarrito(String idUsuario) throws CarritoException;

    double calcularTotalCarrito(String idUsuario) throws CarritoException;

}
