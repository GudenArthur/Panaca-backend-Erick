package Panaca.controllers;

import Panaca.dto.PQR.CrearPQRDTO;
import Panaca.dto.PQR.ItemPQRDTO;
import Panaca.dto.autenticacion.MensajeDTO;
import Panaca.dto.carrito.DetalleCarritoDTO;
import Panaca.dto.carrito.InformacionEventoCarritoDTO;
import Panaca.dto.cuenta.EditarCuentaDTO;
import Panaca.dto.cuenta.InformacionCuentaDTO;
import Panaca.dto.devolucion.DevolucionRequestDTO;
import Panaca.dto.devolucion.DevolucionResponseDTO;
import Panaca.dto.donation.DonationDTO;
import Panaca.dto.orden.EditarOrdenDTO;
import Panaca.dto.orden.MostrarOrdenDTO;
import Panaca.exceptions.*;
import Panaca.model.documents.Carrito;
import Panaca.model.documents.PQR;
import Panaca.service.service.*;
import Panaca.configs.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import com.mercadopago.resources.preference.Preference;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cuenta")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CuentaController {

    @Autowired
    CuentaService cuentaService;
    @Autowired
    Panaca.service.service.PQRService PQRService;
    @Autowired
    CuponService cuponService;
    @Autowired
    CarritoService carritoService;
    @Autowired
    OrdenService ordenService;
    @Autowired
    DonationService donationService;
    @Autowired
    DevolucionService devolucionService;

