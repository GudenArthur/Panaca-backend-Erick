package Panaca.service.service;

import Panaca.dto.orden.CrearOrdenDTO;
import Panaca.dto.orden.EditarOrdenDTO;
import Panaca.dto.orden.MostrarOrdenDTO;
import Panaca.model.documents.Orden;
import Panaca.exceptions.CarritoException;
import Panaca.exceptions.CuentaException;
import Panaca.exceptions.CuponException;
import Panaca.exceptions.OrdenException;

import java.util.Map;
import com.mercadopago.resources.preference.Preference;
import jakarta.validation.Valid;

public interface OrdenService {

    void crearOrdenDesdeCarrito(String idCliente, String codigoCupon, String codigoPasarela) throws OrdenException, CarritoException, CuponException;

    void crearOrden(CrearOrdenDTO ordenDTO, double totalOrden) throws OrdenException;

    MostrarOrdenDTO mostrarOrden(String idOrden) throws OrdenException, CuentaException;

    Orden obtenerOrdenPorId(String ordenId) throws OrdenException;

    void actualizarOrden (String id,@Valid EditarOrdenDTO ordenDTO) throws OrdenException, CuponException;

    void eliminarOrden(String id) throws OrdenException;

    Preference realizarPago(String idOrden) throws Exception;

    void recibirNotificacionMercadoPago(Map<String, Object> request);
}
