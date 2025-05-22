package Panaca.service.service;

import Panaca.dto.cupon.CrearCuponDTO;
import Panaca.dto.cupon.EditarCuponDTO;
import Panaca.dto.cupon.InformacionCuponDTO;
import Panaca.dto.cupon.ItemsCuponDTO;
import Panaca.model.documents.Cupon;
import Panaca.dto.cupon.*;
import Panaca.exceptions.CuponException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface CuponService {

    void crearCupon(CrearCuponDTO cupon) throws CuponException;

    void editarCupon(EditarCuponDTO cupon, String cuponId) throws CuponException;

    void eliminarCupon(String id) throws CuponException;

    InformacionCuponDTO obtenerInformacionCupon(String id) throws CuponException;

    Page<Cupon> getAllDisponibles(PageRequest pageRequest);

    Page<Cupon> getAllNoDisponibles(PageRequest pageRequest);

    List<ItemsCuponDTO> obtenerCuponesFiltrados(ItemsCuponFiltroDTO filtro);

    Cupon aplicarCupon(String codigoCupon, LocalDateTime fechaCompra) throws CuponException;

}