//==================================== METODOS PERFIL =============================================//

    @PreAuthorize("hasRole('CLIENTE')")
    @PutMapping("/editar-perfil")
    public ResponseEntity<MensajeDTO<String>> editarCuenta(@Valid @RequestBody EditarCuentaDTO cuenta) throws CuentaException {
        cuentaService.editarCuenta(cuenta);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta editada exitosamente"));
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @DeleteMapping("/eliminar-cuenta")
    public ResponseEntity<MensajeDTO<String>> eliminarCuenta(Authentication authentication) throws CuentaException {
        String id = AuthUtils.obtenerIdUsuarioDesdeToken(authentication);
        cuentaService.eliminarCuenta(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta eliminada exitosamente"));
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/obtener-informacion")
    public ResponseEntity<MensajeDTO<InformacionCuentaDTO>> obtenerInformacionCuenta(Authentication authentication) throws CuentaException {
        String id = AuthUtils.obtenerIdUsuarioDesdeToken(authentication);
        InformacionCuentaDTO info = cuentaService.obtenerInformacionCuenta(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, info));
    }

//==================================== METODOS PQR =============================================//

    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/crear-pqr")
    public ResponseEntity<MensajeDTO<String>> crearPQR(@Valid @RequestBody CrearPQRDTO crearPQRDTO) throws PQRException {
        PQRService.crearPQR(crearPQRDTO);
        return ResponseEntity.ok(new MensajeDTO<>(true, "PQR creada exitosamente"));
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/listar-pqr")
    public ResponseEntity<MensajeDTO<List<ItemPQRDTO>>> listarPQRs() throws PQRException {
        List<ItemPQRDTO> pqrList = PQRService.listarPQRs();
        return ResponseEntity.ok(new MensajeDTO<>(true, pqrList));
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/obtener-pqr-usuario")
    public ResponseEntity<MensajeDTO<List<PQR>>> obtenerPQRsPorUsuario(Authentication authentication) throws PQRException {
        String id = AuthUtils.obtenerIdUsuarioDesdeToken(authentication);
        List<PQR> pqrsObtenidos = PQRService.obtenerPQRsPorUsuario(id);
        return ResponseEntity.ok(new MensajeDTO<>(true, pqrsObtenidos));
    }

//==================================== METODOS CARRITO =============================================//

    @PreAuthorize("hasRole('CLIENTE')")
    @PutMapping("/agregar-items-carrito")
    public ResponseEntity<MensajeDTO<String>> agregarItemsAlCarrito(Authentication authentication, @Valid @RequestBody List<DetalleCarritoDTO> itemsCarritoDTO) throws CarritoException {
        String idUsuario = AuthUtils.obtenerIdUsuarioDesdeToken(authentication);
        carritoService.agregarItemsAlCarrito(idUsuario, itemsCarritoDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Items agregados exitosamente al carrito."));
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/obtener-carrito")
    public ResponseEntity<MensajeDTO<Carrito>> obtenerCarrito(Authentication authentication) throws CarritoException {
        String idUsuario = AuthUtils.obtenerIdUsuarioDesdeToken(authentication);
        Carrito carrito = carritoService.obtenerCarritoPorUsuario(idUsuario);
        return ResponseEntity.ok(new MensajeDTO<>(false, carrito));
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @DeleteMapping("/eliminar-item-carrito/{idEvento}/{fechaUso}")
    public ResponseEntity<MensajeDTO<String>> eliminarItemDelCarrito(
            Authentication authentication,
            @PathVariable String idEvento,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaUso
    ) throws CarritoException {
        String idUsuario = AuthUtils.obtenerIdUsuarioDesdeToken(authentication);
        carritoService.eliminarItemDelCarrito(idUsuario, idEvento, fechaUso);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Item eliminado del carrito exitosamente."));
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @DeleteMapping("/vaciar-carrito")
    public ResponseEntity<MensajeDTO<String>> vaciarCarrito(Authentication authentication) throws CarritoException {
        String idUsuario = AuthUtils.obtenerIdUsuarioDesdeToken(authentication);
        carritoService.vaciarCarrito(idUsuario);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Carrito vaciado exitosamente."));
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/listar-productos-carrito")
    public ResponseEntity<MensajeDTO<List<InformacionEventoCarritoDTO>>> listarProductosEnCarrito(Authentication authentication) throws CarritoException {
        String idUsuario = AuthUtils.obtenerIdUsuarioDesdeToken(authentication);
        List<InformacionEventoCarritoDTO> itemsCarrito = carritoService.listarProductosEnCarrito(idUsuario);
        return ResponseEntity.ok(new MensajeDTO<>(false, itemsCarrito));
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/total-carrito")
    public ResponseEntity<MensajeDTO<Double>> calcularTotalCarrito(Authentication authentication) throws CarritoException {
        String idUsuario = AuthUtils.obtenerIdUsuarioDesdeToken(authentication);
        double total = carritoService.calcularTotalCarrito(idUsuario);
        return ResponseEntity.ok(new MensajeDTO<>(false, total));
    }


//==================================== METODOS ORDEN =============================================//

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/mostrar-orden/{idOrden}")
    public ResponseEntity<MensajeDTO<MostrarOrdenDTO>> mostrarOrden(@PathVariable String idOrden) throws CuentaException, OrdenException {
        MostrarOrdenDTO ordenDTO = ordenService.mostrarOrden(idOrden);
        return ResponseEntity.ok(new MensajeDTO<>(false, ordenDTO));
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @PutMapping("/actualizar-orden/{id}")
    public ResponseEntity<MensajeDTO<String>> actualizarOrden(@PathVariable String id, @RequestBody @Valid EditarOrdenDTO ordenDTO) throws OrdenException, CuponException {
        ordenService.actualizarOrden(id, ordenDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Orden actualizada exitosamente."));
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @DeleteMapping("/eliminar-orden/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarOrden(@PathVariable String id) throws OrdenException {
        ordenService.eliminarOrden(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Orden eliminada exitosamente."));
    }

//==================================== METODOS PAGO =============================================//

    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/realizar-pago")
    public ResponseEntity<MensajeDTO<Preference>> realizarPago(@RequestParam("idOrden") String idOrden) throws Exception {
        Preference preference = ordenService.realizarPago(idOrden);
        return ResponseEntity.ok(new MensajeDTO<>(false, preference));
    }

    // Este endpoint no lleva seguridad, lo llama MercadoPago externamente
    @PostMapping("/notificacion-pago")
    public void recibirNotificacionMercadoPago(@RequestBody Map<String, Object> requestBody) {
        ordenService.recibirNotificacionMercadoPago(requestBody);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/crear-orden")
    public ResponseEntity<MensajeDTO<String>> crearOrdenDesdeCarrito(
            Authentication authentication,
            @RequestParam(required = false) String idCupon,
            @RequestParam(required = false) String codigoPasarela
    ) {
        try {
            String idCliente = AuthUtils.obtenerIdUsuarioDesdeToken(authentication);
            ordenService.crearOrdenDesdeCarrito(idCliente, idCupon, codigoPasarela);
            return ResponseEntity.ok(new MensajeDTO<>(false, "Orden creada exitosamente desde el carrito."));
        } catch (OrdenException | CarritoException | CuponException e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    //==================================== METODOS DONACIONES =============================================//

    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/donaciones-crear")
    public ResponseEntity<MensajeDTO<String>> donar(@Valid @RequestBody DonationDTO dto) {
        donationService.crearDonacion(dto);
        return ResponseEntity.ok(new MensajeDTO<>(true, "Donación registrada exitosamente"));
    }

    //==================================== METODOS DEVOLUCIONES =============================================//

    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/devoluciones-solicitar")
    public ResponseEntity<MensajeDTO<DevolucionResponseDTO>> solicitarDevolucion(
            @Valid @RequestBody DevolucionRequestDTO dto) {
        DevolucionResponseDTO response = devolucionService.solicitar(dto);
        return ResponseEntity.ok(new MensajeDTO<>(true, response));
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/devoluciones-historial")
    public ResponseEntity<MensajeDTO<List<DevolucionResponseDTO>>> listarDevolucionesUsuario(
            Authentication authentication) {
        String cuentaId = AuthUtils.obtenerIdUsuarioDesdeToken(authentication);
        List<DevolucionResponseDTO> devoluciones = devolucionService.listarPorUsuario(cuentaId);
        return ResponseEntity.ok(new MensajeDTO<>(true, devoluciones));
    }


}